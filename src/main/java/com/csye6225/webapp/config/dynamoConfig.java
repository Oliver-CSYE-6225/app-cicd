package com.csye6225.webapp.config;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.auth.*;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.beans.factory.annotation.Value;
// import org.springframework.cloud.aws.core.region.DefaultAwsRegionProviderChainDelegate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
// @EnableDynamoDBRepositories
//   (basePackages = "com.baeldung.spring.data.dynamodb.repositories")
public class dynamoConfig {

    private static final Logger LOGGER=LoggerFactory.getLogger(dynamoConfig.class);

    @Value("${amazon.dynamodb.endpoint}")
    private static String amazonDynamoDBEndpoint;

    @Bean
    public static AmazonDynamoDB dynamoClient() {

        AmazonDynamoDB client = new AmazonDynamoDBClient(new DefaultAWSCredentialsProviderChain());
        LOGGER.info("Dynamo endpoint" + amazonDynamoDBEndpoint);
        client.setEndpoint("https://dynamodb.us-east-1.amazonaws.com");
        return client;
    }
}
