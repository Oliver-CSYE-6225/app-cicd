package com.csye6225.webapp.service;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Iterator;

@Service
public class ValidationService {
    String[] updateFields = {"first_name", "last_name", "password", "username"};

    public String validateModifyObject(JSONObject reqObj){
        String errorString = "";
        if(!reqObj.has("username") || !validateUsernameFormat(reqObj.getString("username"))){
            errorString = "Username can only be a valid email address";
            return errorString;
        }
        Iterator<String> keys = reqObj.keys();
        while(keys.hasNext()) {
            String key = keys.next();
            boolean contains = Arrays.stream(updateFields).anyMatch(key::equals);
            if (!contains) {
                errorString = "Only following User fields are allowed for modification: {first_name, last_name, password, username}";
                return errorString;
            }
            if (!(reqObj.get(key) instanceof String)) {
                return errorString = key + " can only be a String type";
            }
        }

        return errorString;
    }

    public String validateSaveObject(JSONObject reqObj){
        String errorString = "";

        for(String field : updateFields) {
            if (!reqObj.has(field)) {
                errorString = "Missing field " + field + ". Following fields are mandatory: {first_name, last_name, password, username}";
                return errorString;
            }
            if (!(reqObj.get(field) instanceof String)) {
                return errorString = field + " can only be a String type";
            }
        }

        if(!validateUsernameFormat(reqObj.getString("username"))){
            errorString = "Username can only be a valid email address";
            return errorString;
        }
        return errorString;
    }

    public boolean validateUsernameFormat(String username){
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return username.matches(regex);
    }
}
