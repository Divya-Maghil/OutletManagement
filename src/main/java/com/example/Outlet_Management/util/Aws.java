package com.example.Outlet_Management.util;



import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.Outlet_Management.error.AWSImageUploadFailedException;


import java.io.*;

public class Aws {
    private AWSCredentials awsCredentials(String accessKey, String secretKey) {
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        return credentials;
    }


    private AmazonS3 awsS3ClientBuilder(String accessKey, String secretKey) {
        AmazonS3 s3Client = AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials(accessKey, secretKey)))
                .withRegion(Regions.AP_SOUTHEAST_2)
                .build();
        return s3Client;

    }


    public boolean uploadFileToS3(String fileName, byte[] fileContent,String mimeType, String accessKey, String secretKey, String bucketName) throws AWSImageUploadFailedException {
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey);
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(Regions.AP_SOUTHEAST_2)
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .build();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(fileContent);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(fileContent.length);
        metadata.setContentType(mimeType);
        try {
            PutObjectRequest request = new PutObjectRequest(bucketName, fileName, byteArrayInputStream, metadata);
            s3Client.putObject(request);
            return true;
        } catch (AmazonS3Exception e) {
            throw new AWSImageUploadFailedException("File upload failed to Aws Bucket" , e);
        }

    }
}
