package com.csye6225.webapp.controller;


import com.amazonaws.util.IOUtils;
import com.csye6225.webapp.entity.Image;
import com.csye6225.webapp.entity.User;
import com.csye6225.webapp.service.*;
import javassist.NotFoundException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.timgroup.statsd.StatsDClient;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

@RestController
public class ImageController {

    @Autowired
    private ImageStoreService imageStoreService;
    @Autowired
    private ImageService imageService;

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    UserService userService;

    @Autowired
    CommonUtilsService commonUtilsService;

    @Autowired
    StatsDClient statsd;
    // private static final StatsDClient statsd = new NonBlockingStatsDClient("my.prefix", "localhost", 8125);

    private static final Logger LOGGER=LoggerFactory.getLogger(UserController.class);


    @PostMapping(path = "/v1/user/self/pic", produces = "application/json")
    public ResponseEntity<String> uploadFile(@RequestHeader HttpHeaders headers, HttpServletRequest request) {
        statsd.incrementCounter("Upload Image /v1/user/self/pic");
        LOGGER.info("Starting uploading User pic");
        long startTime = System.currentTimeMillis();
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



        String contentType = request.getContentType();
        byte[] pictureBA = new byte[0];
        if(!contentType.contains("image/")){
            LOGGER.error("File uploaded is not an image");
            return new ResponseEntity<String>("File should be Image", HttpStatus.BAD_REQUEST);
        } else {
            InputStream pictureIS = null;
            try {
                pictureIS = request.getInputStream();
                pictureBA = IOUtils.toByteArray(pictureIS);
            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.error("Image file uploaded is corrupted");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new JSONObject().put("message","Image file is corrupted").toString());
            }
        }
        String name = "file.txt";
        String originalFileName = "file.txt";
        String userHeader = request.getHeader("Authorization");
        MultipartFile file = new MockMultipartFile(
                name,
                originalFileName,
                contentType,
                pictureBA
        );

        Image i = null;
        JSONObject resp = null;
        User u = userService.getUser(tokens[0]);
        try{
            System.out.println(u.getId());
            i = imageService.getImageMetaData(u.getId());
            imageStoreService.deleteFile(i.getFile_name());
            resp = imageStoreService.uploadFile(file, tokens[0]);
            i.setFile_name(resp.getString("filename"));
            i.setUrl(resp.getString("url"));
            imageService.saveImageMetaData(i);
            statsd.recordExecutionTime("Post Image Execution Time", System.currentTimeMillis()-startTime);
            return ResponseEntity.status(HttpStatus.CREATED).body(commonUtilsService.getImageAsJSON(i).toString());
        } catch(NotFoundException e){
            resp = imageStoreService.uploadFile(file, tokens[0]);
            i = new Image();
            i.setId(UUID.randomUUID());
            i.setFile_name(resp.getString("filename"));
            i.setUrl(resp.getString("url"));
            i.setUser_id(u.getId());
            i.setUpload_date(new Date());
            imageService.saveImageMetaData(i);
            statsd.recordExecutionTime("Post Image Execution Time", System.currentTimeMillis() - startTime);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(commonUtilsService.getImageAsJSON(i).toString());
        }
    }

    @GetMapping(path = "/v1/user/self/pic", produces = "application/json")
    public ResponseEntity<String> downloadFile(@RequestHeader HttpHeaders headers) {
        statsd.incrementCounter("Get Image /v1/user/self/pic");
        LOGGER.info("Get Image Metadata called");
        long startTime = System.currentTimeMillis();
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

        User u =  userService.getUser(tokens[0]);
        try{
            System.out.println(u.getId());
            Image i = imageService.getImageMetaData(u.getId());
            statsd.recordExecutionTime("Get Image Execution Time", System.currentTimeMillis() - startTime);
            return ResponseEntity.status(HttpStatus.CREATED).body(commonUtilsService.getImageAsJSON(i).toString());
        } catch(NotFoundException e){
            statsd.recordExecutionTime("Get Image Execution Time", System.currentTimeMillis() - startTime);
            LOGGER.error("No profile pic exists for image uploaded");
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new JSONObject().put("message","No Profile Pic exists for " + u.getUsername()).toString());
        }
    }

    @DeleteMapping(path ="/v1/user/self/pic")
    public ResponseEntity<String> deleteFile(@RequestHeader HttpHeaders headers) {
        statsd.incrementCounter("Delete Image /v1/user/self/pic");
        LOGGER.info("Delete image metadata called");
        long startTime = System.currentTimeMillis();
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
        User u = userService.getUser(tokens[0]);

        try{
            System.out.println(u.getId());
            Image image = imageService.getImageMetaData(u.getId());
            imageStoreService.deleteFile(image.getFile_name());
            imageService.deleteImageMetaData(image);
            statsd.recordExecutionTime("Delete Image Execution Time", System.currentTimeMillis() - startTime);
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body("");
        } catch(NotFoundException e){
            statsd.recordExecutionTime("Delete Image Execution Time", System.currentTimeMillis() - startTime);
            LOGGER.info("Image already deleted");
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new JSONObject().put("message","No Profile Pic exists for " + u.getUsername()).toString());
        }

    }
}
