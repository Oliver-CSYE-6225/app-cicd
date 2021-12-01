package com.csye6225.webapp.service;

import com.csye6225.webapp.entity.User;
import com.csye6225.webapp.repository.ReadUserRepository;
import com.csye6225.webapp.repository.UserRepository;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.timgroup.statsd.StatsDClient;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    ReadUserRepository readUserRepository;

    @Autowired
    StatsDClient statsd;

    public User getUser(String userName) {
        long startTime = System.currentTimeMillis();
        Optional<User> user = readUserRepository.findByUserName(userName);
        statsd.recordExecutionTime("Fetch User  Execution Time", startTime -  System.currentTimeMillis());

        try{
            user.orElseThrow(() -> new NotFoundException("Not found: " + userName));
        }catch(Exception e){
            System.out.println("User" +  user);
        }
        System.out.println("My user" +  user);
        return user.get();
    }

    public User saveUser(User user) {
        long startTime = System.currentTimeMillis();
        User foo = userRepository.saveAndFlush(user);
        statsd.recordExecutionTime("Save/Update User  Execution Time", startTime -  System.currentTimeMillis());
        return foo;
    }


}
