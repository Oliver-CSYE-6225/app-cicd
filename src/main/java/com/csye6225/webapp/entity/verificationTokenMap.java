// package com.csye6225.webapp.entity;

// import java.util.UUID;

// import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
// import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
// import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

// @DynamoDBTable(tableName = "Email-Tokens")
// public class verificationTokenMap {
//     private String EmailId;
//     private String token;
//     private long TimeToExist;

//     @DynamoDBHashKey
//     public String getEmailId() {
//         return EmailId;
//     }

//     // @DynamoDBAttribute
//     // public String getToken() {
//     //     return EmailId;
//     // }

//     // @DynamoDBAttribute
//     // public String getCost() {
//     //     return EmailId;
//     // }
// }
