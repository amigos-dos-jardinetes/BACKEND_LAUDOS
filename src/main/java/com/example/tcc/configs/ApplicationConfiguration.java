package com.example.tcc.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfiguration {
    @Value("${aws.s3.bucket.name}")
    private String bucketName;
    @Value("${aws.s3.bucket.url}")
    private String bucketURL;
    @Value("${ocr.server.endpoint}")
    private String ocrServerEndpoint;

    @Bean
    public String bucketName() {
        return bucketName;
    }

    @Bean
    public String bucketURL() { return bucketURL; }

    @Bean
    public String ocrServerEndpoint() {
        return ocrServerEndpoint;
    }

}