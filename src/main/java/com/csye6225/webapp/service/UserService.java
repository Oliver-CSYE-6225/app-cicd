package com.csye6225.webapp.service;

import com.csye6225.webapp.entity.User;
import com.csye6225.webapp.repository.UserRepository;
import javassist.NotFoundException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    public User getUser(String userName) {
        Optional<User> user = userRepository.findByUserName(userName);

        try{
            user.orElseThrow(() -> new NotFoundException("Not found: " + userName));
        }catch(Exception e){
            System.out.println("User" +  user);
        }
        System.out.println("My user" +  user);
        return user.get();
    }

    public User saveUser(User user) {
        return userRepository.saveAndFlush(user);
    }


}
