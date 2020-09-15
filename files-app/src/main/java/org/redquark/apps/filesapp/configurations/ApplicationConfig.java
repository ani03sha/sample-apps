package org.redquark.apps.filesapp.configurations;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Bean
    @ConfigurationProperties(prefix = "amazon-config")
    public AmazonS3Config getAmazonS3Config() {
        return new AmazonS3Config();
    }
}
