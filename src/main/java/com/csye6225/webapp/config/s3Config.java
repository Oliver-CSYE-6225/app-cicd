package com.csye6225.webapp.config;

import com.amazonaws.auth.*;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.BucketReplicationConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class s3Config {
//    @Value("${cloud.aws.credentials.access-key}")
//    private String accessKey;
//
//    @Value("${cloud.aws.credentials.secret-key}")
//    private String accessSecret;
//    @Value("${cloud.aws.region.static}")
//    private String region;

    @Bean
    public static AmazonS3Client s3Client() {
//        AWSCredentials credentials = new BasicAWSCredentials(accessKey, accessSecret);
//        BucketReplicationConfiguration config = new BucketReplicationConfiguration().withRoleARN();
//        AmazonS3Client  s3Client = new AmazonS3Client();
//        s3Client.setBucketReplicationConfiguration(, config);
//        return AmazonS3ClientBuilder.standard().withClientConfiguration()
//                .
////                .withCredentials(new AWSStaticCredentialsProvider(credentials))
//                .withRegion(region)
//                .build();
//        AmazonS3 s3Client = AmazonS3ClientBuilder.standard().build();
        AmazonS3Client s3 = (AmazonS3Client) AmazonS3ClientBuilder.standard()
                .withCredentials(new DefaultAWSCredentialsProviderChain())
                .build();
        return s3;
    }
}
