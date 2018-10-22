package com.payremindme.api.config.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("payremindme")
public class PayRemindMeApiProperty {

    private String origemPermitida = "http://localhost:8000";
    private final Seguranca seguranca = new Seguranca();
    private final Mail mail = new Mail();
    private final AmazonS3 amazonS3 = new AmazonS3();


    public Seguranca getSeguranca() {
        return seguranca;
    }

    public String getOrigemPermitida() {
        return origemPermitida;
    }

    public void setOrigemPermitida(String origemPermitida) {
        this.origemPermitida = origemPermitida;
    }

    public Mail getMail() {
        return mail;
    }

    public AmazonS3 getAmazonS3() {
        return amazonS3;
    }

    public static class Seguranca {

        private boolean enableHttps;

        public boolean isEnableHttps() {
            return enableHttps;
        }

        public void setEnableHttps(boolean enableHttps) {
            this.enableHttps = enableHttps;
        }
    }


    public static class Mail {
        private String  host;
        private Integer port;
        private String  username;
        private String  password;

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public Integer getPort() {
            return port;
        }

        public void setPort(Integer port) {
            this.port = port;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public static class AmazonS3{
        private String accessKeyId;
        private String secretAccessKey;
        private String bucket;

        public String getAccessKeyId() {
            return accessKeyId;
        }

        public void setAccessKeyId(String accessKeyId) {
            this.accessKeyId = accessKeyId;
        }

        public String getSecretAccessKey() {
            return secretAccessKey;
        }

        public void setSecretAccessKey(String secretAccessKey) {
            this.secretAccessKey = secretAccessKey;
        }

        public String getBucket() {
            return bucket;
        }

        public void setBucket(String bucket) {
            this.bucket = bucket;
        }
    }
}
