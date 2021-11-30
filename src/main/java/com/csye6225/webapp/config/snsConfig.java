package com.csye6225.webapp.config;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.sns.AmazonSNSClient;

import org.springframework.context.annotation.Bean;

public class snsConfig {
    @Bean
    public static AmazonSNSClient snsClient() {

        AmazonSNSClient client = new AmazonSNSClient(new DefaultAWSCredentialsProviderChain());
        // client.getItem("tableName", key)
        // client.publish(topicArn, message)
        return client;
    }
}
