package com.example.Outlet_Management.service.serviceImpl;

import com.example.Outlet_Management.Dao.AvailabilityDao;
import com.example.Outlet_Management.Dao.LocationDao;
import com.example.Outlet_Management.Dao.MediaDao;
import com.example.Outlet_Management.Dto.*;
import com.example.Outlet_Management.config.AWSCredentials;
import com.example.Outlet_Management.entity.MhLocation;
import com.example.Outlet_Management.entity.MhMedia;
import com.example.Outlet_Management.entity.mhAvailability;
import com.example.Outlet_Management.error.AWSImageUploadFailedException;
import com.example.Outlet_Management.error.ImageNotFoundException;
import com.example.Outlet_Management.service.ManagementService;
import com.example.Outlet_Management.util.Aws;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
@Slf4j
public class ManagementServiceImpl implements ManagementService {

    @Autowired
    private LocationDao locationDao;

    @Autowired
    private MediaDao mediaDao;
    @Autowired
    private AvailabilityDao availabilityDao;
    private AWSCredentials awsCredentials;
    Map<String,Object> attributesMap = new HashMap<>();

    @Override
    public ResponseEntity<String> saveRegistration(RegistrationDTO registrationDTO) throws ImageNotFoundException, AWSImageUploadFailedException, JsonProcessingException {
        String id=UUID.randomUUID().toString();
        MhLocation newLocation=new MhLocation();
        newLocation.setMerchantId("8dfe7674-709d-431c-a233-628e839ecc76");
        newLocation.setId(id);
        newLocation.setRestaurantName(registrationDTO.getRestaurantName());
        newLocation.setName(registrationDTO.getName());
        newLocation.setPhone(registrationDTO.getPhone());
        newLocation.setEmail(registrationDTO.getEmail());

        if (newLocation.getAttributes() == null) {
            newLocation.setAttributes("{}");
        }

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode json = objectMapper.createObjectNode();
        json.put("gstNumber", registrationDTO.getGstNumber());

        String updatedJson = objectMapper.writeValueAsString(json);
        System.out.println(updatedJson);
        newLocation.setAttributes(updatedJson);


        if(registrationDTO.getBase64Image()!=null){
            byte[] image = Base64.getDecoder().decode(registrationDTO.getBase64Image());
            Tika tika = new Tika();
            String mimiType = tika.detect(image);
            Aws aws = new Aws();

            if (aws.uploadFileToS3(id, image, mimiType, awsCredentials.getACCESS_KEY(), awsCredentials.getSECRET_KEY(), awsCredentials.getBUCKET_NAME())) {
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
        Optional<MhLocation> locationData = locationDao.findById(onboardingDto.getRestaurant_details().getId());
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
            String fileName = String.valueOf("cms_"+System.currentTimeMillis());
            Tika tika = new Tika();
            String mimeType = tika.detect(imageBytes);
            try {
                Aws awsCloudUtil = new Aws();
                awsCloudUtil.uploadFileToS3(fileName, imageBytes, mimeType, awsCredentials.getACCESS_KEY(), awsCredentials.getSECRET_KEY(), awsCredentials.getBUCKET_NAME());

                MhMedia media = new MhMedia();
                media.setId(UUID.randomUUID().toString());
                media.setEntityId(location.getId());
                media.setEntityType("FSSAI_DOCUMENT");
                media.setFileName(fileName);
                media.setMimeType(mimeType);
                imageId = media.getId();
                mediaDao.saveMedia(media);
            } catch (Exception e) {
                throw new AWSImageUploadFailedException("Failed to upload image to AWS S3", e);
            }
        }
        System.out.println(onboardingDto.getFssai_details());
        if(onboardingDto.getFssai_details().getIsEnabled().equalsIgnoreCase("yes")) {
            fssaiDetailsJson.setDocuments(imageId);
            String attributesJson = objectMapper.writeValueAsString(onboardingDto.getFssai_details());
            attributesMap.put("FSSAIDetails",attributesJson);
        }
        if(onboardingDto.getBank_details()!=null) {
            String attributesJson = objectMapper.writeValueAsString(onboardingDto.getBank_details());
            attributesMap.put("checkInDetails",attributesJson);
        }

        attributesMap.put("RestaurantNumber",onboardingDto.getRestaurant_details().getRestaurantNumber());
        attributesMap.put("websiteLink",onboardingDto.getRestaurant_details().getWebsite());
        attributesMap.put("instagramLink",onboardingDto.getRestaurant_details().getInstagramLink());
        attributesMap.put("FaceBookLink",onboardingDto.getRestaurant_details().getFacebookLink());
        attributesMap.put("WhatsappNumber",onboardingDto.getRestaurant_details().getWhatsappNumber());

        String existingAttributes = locationData.get().getAttributes();
        JsonNode oldAttributes = existingAttributes != null ? objectMapper.readTree(existingAttributes) : objectMapper.createObjectNode();
        JsonNode mergeData = objectMapper.readerForUpdating(oldAttributes).readValue(objectMapper.writeValueAsString(attributesMap));
        location.setAttributes(objectMapper.writeValueAsString(attributesMap));
        locationDao.save(location);

        return ResponseEntity.status(HttpStatus.OK).body("Success");
    }

    @Override
    public ResponseEntity<String> saveBasic(BasicDetailsDto basicDetailsDto) throws Exception {
        final ObjectMapper objectMapper = new ObjectMapper();
        List<RestaurantSessionDto> sessionDtos=basicDetailsDto.getRestaurantSessionDto();
        if(sessionDtos.size()!=0){
            for(RestaurantSessionDto sessionDto:sessionDtos){
                int count=sessionDto.getWeekday().size();
                int len=sessionDto.getWeekday().size();
                while(count>0) {
                    mhAvailability availability = new mhAvailability();
                    availability.setName(sessionDto.getName());
                    availability.setId(UUID.randomUUID().toString());
                    availability.setStart_time(sessionDto.getStart_time());
                    availability.setEnd_time(sessionDto.getEnd_time());
                    availability.setLocation_id(basicDetailsDto.getLocation_id());
                    availability.setWeekday(numberingDays(sessionDto.getWeekday().get(len-count)));
                    availabilityDao.save(availability);
                    count--;
                }
                Optional<MhLocation> existingLocation=locationDao.findById(basicDetailsDto.getLocation_id());
                if(existingLocation.isPresent()) {
                    MhLocation location=existingLocation.get();
                    Map<String, String> attributesMap = new HashMap<>();
                    if(basicDetailsDto.getCuisines()!= null)
                        attributesMap.put("cuisines", String.valueOf(basicDetailsDto.getCuisines()));
                    if(basicDetailsDto.getAmenities()!=null)
                        attributesMap.put("amenities", String.valueOf(basicDetailsDto.getAmenities()));
                    if(basicDetailsDto.getParking()!=null)
                        attributesMap.put("parking", String.valueOf(basicDetailsDto.getParking()));
                    attributesMap.put("safetyMeasures", String.valueOf(basicDetailsDto.getSafetyMeasures()));
                    String attributesJson = objectMapper.writeValueAsString(attributesMap);
                    String existingAttributes = existingLocation.get().getAttributes();
                    JsonNode oldAttributes = existingAttributes != null ? objectMapper.readTree(existingAttributes) : objectMapper.createObjectNode();

                    JsonNode mergeData = objectMapper.readerForUpdating(oldAttributes).readValue(attributesJson);
                    location.setAttributes(objectMapper.writeValueAsString(mergeData));
                    locationDao.save(location);
                }else {
                    throw new Exception("Entity not found");
                }

            }
        }
        return ResponseEntity.status(HttpStatus.OK).body("Success");
    }

    private String numberingDays(String weekday) {
        Map<String,String> dayMap=new HashMap<>();
        dayMap.put("Monday","1");
        dayMap.put("Tuesday","2");
        dayMap.put("Wednesday","3");
        dayMap.put("Thursday","4");
        dayMap.put("Friday","5");
        dayMap.put("Saturday","6");
        dayMap.put("Sunday","7");
        return dayMap.get(weekday);
    }


    @Override
    public ResponseEntity<String> saveRestaurantImg(RestaurantImgDto restaurantImgDTO) throws AWSImageUploadFailedException {
        if(restaurantImgDTO.getProfileImg()!=null){
            byte[] imageBytes = Base64.getDecoder().decode(restaurantImgDTO.getProfileImg());
            String fileName = String.valueOf("cms_"+System.currentTimeMillis());
            Tika tika = new Tika();
            String mimeType = tika.detect(imageBytes);
            try {
                Aws awsCloudUtil = new Aws();
                awsCloudUtil.uploadFileToS3(fileName, imageBytes, mimeType, awsCredentials.getACCESS_KEY(), awsCredentials.getSECRET_KEY(), awsCredentials.getBUCKET_NAME());
                MhMedia media = new MhMedia();
                media.setId(UUID.randomUUID().toString());
                media.setEntityId(restaurantImgDTO.getLocationId());
                media.setEntityType("profile_Img");

                media.setFileName(fileName);
                media.setMimeType(mimeType);
                mediaDao.saveMedia(media);
            } catch (Exception e) {
                throw new AWSImageUploadFailedException("Failed to upload image to AWS S3", e);
            }

        }
        if(restaurantImgDTO.getRestaurantImgs()!=null){
            for(String restImg: restaurantImgDTO.getRestaurantImgs()){
                byte[] imageBytes = Base64.getDecoder().decode(restImg);
                String fileName = String.valueOf("cms_"+System.currentTimeMillis());
                Tika tika = new Tika();
                String mimeType = tika.detect(imageBytes);
                try {
                    Aws awsCloudUtil = new Aws();
                    awsCloudUtil.uploadFileToS3(fileName, imageBytes, mimeType, awsCredentials.getACCESS_KEY(), awsCredentials.getSECRET_KEY(), awsCredentials.getBUCKET_NAME());
                    MhMedia media = new MhMedia();
                    media.setId(UUID.randomUUID().toString());
                    media.setEntityId(restaurantImgDTO.getLocationId());
                    media.setEntityType("restaurant_Img");
                    media.setFileName(fileName);
                    media.setMimeType(mimeType);
                    mediaDao.saveMedia(media);
                }catch (Exception e) {
                    throw new AWSImageUploadFailedException("Failed to upload image to AWS S3", e);
                }
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body("Success");
    }

    @Override
    public ResponseEntity<String> saveDineIn(DineInDto dineInDto) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Optional<MhLocation> existingLocation=locationDao.findById(dineInDto.getLocationId());
        if(existingLocation.isPresent()) {
            MhLocation location=existingLocation.get();
            Map<String, String> attributesMap = new HashMap<>();
            attributesMap.put("dineIn", dineInDto.getDineIn());
            attributesMap.put("highChair", dineInDto.getHighChair());
            attributesMap.put("interactiveDineIn", dineInDto.getInteractiveDineIn());
            attributesMap.put("merchant4DigitalValidation", dineInDto.getMerchant4DigitValidation());
            if(dineInDto.getCheckIn()!=null) {
                String attributesJson = objectMapper.writeValueAsString(dineInDto.getCheckIn());
                attributesMap.put("checkInDetails",attributesJson);
            }
            if(dineInDto.getReservation() != null) {
                String attributesJson = objectMapper.writeValueAsString(dineInDto.getReservation());
                attributesMap.put("reservationDetails",attributesJson);
            }
            String existingAttributes = existingLocation.get().getAttributes();
            JsonNode oldAttributes = existingAttributes != null ? objectMapper.readTree(existingAttributes) : objectMapper.createObjectNode();
            JsonNode mergeData = objectMapper.readerForUpdating(oldAttributes).readValue(objectMapper.writeValueAsString(attributesMap));
            location.setAttributes(objectMapper.writeValueAsString(mergeData));
            locationDao.save(location);
        }else {
            throw new Exception("Entity not found");
        }
        return ResponseEntity.status(HttpStatus.OK).body("Success");
    }

    @Override
    public ResponseEntity<String> savePickup(PickupDto pickupDto) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Optional<MhLocation> existingLocation=locationDao.findById(pickupDto.getLocationId());
        if(existingLocation.isPresent()) {

            MhLocation location=existingLocation.get();
            String attributesJson = objectMapper.writeValueAsString(pickupDto);
            System.out.println(attributesJson);
            Map<String, String> attributesMap = new HashMap<>();
            attributesMap.put("pickUpDetails",attributesJson);
            String existingAttributes = existingLocation.isPresent() ? existingLocation.get().getAttributes() : null;
            JsonNode oldAttributes = existingAttributes != null ? objectMapper.readTree(existingAttributes) : objectMapper.createObjectNode();
            JsonNode mergeData = objectMapper.readerForUpdating(oldAttributes).readValue(objectMapper.writeValueAsString(attributesMap));
            location.setAttributes(objectMapper.writeValueAsString(mergeData));

            locationDao.save(location);

        }else {
            throw new Exception("Entity not found");
        }
        return ResponseEntity.status(HttpStatus.OK).body("Success");

    }


    @Override
    public ResponseEntity<String> saveKitchen(KitchenDto kitchenDto) throws Exception {
        Optional<MhLocation> existingLocation = locationDao.findById(kitchenDto.getLocationId());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, false);
        if (existingLocation.isPresent()) {
            MhLocation location=existingLocation.get();
            String attributesJson = objectMapper.writeValueAsString(kitchenDto);
            Map<String, String> attributesMap = new HashMap<>();
            attributesMap.put("KitchenDetails",attributesJson);
            String existingAttributes = existingLocation.isPresent() ? existingLocation.get().getAttributes() : null;
            JsonNode oldAttributes = existingAttributes != null ? objectMapper.readTree(existingAttributes) : objectMapper.createObjectNode();
            JsonNode mergeData = objectMapper.readerForUpdating(oldAttributes).readValue(objectMapper.writeValueAsString(attributesMap));

            location.setAttributes(objectMapper.writeValueAsString(mergeData));

            locationDao.save(location);
        }
        else {
            throw new Exception("Entity not found");
        }
        return ResponseEntity.status(HttpStatus.OK).body("Success");
    }
}
