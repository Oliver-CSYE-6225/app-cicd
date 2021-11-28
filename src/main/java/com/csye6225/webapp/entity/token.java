package com.csye6225.webapp.entity;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "ProductInfo")
public class token {
    private String EmailId;
    private String token;
    private String cost;

    @DynamoDBHashKey
    // @DynamoDBAutoGeneratedKey
    public String getEmailId() {
        return EmailId;
    }

    @DynamoDBAttribute
    public String gettoken() {
        return token;
    }
}