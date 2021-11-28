package com.csye6225.webapp.config;

import com.amazonaws.auth.*;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

import org.springframework.cloud.aws.core.region.DefaultAwsRegionProviderChainDelegate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class dynamoConfig {
    @Bean
    public static AmazonDynamoDBClient dynamoClient() {
        AmazonDynamoDBClient client = new AmazonDynamoDBClient(new DefaultAWSCredentialsProviderChain());
        // client.getItem("tableName", key)
        return client;
    }
}
