package org.redquark.apps.filesapp.services;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import org.redquark.apps.filesapp.configurations.AmazonS3Config;
import org.redquark.apps.filesapp.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import static org.redquark.apps.filesapp.constants.AppConstants.SLASH;

@Service
public class AmazonClientService {

    private static final String TAG = AmazonClientService.class.getSimpleName();
    private static final Logger LOGGER = LoggerFactory.getLogger(AmazonClientService.class);

    private final AmazonS3Config amazonS3Config;

    private AmazonS3 s3client;

    @Autowired
    public AmazonClientService(AmazonS3Config amazonS3Config) {
        this.amazonS3Config = amazonS3Config;
    }

    @PostConstruct
    private void initializeAmazon() {
        AWSCredentials awsCredentials = new BasicAWSCredentials(
                amazonS3Config.getAccessKey(), amazonS3Config.getSecretKey());
        this.s3client = AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .withRegion(Regions.AP_SOUTH_1)
                .build();
    }

    public String upload(MultipartFile multipartFile) {
        LOGGER.info("{}: uploading {} to S3 bucket", TAG, multipartFile.getOriginalFilename());
        String fileUrl = "";
        try {
            // Get the converted file
            File file = FileUtils.convertMultipartFileToFile(multipartFile);
            // Get the file name
            String fileName = FileUtils.generateFileName(multipartFile);
            LOGGER.debug("{}: name of the file to be uploaded: {}", TAG, fileName);
            // Get the url for the file
            fileUrl = amazonS3Config.getEndpointUrl() + SLASH + amazonS3Config.getBucketName() + SLASH + fileName;
            // Uploading file to the S3 bucket
            s3client.putObject(new PutObjectRequest(amazonS3Config.getBucketName(), fileName, file)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (Exception e) {
            LOGGER.error("{}: file cannot be uploaded to s3 due to {}", TAG, e.getMessage());
        }
        return fileUrl;
    }

    public String delete(String fileUrl) {
        // Get the file name
        String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
        LOGGER.info("{}: trying to delete {}", TAG, fileName);
        s3client.deleteObject(new DeleteObjectRequest(amazonS3Config.getBucketName(), fileName));
        return "Successfully deleted";
    }

    public boolean doesFileExist(String fileName) {
        return s3client.doesObjectExist(amazonS3Config.getBucketName(), fileName);
    }

    public String get(String fileName) {
        StringBuilder content = new StringBuilder();
        try {
            S3Object fileObject = s3client.getObject(amazonS3Config.getBucketName(), fileName);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileObject.getObjectContent()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line);
            }
        } catch (IOException e) {
            LOGGER.error("{}: cannot get {} due to {}", TAG, fileName, e.getMessage());
        }
        return content.toString();
    }
}