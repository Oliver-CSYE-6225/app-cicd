package com.csye6225.webapp.controller;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.csye6225.webapp.entity.User;
import com.csye6225.webapp.service.AuthenticationService;
import com.csye6225.webapp.service.CommonUtilsService;
import com.csye6225.webapp.service.UserService;
import com.csye6225.webapp.service.ValidationService;

import com.timgroup.statsd.StatsDClient;

import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    ValidationService validationService;

    @Autowired
    CommonUtilsService commonUtilsService;

    @Autowired
    private AmazonSNSClient snsCLient;

    @Autowired
    private AmazonDynamoDB dynamoClient;

    @Autowired
    StatsDClient statsd;
    // private static final StatsDClient statsd = new
    // NonBlockingStatsDClient("my.prefix", "localhost", 8125);

    @Value("${application.sns.arn}")
    private String snsTopic;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @GetMapping(path = "/", produces = "application/json")
    public ResponseEntity<String> rootURl(@RequestHeader HttpHeaders headers) {

        return ResponseEntity.ok().body("");
    }

    @GetMapping(path = "/v1/verifyUserEmail", produces = "application/json")
    public ResponseEntity<String> verifyUserEmail(@RequestHeader HttpHeaders headers,
            @RequestParam("email") String email, @RequestParam("token") String token) {
        Map<String, AttributeValue> map = new HashMap<>();
        map.put("EmailId", new AttributeValue(email));
        GetItemResult g = null;
        try {
            g = dynamoClient.getItem("Email-Tokens", map);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            LOGGER.error("" + e.getStackTrace());
        }
        if (g != null) {
            LOGGER.info("My item dynamo: " + g.getItem());
            Map<String, AttributeValue> attrMap = g.getItem();
            AttributeValue tokenAttr = attrMap.get("Token");
            AttributeValue timeToExist = attrMap.get("TimeToExist");
            long expiryTime = Long.valueOf(timeToExist.getN()).longValue();
            if(expiryTime > System.currentTimeMillis()/1000 && tokenAttr.getS().equals(token)){
                // if(tokenAttr.getS().equals(token)){

                User u = userService.getUser(email);
                u.setVerified(true);
                u.setAccount_verified();
                userService.saveUser(u);
                return ResponseEntity.ok().body("User successfully verified");
            } else{
                LOGGER.error("Token doesn't match" + attrMap.get("Token").getS() + " " + token);
                LOGGER.info("TimeToExist" + expiryTime + " Current Time:" + System.currentTimeMillis()/1000);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User cannot be verified due to invalid verification link");
            }

        }else{
            LOGGER.error("Record does not exist in dynamo db");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The verification link has expired");
        }
    }

    @GetMapping(path = "/v1/user/self", produces = "application/json")
    public ResponseEntity<String> getUser(@RequestHeader HttpHeaders headers) {
        long startTime = System.currentTimeMillis();
        statsd.incrementCounter("Get User /v1/user/self");
        // statsd.recordGaugeValue("baz", 100);
        // statsd.recordSetEvent("qux", "one");
        LOGGER.info("Get User Called");
        LOGGER.info("This is information");
        LOGGER.debug("This is debug");
        String authorization = headers.getFirst("Authorization");
        String decodedTokenString = authenticationService.decodeBasicAuthToken(authorization);
        String[] tokens = new String[2];
        if (decodedTokenString != null) {
            if (decodedTokenString.split(":").length == 2) {
                tokens = decodedTokenString.split(":", 2);
            }
            if (!authenticationService.authenticateUser(tokens)) {
                statsd.recordExecutionTime("Get User Execution Time", System.currentTimeMillis() - startTime);
                LOGGER.error("Unauthorized User");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new JSONObject().put("message", "Authorization Refused for the credentials provided.")
                                .toString());
            }
        } else {
            statsd.recordExecutionTime("Get User Execution Time", System.currentTimeMillis() - startTime);
            LOGGER.error("Unauthorized User");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new JSONObject().put("message", "Authorization Refused for the credentials provided.")
                            .toString());
        }
        User userObj = userService.getUser(tokens[0]);
        LOGGER.info("Get User Execution time " +  (System.currentTimeMillis() - startTime));
        statsd.recordExecutionTime("Get User Execution Time", System.currentTimeMillis() - startTime);
        return ResponseEntity.ok().body(commonUtilsService.getUserAsJSON(userObj).toString());
    }

    @PostMapping(path = "/v1/user", produces = "application/json")
    public ResponseEntity<String> postUser(@RequestHeader HttpHeaders headers, @RequestBody String reqBody) {
        LOGGER.info("Post User Called");
        long startTime = System.currentTimeMillis();
        JSONObject reqObj = new JSONObject(reqBody);
        String errorString = validationService.validateSaveObject(reqObj);
        if (!errorString.equals("")) {
            JSONObject respObj = new JSONObject();
            respObj.put("message", errorString);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respObj.toString());
        }
        User u = new User();
        String first_name = reqObj.getString("first_name");
        String last_name = reqObj.getString("last_name");
        String user_name = reqObj.getString("username");
        String password = reqObj.getString("password");
        BCryptPasswordEncoder b = new BCryptPasswordEncoder(12);
        u.setPassword(b.encode(password));
        u.setId(UUID.randomUUID());
        u.setFirst_name(first_name);
        u.setLast_name(last_name);
        u.setUsername(user_name);
        u.setVerified(false);

        try {
            userService.saveUser(u);
        } catch (Exception e) {
            JSONObject resObj = new JSONObject();
            if (e.getMessage().contains("constraint [usertable_username_key]")) {
                resObj.put("message", "Username already exists.");
                statsd.recordExecutionTime("Post User Execution Time", startTime - System.currentTimeMillis());
                LOGGER.error("Duplicate Username");
                // TODO: Remove following block after testing
                u = userService.getUser(reqObj.getString("username"));
                System.out.println("duplicate user"+u.getUsername());
                JSONObject snsMessage = new JSONObject();
                snsMessage.put("email", reqObj.getString("username"));
                snsMessage.put("token", u.getId() + "");
                snsMessage.put("message_type", "user_created");
                snsCLient.publish(snsTopic, snsMessage.toString());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resObj.toString());
            } else {
                LOGGER.error("Error in saving user Information" + e.getMessage());
                resObj.put("message", "Unable to Save User Information. Please try again.");
                statsd.recordExecutionTime("Post User Execution Time", startTime - System.currentTimeMillis());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resObj.toString());
            }
        }

        u = userService.getUser(reqObj.getString("username"));
        JSONObject snsMessage = new JSONObject();
        snsMessage.put("email", reqObj.getString("username"));
        snsMessage.put("token", u.getId() + "");
        snsMessage.put("message_type", "user_created");
        snsCLient.publish(snsTopic, snsMessage.toString());

        statsd.recordExecutionTime("Post User Execution Time", startTime - System.currentTimeMillis());
        return ResponseEntity.status(HttpStatus.CREATED).body(commonUtilsService.getUserAsJSON(u).toString());
    }

    @PutMapping(path = "/v1/user/self", produces = "application/json")
    public ResponseEntity<String> putUser(@RequestHeader HttpHeaders headers, @RequestBody String reqBody) {
        LOGGER.info("Post User Called");
        long startTime = System.currentTimeMillis();
        String authorization = headers.getFirst("Authorization");
        String decodedTokenString = authenticationService.decodeBasicAuthToken(authorization);
        String[] tokens = new String[2];

        if (decodedTokenString != null) {
            if (decodedTokenString.split(":").length == 2) {
                tokens = decodedTokenString.split(":", 2);
            }
            if (!authenticationService.authenticateUser(tokens)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new JSONObject().put("message", "Authorization Refused for the credentials provided.")
                                .toString());
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new JSONObject().put("message", "Authorization Refused for the credentials provided.")
                            .toString());
        }

        JSONObject updateObj = new JSONObject(reqBody);
        // Validate update object to check for unwanted fields and data type
        String errorString = validationService.validateModifyObject(updateObj);
        if (!errorString.equals("")) {
            JSONObject respObj = new JSONObject();
            respObj.put("message", errorString);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respObj.toString());
        }

        if (!tokens[0].equals(updateObj.getString("username"))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new JSONObject().put("message",
                            "Incorrect Authorization credentials for " +
                                    updateObj.getString("username"))
                            .toString());
        }

        User u = userService.getUser(updateObj.getString("username"));
        try {
            if (updateObj.has("first_name")) {
                u.setFirst_name(updateObj.getString("first_name"));
            }
            if (updateObj.has("last_name")) {
                u.setLast_name(updateObj.getString("last_name"));
            }
            if (updateObj.has("password")) {
                String password = updateObj.getString("password");
                BCryptPasswordEncoder b = new BCryptPasswordEncoder(12);
                updateObj.put("password", b.encode(password));
                u.setPassword(updateObj.getString("password"));
            }
            User savedUser = userService.saveUser(u);

        } catch (Exception e) {
            e.printStackTrace();
            JSONObject resObj = new JSONObject();
            resObj.put("statusCode", HttpStatus.INTERNAL_SERVER_ERROR);
            resObj.put("message", "Unable to Update User Information. Please try again.");
            statsd.recordExecutionTime("Update User Execution Time", startTime - System.currentTimeMillis());
            LOGGER.error("Unable to update user information");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resObj.toString());
        }

        statsd.recordExecutionTime("Update User Execution Time", startTime - System.currentTimeMillis());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(updateObj.toString());
    }

}
