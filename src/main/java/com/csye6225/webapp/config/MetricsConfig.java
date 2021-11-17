package com.csye6225.webapp.config;

import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {
   @Bean
   public StatsDClient statsDClient(
           @Value("${metrics.statsd.host:localhost}") String host,
           @Value("${metrics.statsd.port:8125}") int port,
           @Value("webapp_csye6225") String prefix
   ) {
       return new NonBlockingStatsDClient(prefix, host, port);
   }
}