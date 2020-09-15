package org.redquark.apps.filesapp.configurations;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AmazonS3Config {

    private String endpointUrl;
    private String accessKey;
    private String secretKey;
    private String bucketName;
}
