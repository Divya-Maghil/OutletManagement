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
import com.example.Outlet_Management.mapper.LocationMapper;
import com.example.Outlet_Management.mapper.OnboardingMapper;
import com.example.Outlet_Management.mapper.RegistrationMapper;
import com.example.Outlet_Management.service.ImageService;
import com.example.Outlet_Management.service.ManagementService;
import com.example.Outlet_Management.util.Aws;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

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
    @Autowired
    private ImageService imageService;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private OnboardingMapper onboardingMapper;





    @Override
    public ResponseEntity<String> saveRegistration(RegistrationDTO registrationDTO) throws ImageNotFoundException, AWSImageUploadFailedException, JsonProcessingException {
        MhLocation newLocation = RegistrationMapper.INSTANCE.toMhLocation(registrationDTO);
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode json = objectMapper.createObjectNode();
        json.put("gstNumber", registrationDTO.getGstNumber());
        String updatedJson = objectMapper.writeValueAsString(json);
        newLocation.setAttributes(updatedJson);
        locationDao.save(newLocation);
        imageService.storeImage(newLocation.getId(), registrationDTO.getBase64Image(),"LOGO");

        return ResponseEntity.status(HttpStatus.OK).body(newLocation.getId());
    }





    public List<GetDto> getData(String merchantId) throws LocationNotFoundException {
        // Retrieve MhLocation entities
        List<MhLocation> locations = entityManager.createQuery(
                        "SELECT l FROM MhLocation l WHERE l.merchantId = :merchantId", MhLocation.class)
                .setParameter("merchantId", merchantId)
                .getResultList();

        if (locations.isEmpty()) {
            throw new LocationNotFoundException("Invalid Merchant");
        }

        // Retrieve MhMedia entities for all locations
        List<MhMedia> mediaList = entityManager.createQuery(
                        "SELECT m FROM MhMedia m WHERE m.entityId IN :locationIds", MhMedia.class)
                .setParameter("locationIds", locations.stream().map(MhLocation::getId).collect(Collectors.toList()))
                .getResultList();

        // Retrieve mhAvailability entities for all locations
        List<mhAvailability> availabilityList = entityManager.createQuery(
                        "SELECT a FROM mhAvailability a WHERE a.locationId IN :locationIds", mhAvailability.class)
                .setParameter("locationIds", locations.stream().map(MhLocation::getId).collect(Collectors.toList()))
                .getResultList();

        // Map entities to DTOs using MapStruct
        return locations.stream()
                .map(location -> LocationMapper.INSTANCE.toDto(location, mediaList, availabilityList))
                .collect(Collectors.toList());
    }



    @Override
    public ResponseEntity<String> onboarding(OnboardingDto onboardingDto) throws JsonProcessingException, AWSImageUploadFailedException, LocationNotFoundException, ImageNotFoundException {
        Optional<MhLocation> locationData = locationDao.findById(onboardingDto.getRestaurant_details().getLocationId());

        if (!locationData.isPresent()) {
            throw new LocationNotFoundException("Location not present");
        }

        MhLocation location = locationData.get();
        onboardingMapper.updateMhLocationFromOnboardingDto(onboardingDto, location);

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode attributesNode = objectMapper.createObjectNode();

        if (onboardingDto.getFssai_details() != null) {
            FssaiDto fssaiDetailsJson = onboardingDto.getFssai_details();


            if (onboardingDto.getFssai_details().getDocuments() != null && !onboardingDto.getFssai_details().getDocuments().isEmpty()) {
                String fileName=imageService.storeImage(onboardingDto.getRestaurant_details().getLocationId(), onboardingDto.getFssai_details().getDocuments(), "FSSAI_DOCUMENT");
                fssaiDetailsJson.setDocuments(fileName);
                JsonNode fssaiNode = objectMapper.valueToTree(onboardingDto.getFssai_details());
                attributesNode.set("FSSAIDetails", fssaiNode);
            }
        }

        if (onboardingDto.getBank_details() != null) {
            JsonNode bankNode = objectMapper.valueToTree(onboardingDto.getBank_details());
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
    public ResponseEntity<String> saveBasic(BasicDetailsDto basicDetailsDto) throws LocationNotFoundException, JsonProcessingException {
        final ObjectMapper objectMapper = new ObjectMapper();
        List<RestaurantSessionDto> sessionDtos = basicDetailsDto.getRestaurantSessionDto();
        if (!sessionDtos.isEmpty()) {
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
                    throw new LocationNotFoundException();
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
    public ResponseEntity<String> saveRestaurantImg(RestaurantImgDto restaurantImgDTO) throws AWSImageUploadFailedException, ImageNotFoundException {
        if (restaurantImgDTO.getProfileImg() != null) {
            imageService.storeImage(restaurantImgDTO.getLocationId(),restaurantImgDTO.getProfileImg(),"Profile_Image");

        }
        if (restaurantImgDTO.getRestaurantImgs() != null) {
            for (String restImg : restaurantImgDTO.getRestaurantImgs()) {
                imageService.storeImage(restaurantImgDTO.getLocationId(),restImg,"Restaurant_Image");
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body("Success");
    }



    @Override
    public ResponseEntity<String> saveDineIn(DineInDto dineInDto) throws LocationNotFoundException, JsonProcessingException {
        Optional<MhLocation> existingLocation = locationDao.findById(dineInDto.getLocationId());
        ObjectMapper objectMapper = new ObjectMapper();
        if (existingLocation.isPresent()) {
            MhLocation location = existingLocation.get();
            ObjectNode kitchenDtoJsonNode = objectMapper.valueToTree(dineInDto);

            kitchenDtoJsonNode.remove("locationId");

            ObjectNode attributesMapNode = objectMapper.createObjectNode();
            attributesMapNode.set("DineInDetails", kitchenDtoJsonNode);

            mergeAttributes(location,attributesMapNode);

        } else {
            throw new LocationNotFoundException();
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

           mergeAttributes(location,attributesMapNode);


        } else {
            throw new LocationNotFoundException();
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
            kitchenDtoJsonNode.remove("locationId");

            ObjectNode attributesMapNode = objectMapper.createObjectNode();
            attributesMapNode.set("KitchenDetails", kitchenDtoJsonNode);
            mergeAttributes(location,attributesMapNode);


        } else {
            throw new LocationNotFoundException();
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
                    DeliverySettingDto deliveryOption = deliveryDto.getDeliveryOption();

                    // Handle inHouse
                    if (deliveryOption.getInHouse() != null && Boolean.TRUE.equals(deliveryOption.getInHouse().getIsEnabled())) {
                            deliveryDtoJsonNode.set("inHouse", objectMapper.valueToTree(deliveryOption.getInHouse()));
                    }
                    else {
                        deliveryDtoJsonNode.put("isInHouseEnabled", false);
                    }
                    // Handle thirdParty
                    if (deliveryOption.getThirdParty() != null && (Boolean.TRUE.equals(deliveryOption.getThirdParty().getIsEnabled()))) {
                        //ObjectNode thirdPartyNode = objectMapper.valueToTree(deliverySetting.getThirdParty());
                        deliveryDtoJsonNode.set("thirdParty", objectMapper.valueToTree(deliveryOption.getThirdParty()));
                    }
                    else {
                        deliveryDtoJsonNode.put("isThirdPartyEnabled", false);
                    }

                    deliveryDtoJsonNode.remove("deliveryOption");

                }
                  ObjectNode attributesMapNode = objectMapper.createObjectNode();
                  attributesMapNode.set("DeliveryDetails", deliveryDtoJsonNode);

                  mergeAttributes(location,attributesMapNode);
                  return ResponseEntity.status(HttpStatus.OK).body("Success");
                }
        throw new LocationNotFoundException();
    }

    public void mergeAttributes(MhLocation location,ObjectNode attributesMapNode) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String existingAttributes = location.getAttributes();
        JsonNode oldAttributes = existingAttributes != null ? objectMapper.readTree(existingAttributes) : objectMapper.createObjectNode();

        // Merge new attributes with the existing attributes
        ((ObjectNode) oldAttributes).setAll(attributesMapNode);

        // Save merged attributes back to the location
        location.setAttributes(objectMapper.writeValueAsString(oldAttributes));

        locationDao.save(location);
    }

}







