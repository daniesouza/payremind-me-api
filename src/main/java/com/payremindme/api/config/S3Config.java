package com.payremindme.api.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.BucketLifecycleConfiguration;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.amazonaws.services.s3.model.Tag;
import com.amazonaws.services.s3.model.lifecycle.LifecycleFilter;
import com.amazonaws.services.s3.model.lifecycle.LifecycleTagPredicate;
import com.payremindme.api.config.property.PayRemindMeApiProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Config {

    private static final Logger logger = LoggerFactory.getLogger(S3Config.class);


    @Autowired
    private PayRemindMeApiProperty property;

    @Bean
    public AmazonS3 amazonS3() {

        logger.info("Inicializando Bucket Amazon S3 com nome "+property.getAmazonS3().getBucket()+"...");

        AWSCredentials credentials = new BasicAWSCredentials(property.getAmazonS3().getAccessKeyId(),
                property.getAmazonS3().getSecretAccessKey());

        AmazonS3 amazonS3 = AmazonS3ClientBuilder.standard()
                .withRegion(Regions.SA_EAST_1)
                .withCredentials(
                new AWSStaticCredentialsProvider(credentials))
                .build();

        if (!amazonS3.doesBucketExistV2(property.getAmazonS3().getBucket())) {

            logger.info("Bucket "+property.getAmazonS3().getBucket() + "Nao encontrado. Criando um novo...");

            amazonS3.createBucket(new CreateBucketRequest(property.getAmazonS3().getBucket()));

            BucketLifecycleConfiguration.Rule regraExpiracao = new BucketLifecycleConfiguration.Rule()
                    .withId("Regra expiracao de arquivos temporarios")
                    .withFilter(new LifecycleFilter(new LifecycleTagPredicate(new Tag("expirar", "true"))))
                    .withExpirationInDays(1)
                    .withStatus(BucketLifecycleConfiguration.ENABLED);

            BucketLifecycleConfiguration configuration = new BucketLifecycleConfiguration()
                    .withRules(regraExpiracao);

            amazonS3.setBucketLifecycleConfiguration(property.getAmazonS3().getBucket(), configuration);

            logger.info("Bucket "+property.getAmazonS3().getBucket() + "Criado");

        }

        logger.info("Bucket "+property.getAmazonS3().getBucket() + " Inicializado.");


        return amazonS3;
    }
}
