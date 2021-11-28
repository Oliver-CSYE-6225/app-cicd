package com.csye6225.webapp.service;

import com.csye6225.webapp.entity.User;
import com.csye6225.webapp.repository.UserRepository;
import javassist.NotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    UserRepository userRepository;

    private static final Logger LOGGER=LoggerFactory.getLogger(AuthenticationService.class);

    public boolean authenticateUser(String[] tokens) {
                BCryptPasswordEncoder b = new BCryptPasswordEncoder(12);
                String storedPassword = userRepository.findUserPassword(tokens[0]);
                LOGGER.info("stored password for user: " + storedPassword);
                if(storedPassword != null && b.matches(tokens[1], storedPassword)) {
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
