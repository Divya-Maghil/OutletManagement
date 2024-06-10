package com.example.Outlet_Management.service.serviceImpl;

import com.example.Outlet_Management.Dao.MediaDao;
import com.example.Outlet_Management.config.AWSCredentials;
import com.example.Outlet_Management.entity.MhMedia;
import com.example.Outlet_Management.error.AWSImageUploadFailedException;
import com.example.Outlet_Management.error.ImageNotFoundException;
import com.example.Outlet_Management.service.ImageService;
import com.example.Outlet_Management.util.Aws;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Base64;
import java.util.UUID;


@Service
public class ImageServiceImpl implements ImageService {

    @Autowired
    private AWSCredentials awsCredentials;

    @Autowired
    private MediaDao mediaDao;


    public String storeImage(String entityId, String base64Image, String entityType) throws ImageNotFoundException, AWSImageUploadFailedException {
        if (entityId != null) {
            if (base64Image != null) {
                byte[] image = Base64.getDecoder().decode(base64Image);
                Tika tika = new Tika();
                String mimeType = tika.detect(image);
                Aws aws = new Aws();

                // Generate a unique key for the image
                String key = "oms_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString();

                if (aws.uploadFileToS3(key, image, mimeType, awsCredentials.getACCESS_KEY(), awsCredentials.getSECRET_KEY(), awsCredentials.getBUCKET_NAME())) {
                    MhMedia media = new MhMedia();
                    media.setEntityId(entityId);
                    media.setEntityType(entityType);
                    media.setFileName(key); // Use the generated key as the file name
                    media.setMimeType(mimeType);
                    mediaDao.saveMedia(media);
                    return media.getId();
                } else {
                    throw new AWSImageUploadFailedException("Failed to upload image to S3");
                }
            } else {
                throw new ImageNotFoundException("Image not present in request body");
            }
        }else{
            throw new IllegalArgumentException("exception Id is blank");
        }
    }
}
