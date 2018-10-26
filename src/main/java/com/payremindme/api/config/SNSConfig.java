package com.payremindme.api.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.payremindme.api.config.property.PayRemindMeApiProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SNSConfig {

    private static final Logger logger = LoggerFactory.getLogger(SNSConfig.class);


    @Autowired
    private PayRemindMeApiProperty property;

    @Bean
    public AmazonSNS amazonSNS() {

        logger.info("Inicializando Amazon SNS na regiao "+Regions.US_EAST_1+"...");

        AWSCredentials credentials = new BasicAWSCredentials(property.getAmazonS3().getAccessKeyId(),
                property.getAmazonS3().getSecretAccessKey());

        AmazonSNS amazonSNS = AmazonSNSClientBuilder.standard().
                withRegion(Regions.US_EAST_1).
                withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();


        logger.info("Amazon SNS na regiao "+Regions.US_EAST_1+ " Inicializado.");


        return amazonSNS;
    }
}
