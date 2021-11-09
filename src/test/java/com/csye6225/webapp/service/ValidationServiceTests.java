package com.csye6225.webapp.service;


import com.csye6225.webapp.service.ValidationService;
import org.json.JSONException;
import org.json.JSONObject;
import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class ValidationServiceTests {
    @Test
    void testValidate0() {
        ValidationService v = new ValidationService();
        JSONObject obj = new JSONObject();
        try{
            obj.put("first_name", "Jane");
            obj.put("last_name", "Doe");
            obj.put("username", "abc@gmail.com");
            obj.put("password", "abc@123");

        } catch (JSONException e){
            System.out.println("JSONException:" + e);
        }
        System.out.println(v.validateModifyObject(obj));
        assertEquals("", v.validateModifyObject(obj));
    }

    @Test
    void testValidate1() {
        ValidationService v = new ValidationService();
        JSONObject obj = new JSONObject();
        try{
            obj.put("first_name", "Jane");
            obj.put("last_name", "Doe");
            obj.put("username", "abc@gmail.com");
            obj.put("password", "abc@123");
            obj.put("account_create", "2021-10-06T00:27:01Z");

        } catch (JSONException e){
            System.out.println("JSONException:" + e);
        }
        assertEquals("Only following User fields are allowed for modification: {first_name, last_name, password, username}", v.validateModifyObject(obj));
    }

    @Test
    void testValidate2() {
        ValidationService v = new ValidationService();
        JSONObject obj = new JSONObject();
        try{
            obj.put("first_name", "Jane");
            obj.put("last_name", "Doe");
            obj.put("username", "invalidemailstring");
            obj.put("password", "abc@123");

        } catch (JSONException e){
            System.out.println("JSONException:" + e);
        }
        assertEquals("Username can only be a valid email address", v.validateModifyObject(obj));
    }

    @Test
     void testValidateUsernameFormat() {
        ValidationService v = new ValidationService();
        String username = "abc";
        assertEquals(false, v.validateUsernameFormat(username));
    }
}
