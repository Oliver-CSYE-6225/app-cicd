package com.csye6225.webapp.service;

import com.csye6225.webapp.entity.Image;
import com.csye6225.webapp.entity.User;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;

@Service
public class CommonUtilsService {

    public JSONObject getUserAsJSON(User u){
        JSONObject obj = new JSONObject();
        obj.put("id", u.getId());
        obj.put("first_name", u.getFirst_name());
        obj.put("last_name", u.getLast_name());
        obj.put("username", u.getUsername());
        SimpleDateFormat s = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss'Z'");
        System.out.println("hello create" + u.getAccount_created());
        obj.put("account_created", s.format(u.getAccount_created()));
        obj.put("account_updated", s.format(u.getAccount_updated()));
        return obj;
    }

    public JSONObject getImageAsJSON(Image i){
        JSONObject obj = new JSONObject();
        obj.put("file_name", i.getFile_name());
        obj.put("id", i.getId());
        obj.put("url", i.getUrl());
        SimpleDateFormat s = new SimpleDateFormat( "yyyy-MM-dd");
        obj.put("upload_date", s.format(i.getUpload_date()));
        obj.put("user_id", i.getUser_id());
//        System.out.println("hello create" + u.getAccount_created());
//        obj.put("account_created", s.format(u.getAccount_created()));
//        obj.put("account_updated", s.format(u.getAccount_updated()));
        return obj;
    }

}
