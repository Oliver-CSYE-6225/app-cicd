// package com.csye6225.webapp.service;

// import com.csye6225.webapp.entity.User;
// import com.csye6225.webapp.repository.ReadUserRepository;
// import com.csye6225.webapp.repository.UserRepository;

// import javassist.NotFoundException;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.stereotype.Service;

// import java.util.Base64;
// import java.util.Optional;

// @Service
// public class AuthenticationService {

//     @Autowired
//     UserRepository readUserRepository;

//     private static final Logger LOGGER=LoggerFactory.getLogger(AuthenticationService.class);

//     // public boolean authenticateUser(String[] tokens) {
//     //             BCryptPasswordEncoder b = new BCryptPasswordEncoder(12);
//     //             // Optional<User> user = readUserRepository.findByUserName(tokens[0]);
        
//     //             // try{
//     //             //     user.orElseThrow(() -> new NotFoundException("Not found: " + tokens[0]));
//     //             // }catch(Exception e){
//     //             //     System.out.println("User" +  user);
//     //             // } 
//     //             // User u = user.get();               
//     //             String storedPassword = readUserRepository.findUserPassword(tokens[0]);
//     //             // String storedPassword = u.getPassword();
//     //             // boolean isVerified = u.getVerified();

//     //             LOGGER.info("stored password for user: " + storedPassword);
//     //             if(storedPassword != null && b.matches(tokens[1], storedPassword)) {
//     //                 return true;
//     //             }
//     //             return false;
//     // }

//     public boolean authenticateUser(String[] tokens) {
//         BCryptPasswordEncoder b = new BCryptPasswordEncoder(12);
//         String storedPassword = readUserRepository.findUserPassword(tokens[0]);
//         if(storedPassword != null && b.matches(tokens[1], storedPassword)) {
//             return true;
//         }
//         return false;
// }



//     public String decodeBasicAuthToken(String authorization){
//         if (authorization != null && authorization.toLowerCase().startsWith("basic")) {
//             String base64Credentials = authorization.substring("Basic".length()).trim();
//             byte[] decodedBytes = Base64.getDecoder().decode(base64Credentials);
//             return new String(decodedBytes);
//         }

//         return null;
//     }
// }

package com.csye6225.webapp.service;

import com.csye6225.webapp.config.ReadOnlyRepository;
import com.csye6225.webapp.entity.User;
import com.csye6225.webapp.repository.ReadUserRepository;
import com.csye6225.webapp.repository.UserRepository;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Optional;

@Service
public class AuthenticationService {

    @Autowired
    UserService userService;

    public boolean authenticateUser(String[] tokens) {
                BCryptPasswordEncoder b = new BCryptPasswordEncoder(12);
                // String storedPassword = userRepository.findUserPassword(tokens[0]);
                User u = userService.getUser(tokens[0]);
                if(u!= null && u.getPassword() != null && b.matches(tokens[1], u.getPassword())) {
                    return true;
                }
                return false;
    }




    public String decodeBasicAuthToken(String authorization){
        if (authorization != null && authorization.toLowerCase().startsWith("basic")) {
            String base64Credentials = authorization.substring("Basic".length()).trim();
            byte[] decodedBytes = Base64.getDecoder().decode(base64Credentials);
            return new String(decodedBytes);
        }

        return null;
    }
}