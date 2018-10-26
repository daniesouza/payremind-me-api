package com.payremindme.api.amazon;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.payremindme.api.config.property.PayRemindMeApiProperty;
import com.payremindme.api.dto.Anexo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

@Component
public class StorageS3 {

    private static final Logger logger = LoggerFactory.getLogger(StorageS3.class);

    private AmazonS3 amazonS3;
    private PayRemindMeApiProperty property;

    @Autowired
    public StorageS3(AmazonS3 amazonS3, PayRemindMeApiProperty property){
        this.amazonS3 = amazonS3;
        this.property = property;
    }

    public Anexo saveTemporary(MultipartFile arquivo) {
        AccessControlList acl = new AccessControlList();
        acl.grantPermission(GroupGrantee.AllUsers, Permission.Read);

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(arquivo.getContentType());
        objectMetadata.setContentLength(arquivo.getSize());

        String nomeUnico = gerarNomeUnico(arquivo.getOriginalFilename());

        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(property.getAmazonS3().getBucket(),
                    nomeUnico, arquivo.getInputStream(), objectMetadata)
                    .withAccessControlList(acl);

            putObjectRequest.setTagging(new ObjectTagging(
                    Collections.singletonList(new Tag("expirar", "true"))
            ));

            amazonS3.putObject(putObjectRequest);

            if (logger.isDebugEnabled()) {
                logger.debug("Arquivo {} enviado com sucesso para o S3.", nomeUnico);
            }

            return new Anexo(nomeUnico, configurarURL(nomeUnico));

        } catch (IOException e) {
            throw new RuntimeException("Falha ao enviar arquivo para o Amazom S3", e);
        }
    }

    public void save(String objeto) {
        SetObjectTaggingRequest objectTaggingRequest = new SetObjectTaggingRequest(
                property.getAmazonS3().getBucket(),
                objeto,
                new ObjectTagging(Collections.emptyList()));

        amazonS3.setObjectTagging(objectTaggingRequest);
    }

    public void delete(String objeto) {

        DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(
                property.getAmazonS3().getBucket(),
                objeto);
        amazonS3.deleteObject(deleteObjectRequest);
    }

    public void update(String objetoAntigo, String objetoNovo) {
        if(StringUtils.hasText(objetoAntigo)){
            this.delete(objetoAntigo);
        }
        this.save(objetoNovo);
    }

    public String configurarURL(String object) {
        return "https:\\\\s3-sa-east-1.amazonaws.com/" + property.getAmazonS3().getBucket() + "/" + object;
    }

    private String gerarNomeUnico(String originalFilename) {
        return UUID.randomUUID().toString() + "_" + originalFilename;
    }


}
