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
import com.example.Outlet_Management.error.LocationNotFoundException;
import com.example.Outlet_Management.service.ManagementService;
import com.example.Outlet_Management.util.Aws;
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
import com.fasterxml.jackson.databind.node.ArrayNode;

@Service
@Slf4j
public class ManagementServiceImpl implements ManagementService {

    @Autowired
    private LocationDao locationDao;

    @Autowired
    private MediaDao mediaDao;
    @Autowired
    private AvailabilityDao availabilityDao;
    @Autowired
    private AWSCredentials awsCredentials;


    @Override
    public ResponseEntity<String> saveRegistration(RegistrationDTO registrationDTO) throws ImageNotFoundException, AWSImageUploadFailedException, JsonProcessingException {

        String id = UUID.randomUUID().toString();
        MhLocation newLocation = new MhLocation();
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
        newLocation.setAttributes(updatedJson);


        if (registrationDTO.getBase64Image() != null) {
            byte[] image = Base64.getDecoder().decode(registrationDTO.getBase64Image());
            Tika tika = new Tika();
            String mimiType = tika.detect(image);
            Aws aws = new Aws();

            if (aws.uploadFileToS3(id, image, mimiType, awsCredentials.getACCESS_KEY(), awsCredentials.getSECRET_KEY(), awsCredentials.getBUCKET_NAME())) {
                MhMedia media = new MhMedia();
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
        return ResponseEntity.status(HttpStatus.OK).body(newLocation.getId());
    }

    @Override
    public List<GetDto> getData(String id) throws LocationNotFoundException {
        Optional<List<MhLocation>> optionalLocation = locationDao.findByMerchantId(id);
        System.out.println(optionalLocation);
        if (optionalLocation.get().isEmpty()) {
            System.out.println("here....");
            throw new LocationNotFoundException("Invalid Merchant");
        }
        List<MhLocation> location = optionalLocation.get();
        List<GetDto> resultList = new ArrayList<>();
        for (MhLocation locations : location) {
            GetDto returndto = new GetDto();

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
            List<MhMedia> mediaList = mediaDao.findByEntityId(locations.getId());

            if (mediaList != null && !mediaList.isEmpty()) {
                List<MediaDto> mediaDtoList = new ArrayList<>();
                for (MhMedia media : mediaList) {
                    MediaDto mediaDto = new MediaDto();
                    mediaDto.setEntityId(media.getEntityId());
                    mediaDto.setEntityType(media.getEntityType());
                    mediaDto.setFileName(media.getFileName());
                    mediaDto.setMimeType(media.getMimeType());
                    mediaDto.setSortOrder(media.getSortOrder());
                    mediaDto.setTag(media.getTag());
                    mediaDtoList.add(mediaDto);
                }
                returndto.setMedia(mediaDtoList);
            }
            List<mhAvailability> availabilityList = availabilityDao.findAllByEntityId(locations.getId());
            if (availabilityList != null) {
                List<AvailabilityDto> availabilityDtoList = new ArrayList<>();
                for (mhAvailability availability : availabilityList) {
                    AvailabilityDto availabilityDto = new AvailabilityDto();
                    availabilityDto.setName(availability.getName());
                    availabilityDto.setCreatedTime(availability.getCreatedTime());
                    availabilityDto.setEndTime(availability.getEndTime());
                    availabilityDto.setWeekDay(availability.getWeekday());
                    availabilityDtoList.add(availabilityDto);
                }
                returndto.setAvailabilityDtos(availabilityDtoList);
            }
            resultList.add(returndto);
        }
        return resultList;
    }


    @Override
    public ResponseEntity<String> onboarding(OnboardingDto onboardingDto) throws JsonProcessingException, AWSImageUploadFailedException, LocationNotFoundException {
        Optional<MhLocation> locationData = locationDao.findById(onboardingDto.getRestaurant_details().getId());

        String imageId = null;
        if (!locationData.isPresent()) {
            // return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Location not found");
            throw new LocationNotFoundException("location not present");
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
            String fileName = String.valueOf("cms_" + System.currentTimeMillis());
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

        ObjectNode attributesNode = objectMapper.createObjectNode();

        if (onboardingDto.getFssai_details().getIsEnabled().equalsIgnoreCase("yes")) {
            fssaiDetailsJson.setDocuments(imageId);
            JsonNode fssaiNode = objectMapper.valueToTree(fssaiDetailsJson);
            attributesNode.set("FSSAIDetails", fssaiNode);
        }
        if (onboardingDto.getBank_details() != null) {
            JsonNode bankNode = objectMapper.valueToTree(bank);
            attributesNode.set("BankDetails", bankNode);
        }

        attributesNode.put("RestaurantNumber", onboardingDto.getRestaurant_details().getRestaurantNumber());
        attributesNode.put("websiteLink", onboardingDto.getRestaurant_details().getWebsite());
        attributesNode.put("instagramLink", onboardingDto.getRestaurant_details().getInstagramLink());
        attributesNode.put("FaceBookLink", onboardingDto.getRestaurant_details().getFacebookLink());
        attributesNode.put("WhatsappNumber", onboardingDto.getRestaurant_details().getWhatsappNumber());

        String existingAttributes = location.getAttributes();
        JsonNode oldAttributes = existingAttributes != null ? objectMapper.readTree(existingAttributes) : objectMapper.createObjectNode();
        JsonNode mergeData = objectMapper.readerForUpdating(oldAttributes).readValue(attributesNode.toString());
        location.setAttributes(objectMapper.writeValueAsString(mergeData));
        locationDao.save(location);

        return ResponseEntity.status(HttpStatus.OK).body("Success");
    }


    @Override
    public ResponseEntity<String> saveBasic(BasicDetailsDto basicDetailsDto) throws Exception {
        final ObjectMapper objectMapper = new ObjectMapper();
        List<RestaurantSessionDto> sessionDtos = basicDetailsDto.getRestaurantSessionDto();
        if (sessionDtos.size() != 0) {
            for (RestaurantSessionDto sessionDto : sessionDtos) {
                int count = sessionDto.getWeekday().size();
                int len = sessionDto.getWeekday().size();
                while (count > 0) {
                    mhAvailability availability = new mhAvailability();
                    availability.setName(sessionDto.getName());
                    availability.setId(UUID.randomUUID().toString());
                    availability.setStartTime(sessionDto.getStart_time());
                    availability.setEndTime(sessionDto.getEnd_time());
                    availability.setLocationId(basicDetailsDto.getLocation_id());
                    availability.setWeekday(numberingDays(sessionDto.getWeekday().get(len - count)));
                    availabilityDao.save(availability);
                    count--;
                }
                Optional<MhLocation> existingLocation = locationDao.findById(basicDetailsDto.getLocation_id());

                if (existingLocation.isPresent()) {
                    MhLocation location = existingLocation.get();
                    ObjectNode attributesNode = objectMapper.createObjectNode();

                    if (basicDetailsDto.getCuisines() != null) {
                        ArrayNode cuisinesArray = objectMapper.valueToTree(basicDetailsDto.getCuisines());
                        attributesNode.set("cuisines", cuisinesArray);
                    }
                    if (basicDetailsDto.getAmenities() != null) {
                        ArrayNode amenitiesArray = objectMapper.valueToTree(basicDetailsDto.getAmenities());
                        attributesNode.set("amenities", amenitiesArray);
                    }
                    if (basicDetailsDto.getParking() != null) {
                        ArrayNode parkingArray = objectMapper.valueToTree(basicDetailsDto.getParking());
                        attributesNode.set("parking", parkingArray);
                    }
                    if (basicDetailsDto.getSafetyMeasures() != null) {
                        ArrayNode safetyMeasuresArray = objectMapper.valueToTree(basicDetailsDto.getSafetyMeasures());
                        attributesNode.set("safetyMeasures", safetyMeasuresArray);
                    }

                    String existingAttributes = location.getAttributes();
                    JsonNode oldAttributes = existingAttributes != null ? objectMapper.readTree(existingAttributes) : objectMapper.createObjectNode();
                    JsonNode mergeData = objectMapper.readerForUpdating(oldAttributes).readValue(attributesNode.toString());

                    location.setAttributes(objectMapper.writeValueAsString(mergeData));
                    locationDao.save(location);
                } else {
                    throw new Exception("Entity not found");
                }

            }
        }
        return ResponseEntity.status(HttpStatus.OK).body("Success");
    }

    private String numberingDays(String weekday) {
        Map<String, String> dayMap = new HashMap<>();
        dayMap.put("Monday", "1");
        dayMap.put("Tuesday", "2");
        dayMap.put("Wednesday", "3");
        dayMap.put("Thursday", "4");
        dayMap.put("Friday", "5");
        dayMap.put("Saturday", "6");
        dayMap.put("Sunday", "7");
        return dayMap.get(weekday);
    }


    @Override
    public ResponseEntity<String> saveRestaurantImg(RestaurantImgDto restaurantImgDTO) throws AWSImageUploadFailedException {
        if (restaurantImgDTO.getProfileImg() != null) {
            byte[] imageBytes = Base64.getDecoder().decode(restaurantImgDTO.getProfileImg());
            String fileName = String.valueOf("cms_" + System.currentTimeMillis());
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
        if (restaurantImgDTO.getRestaurantImgs() != null) {
            for (String restImg : restaurantImgDTO.getRestaurantImgs()) {
                byte[] imageBytes = Base64.getDecoder().decode(restImg);
                String fileName = String.valueOf("cms_" + System.currentTimeMillis());
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
                } catch (Exception e) {
                    throw new AWSImageUploadFailedException("Failed to upload image to AWS S3", e);
                }
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body("Success");
    }

    @Override
    public ResponseEntity<String> saveDineIn(DineInDto dineInDto) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode attributesNode = objectMapper.createObjectNode();
        Optional<MhLocation> existingLocation = locationDao.findById(dineInDto.getLocationId());
        if (existingLocation.isPresent()) {
            MhLocation location = existingLocation.get();
            Map<String, String> attributesMap = new HashMap<>();
            attributesMap.put("dineIn", dineInDto.getDineIn());
            attributesMap.put("highChair", dineInDto.getHighChair());
            attributesMap.put("interactiveDineIn", dineInDto.getInteractiveDineIn());
            attributesMap.put("merchant4DigitalValidation", dineInDto.getMerchant4DigitValidation());
            if (dineInDto.getCheckIn() != null) {
                JsonNode checkInNode = objectMapper.valueToTree(dineInDto.getCheckIn());
                attributesNode.set("CheckInDetails", checkInNode);
            }
            if (dineInDto.getReservation() != null) {
                JsonNode reservationNode = objectMapper.valueToTree(dineInDto.getReservation());
                attributesNode.set("ReservationDetails", reservationNode);
            }
            String existingAttributes = existingLocation.get().getAttributes();
            JsonNode oldAttributes = existingAttributes != null ? objectMapper.readTree(existingAttributes) : objectMapper.createObjectNode();
            JsonNode mergeData = objectMapper.readerForUpdating(oldAttributes).readValue(objectMapper.writeValueAsString(attributesMap));
            location.setAttributes(objectMapper.writeValueAsString(mergeData));
            locationDao.save(location);
        } else {
            throw new Exception("Entity not found");
        }
        return ResponseEntity.status(HttpStatus.OK).body("Success");
    }

    @Override
    public ResponseEntity<String> savePickup(PickupDto pickupDto) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Optional<MhLocation> existingLocation = locationDao.findById(pickupDto.getLocationId());
        if (existingLocation.isPresent()) {

            MhLocation location = existingLocation.get();
            ObjectNode pickupDtoJsonNode = objectMapper.valueToTree(pickupDto);
            // Remove the locationId field
            pickupDtoJsonNode.remove("locationId");
            ObjectNode attributesMapNode = objectMapper.createObjectNode();
            attributesMapNode.set("PickUpDetails", pickupDtoJsonNode);

            // Get existing attributes
            String existingAttributes = location.getAttributes();
            JsonNode oldAttributes = existingAttributes != null ? objectMapper.readTree(existingAttributes) : objectMapper.createObjectNode();

            // Merge new attributes with the existing attributes
            ((ObjectNode) oldAttributes).setAll(attributesMapNode);

            // Save merged attributes back to the location
            location.setAttributes(objectMapper.writeValueAsString(oldAttributes));

            locationDao.save(location);

        } else {
            throw new Exception("Entity not found");
        }
        return ResponseEntity.status(HttpStatus.OK).body("Success");

    }


    @Override
    public ResponseEntity<String> saveKitchen(KitchenDto kitchenDto) throws Exception {
        Optional<MhLocation> existingLocation = locationDao.findById(kitchenDto.getLocationId());
        ObjectMapper objectMapper = new ObjectMapper();
        if (existingLocation.isPresent()) {
            MhLocation location = existingLocation.get();
            ObjectNode kitchenDtoJsonNode = objectMapper.valueToTree(kitchenDto);
            // Remove the locationId field
            kitchenDtoJsonNode.remove("locationId");

            ObjectNode attributesMapNode = objectMapper.createObjectNode();
            attributesMapNode.set("KitchenDetails", kitchenDtoJsonNode);

            // Get existing attributes
            String existingAttributes = location.getAttributes();
            JsonNode oldAttributes = existingAttributes != null ? objectMapper.readTree(existingAttributes) : objectMapper.createObjectNode();

            // Merge new attributes with the existing attributes
            ((ObjectNode) oldAttributes).setAll(attributesMapNode);

            // Save merged attributes back to the location
            location.setAttributes(objectMapper.writeValueAsString(oldAttributes));
            locationDao.save(location);
        } else {
            throw new Exception("Entity not found");
        }
        return ResponseEntity.status(HttpStatus.OK).body("Success");
    }


    @Override
    public ResponseEntity<String> saveDelivery(DeliveryDto deliveryDto) throws Exception {

            Optional<MhLocation> existingLocation = locationDao.findById(deliveryDto.getLocationId());
            ObjectMapper objectMapper = new ObjectMapper();

            if (existingLocation.isPresent()) {
                MhLocation location = existingLocation.get();
                ObjectNode deliveryDtoJsonNode = objectMapper.valueToTree(deliveryDto);

                // Remove the locationId field
                deliveryDtoJsonNode.remove("locationId");

                if (deliveryDto.getDeliverySettingTime() != null) {
                    deliveryDtoJsonNode.set("deliverySettingTime", objectMapper.valueToTree(deliveryDto.getDeliverySettingTime()));
                }

                // Process deliverySetting
                if (deliveryDto.getDeliveryOption() != null) {
                    DeliverySettingDto deliverySetting = deliveryDto.getDeliveryOption();


                    // Handle inHouse
                    if (deliverySetting.getInHouse() != null) {
                        Boolean isInHouseEnabled = deliverySetting.getInHouse().getIsEnabled();
                        if (Boolean.TRUE.equals(isInHouseEnabled)) {
                            deliveryDtoJsonNode.set("inHouse", objectMapper.valueToTree(deliverySetting.getInHouse()));
                        } else {
                            deliveryDtoJsonNode.put("isInHouseEnabled", false);
                        }
                    } else {
                        deliveryDtoJsonNode.put("isInHouseEnabled", false);
                    }

                    // Handle thirdParty
                    if (deliverySetting.getThirdParty() != null) {
                        Boolean isThirdPartyEnabled = deliverySetting.getThirdParty().getIsEnabled();
                        if (Boolean.TRUE.equals(isThirdPartyEnabled)) {
                            deliveryDtoJsonNode.set("thirdParty", objectMapper.valueToTree(deliverySetting.getThirdParty()));
                        } else {
                            deliveryDtoJsonNode.put("isThirdPartyEnabled", false);
                        }
                    } else {
                        deliveryDtoJsonNode.put("isThirdPartyEnabled", false);
                    }

                    deliveryDtoJsonNode.remove("deliveryOption");


                  ObjectNode attributesMapNode = objectMapper.createObjectNode();
                  attributesMapNode.set("DeliveryDetails", deliveryDtoJsonNode);

                    // Get existing attributes
                    String existingAttributes = location.getAttributes();
                    JsonNode oldAttributes = existingAttributes != null ? objectMapper.readTree(existingAttributes) : objectMapper.createObjectNode();

                    // Merge new attributes with the existing attributes
                    ((ObjectNode) oldAttributes).setAll(attributesMapNode);

                    // Save merged attributes back to the location
                    location.setAttributes(objectMapper.writeValueAsString(oldAttributes));
                    locationDao.save(location);

                    return ResponseEntity.status(HttpStatus.OK).body("Success");
                }
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Entity not found");
            }

    }


