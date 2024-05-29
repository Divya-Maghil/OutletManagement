package com.example.Outlet_Management.service.serviceImpl;

import com.example.Outlet_Management.Dao.LocationDao;
import com.example.Outlet_Management.Dao.MediaDao;
import com.example.Outlet_Management.Dto.*;
import com.example.Outlet_Management.entity.MhLocation;
import com.example.Outlet_Management.entity.MhMedia;
import com.example.Outlet_Management.error.AWSImageUploadFailedException;
import com.example.Outlet_Management.error.ImageNotFoundException;
import com.example.Outlet_Management.service.ManagementService;
import com.example.Outlet_Management.util.Aws;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.*;

@Service
@Slf4j
public class ManagementServiceImpl implements ManagementService {

    @Autowired
    private LocationDao locationDao;

    @Autowired
    private MediaDao mediaDao;


   @Value("${aws.access.key.id}")
   private String ACCESS_KEY;
    @Value("${aws.secret.access.key}")
    private String SECRET_KEY;
    @Value("${aws.s3.region}")
    private String region;
    @Value("${aws.s3.bucket.name}")
    private String BUCKET_NAME;



    @Override
    public ResponseEntity<String> saveRegistration(RegistrationDTO registrationDTO) throws ImageNotFoundException, AWSImageUploadFailedException {
    String id=UUID.randomUUID().toString();

        MhLocation newLocation=new MhLocation();
        newLocation.setMerchantId("8dfe7674-709d-431c-a233-628e839ecc76");
        newLocation.setId(id);
        newLocation.setRestaurantName(registrationDTO.getRestaurantName());
        newLocation.setName(registrationDTO.getName());
        newLocation.setPhone(registrationDTO.getPhone());
        newLocation.setEmail(registrationDTO.getEmail());
        newLocation.setAttributes(registrationDTO.getGstNumber());

        if(registrationDTO.getBase64Image()!=null){

            byte[] image = Base64.getDecoder().decode(registrationDTO.getBase64Image());
            Tika tika = new Tika();
            String mimiType = tika.detect(image);
            Aws aws = new Aws();

            if (aws.uploadFileToS3(id, image, mimiType, ACCESS_KEY, SECRET_KEY, BUCKET_NAME)) {
                MhMedia media=new MhMedia();
                media.setId(UUID.randomUUID().toString());
                media.setEntityId(id);
                media.setEntityType("LOGO");
                media.setFileName("cms_" + System.currentTimeMillis());
                media.setTag(null);
                media.setMimeType(mimiType);
                media.setSortOrder(null);
                mediaDao.saveMedia(media);

            }
        } else {
            throw new ImageNotFoundException("Image not present in request body");
        }
        locationDao.save(newLocation);
    return ResponseEntity.status(HttpStatus.OK).body("Success");
    }

    @Override
    public List<GetDto> getData(String id) {
        List<MhLocation> location=locationDao.findByMerchantId(id);
        List<GetDto> resultList = new ArrayList<>();
        for(MhLocation locations:location) {
            GetDto returndto=new GetDto();

            System.out.println("id:" + locations.getEmail());
            MhMedia media = mediaDao.findByEntityId(locations.getId());
            returndto.setId(locations.getId());

            returndto.setName(locations.getName());
            returndto.setEmail(locations.getEmail());
            returndto.setPhone(locations.getPhone());
            returndto.setRestaurantName(locations.getRestaurantName());
            returndto.setMerchantId(locations.getMerchantId());
            returndto.setCity(locations.getCity());
            returndto.setAddressLine1(locations.getAddressLine1());
            returndto.setAddressLine2(locations.getAddressLine2());
            returndto.setAddressLine3(locations.getAddressLine3());
            returndto.setAttributes(locations.getAttributes());
            returndto.setCountry(locations.getCountry());
            returndto.setState(locations.getState());
            returndto.setPinCode(locations.getPinCode());
            if (media != null) {
                MediaDto mediaDto = new MediaDto();
                mediaDto.setEntityId(media.getEntityId());
                mediaDto.setEntityType(media.getEntityType());
                mediaDto.setFileName(media.getFileName());
                mediaDto.setMimeType(media.getMimeType());
                mediaDto.setSortOrder(media.getSortOrder());
                mediaDto.setTag(media.getTag());
                returndto.setMedia(mediaDto);
            }
            resultList.add(returndto);

        }
        return resultList;
    }


@Override
public ResponseEntity<String> onboarding(OnboardingDto onboardingDto) throws JsonProcessingException, AWSImageUploadFailedException {
    System.out.println("id" + onboardingDto.getRestaurant_details().getId());
    Optional<MhLocation> locationData = locationDao.findById(onboardingDto.getRestaurant_details().getId());
    System.out.println("data;;;;;;;;;;;;;;;;;;");
    System.out.println(locationData);
    String imageId = null;
    if (!locationData.isPresent()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Location not found");
    }
    MhLocation location = locationData.get();

    if (onboardingDto.getRestaurant_details().getBusinessLegalName() != null && !onboardingDto.getRestaurant_details().getBusinessLegalName().equals(location.getRestaurantName())) {
        location.setRestaurantName(onboardingDto.getRestaurant_details().getBusinessLegalName());
    }
    if (onboardingDto.getRestaurant_details().getPhone() != null && !onboardingDto.getRestaurant_details().getPhone().equals(location.getPhone())) {
        location.setPhone(onboardingDto.getRestaurant_details().getPhone());
    }
    if (onboardingDto.getRestaurant_details().getEmail() != null && !onboardingDto.getRestaurant_details().getEmail().equals(location.getEmail())) {
        location.setEmail(onboardingDto.getRestaurant_details().getEmail());
    }
    location.setAddressLine1(onboardingDto.getLocation_Details().getAddress());
    location.setCity(onboardingDto.getLocation_Details().getCity());
    location.setState(onboardingDto.getLocation_Details().getState());
    location.setPinCode(onboardingDto.getLocation_Details().getPinCode());
    location.setCountry(onboardingDto.getLocation_Details().getCountry());

    ObjectMapper objectMapper = new ObjectMapper();
    FssaiDto fssaiDetailsJson = onboardingDto.getFssai_details();
    BankDto bank = onboardingDto.getBank_details();

    if (onboardingDto.getFssai_details().getDocuments() != null && !onboardingDto.getFssai_details().getDocuments().isEmpty()) {
        byte[] imageBytes = Base64.getDecoder().decode(onboardingDto.getFssai_details().getDocuments());
        String fileName = String.valueOf(UUID.randomUUID());
        Tika tika = new Tika();
        String mimeType = tika.detect(imageBytes);
        try {
            Aws awsCloudUtil = new Aws();
            awsCloudUtil.uploadFileToS3(fileName, imageBytes, mimeType, ACCESS_KEY, SECRET_KEY, BUCKET_NAME);

            MhMedia media = new MhMedia();
            media.setId(UUID.randomUUID().toString());
            media.setEntityId(location.getId());
            media.setEntityType("FSSAI_DOCUMENT");

            media.setFileName(fileName);
            media.setMimeType(mimeType);
            media.setSortOrder(1);
            media.setTag("FSSAI Document");
            imageId = media.getId();
            mediaDao.saveMedia(media);
        } catch (Exception e) {
            throw new AWSImageUploadFailedException("Failed to upload image to AWS S3", e);
        }
    }
    Map<String, String> attributesMap = new HashMap<>();
    if(onboardingDto.getFssai_details().getIsEnabled().equalsIgnoreCase("yes")) {
        fssaiDetailsJson.setDocuments(imageId);
        attributesMap.put("Fssai_details", String.valueOf(fssaiDetailsJson));
    }
    attributesMap.put("bank_details", String.valueOf(bank));
    attributesMap.put("RestaurantNumber",onboardingDto.getRestaurant_details().getRestaurantNumber());
    attributesMap.put("websiteLink",onboardingDto.getRestaurant_details().getWebsite());
    attributesMap.put("instagramLink",onboardingDto.getRestaurant_details().getInstagramLink());
    attributesMap.put("FaceBookLink",onboardingDto.getRestaurant_details().getFacebookLink());
    attributesMap.put("WhatsappNumber",onboardingDto.getRestaurant_details().getWhatsappNumber());
    String attributesJson = objectMapper.writeValueAsString(attributesMap);
    if (attributesJson == null || attributesJson.isEmpty()) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid attributes content");
    }

    location.setAttributes(attributesJson);
    locationDao.save(location);

    return ResponseEntity.status(HttpStatus.OK).body("Success");
}


    

}
