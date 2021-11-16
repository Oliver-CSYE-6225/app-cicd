package com.csye6225.webapp.controller;

import com.csye6225.webapp.entity.User;
import com.csye6225.webapp.repository.UserRepository;
import com.csye6225.webapp.service.AuthenticationService;
import com.csye6225.webapp.service.CommonUtilsService;
import com.csye6225.webapp.service.UserService;
import com.csye6225.webapp.service.ValidationService;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    ValidationService validationService;

    @Autowired
    CommonUtilsService commonUtilsService;

    @Autowired
    StatsDClient statsd;
    // private static final StatsDClient statsd = new NonBlockingStatsDClient("my.prefix", "localhost", 8125);

    @GetMapping(path = "/", produces = "application/json")
    public ResponseEntity<String> rootURl(@RequestHeader HttpHeaders headers) {
        
        return ResponseEntity.ok().body("");
    }

    @GetMapping(path = "/v2/user/self", produces = "application/json")
    public ResponseEntity<String> getUser(@RequestHeader HttpHeaders headers) {
        statsd.incrementCounter("bar");
        statsd.recordGaugeValue("baz", 100);
        statsd.recordExecutionTime("bag", 25);
        statsd.recordSetEvent("qux", "one");
        String authorization = headers.getFirst("Authorization");
        String decodedTokenString = authenticationService.decodeBasicAuthToken(authorization);
        String[] tokens = new String[2];

        if(decodedTokenString != null){
            if(decodedTokenString.split(":").length == 2) {
                tokens = decodedTokenString.split(":", 2);
            }
            if(!authenticationService.authenticateUser(tokens)){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new JSONObject().put("message","Authorization Refused for the credentials provided.").toString());
            }
        } else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new JSONObject().put("message","Authorization Refused for the credentials provided.").toString());
        }

        User userObj =  userService.getUser(tokens[0]);
        return ResponseEntity.ok().body(commonUtilsService.getUserAsJSON(userObj).toString());
    }

    @PostMapping(path = "/v2/user", produces = "application/json")
    public ResponseEntity<String> postUser(@RequestHeader HttpHeaders headers, @RequestBody String reqBody) {
        JSONObject reqObj = new JSONObject(reqBody);
        String errorString = validationService.validateSaveObject(reqObj);
        if(!errorString.equals("")) {
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

        try{
            userService.saveUser(u);
        } catch(Exception e) {
            JSONObject resObj = new JSONObject();
            if(e.getMessage().contains("constraint [usertable_username_key]")){
                resObj.put("message", "Username already exists.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resObj.toString());
            } else{
                resObj.put("message", "Unable to Save User Information. Please try again.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resObj.toString());
            }
        }

        u =  userService.getUser(reqObj.getString("username"));

        return ResponseEntity.status(HttpStatus.CREATED).body(commonUtilsService.getUserAsJSON(u).toString());
    }


    @PutMapping(path = "/v2/user/self", produces = "application/json")
    public ResponseEntity<String> putUser(@RequestHeader HttpHeaders headers, @RequestBody String reqBody) {
        String authorization = headers.getFirst("Authorization");
        String decodedTokenString = authenticationService.decodeBasicAuthToken(authorization);
        String[] tokens = new String[2];

        if(decodedTokenString != null){
            if(decodedTokenString.split(":").length == 2) {
                tokens = decodedTokenString.split(":", 2);
            }
            if(!authenticationService.authenticateUser(tokens)){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new JSONObject().put("message","Authorization Refused for the credentials provided.").toString());
            }
        } else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new JSONObject().put("message","Authorization Refused for the credentials provided.").toString());
        }

        JSONObject updateObj = new JSONObject(reqBody);
        //Validate update object to check for unwanted fields and data type
        String errorString = validationService.validateModifyObject(updateObj);
        if(!errorString.equals("")){
            JSONObject respObj = new JSONObject();
            respObj.put("message", errorString);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respObj.toString());
        }

        if(!tokens[0].equals(updateObj.getString("username"))){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new JSONObject().put("message",
                           "Incorrect Authorization credentials for " +
                                   updateObj.getString("username")).toString());
        }



        User u = userService.getUser(updateObj.getString("username"));
        try{
            if(updateObj.has("first_name")){
                u.setFirst_name(updateObj.getString("first_name"));
            }
            if(updateObj.has("last_name")){
                u.setLast_name(updateObj.getString("last_name"));
            }
            if(updateObj.has("password")){
                String password = updateObj.getString("password");
                BCryptPasswordEncoder b = new BCryptPasswordEncoder(12);
                updateObj.put("password", b.encode(password));
                u.setPassword(updateObj.getString("password"));
            }
            User savedUser = userService.saveUser(u);

        } catch(Exception e) {
            e.printStackTrace();
            JSONObject resObj = new JSONObject();
            resObj.put("statusCode", HttpStatus.INTERNAL_SERVER_ERROR);
            resObj.put("message", "Unable to Update User Information. Please try again.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resObj.toString());
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(updateObj.toString());
    }

}
