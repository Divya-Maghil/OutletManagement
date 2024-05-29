package com.example.Outlet_Management.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component("credentials")
@Data
public class AWSCredentials {


    @Value("${aws.access.key.id}")
    private String ACCESS_KEY;
    @Value("${aws.secret.access.key}")
    private String SECRET_KEY;
    @Value("${aws.s3.region}")
    private String region;
    @Value("${aws.s3.bucket.name}")
    private String BUCKET_NAME;


}
