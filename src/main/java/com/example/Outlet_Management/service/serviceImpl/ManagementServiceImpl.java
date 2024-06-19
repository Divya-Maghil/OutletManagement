package com.example.Outlet_Management.service.serviceImpl;


import com.example.Outlet_Management.Dao.AvailabilityDao;
import com.example.Outlet_Management.Dao.LocationDao;
import com.example.Outlet_Management.Dto.*;
import com.example.Outlet_Management.entity.MhLocation;
import com.example.Outlet_Management.entity.MhMedia;
import com.example.Outlet_Management.entity.mhAvailability;
import com.example.Outlet_Management.error.AWSImageUploadFailedException;
import com.example.Outlet_Management.error.ImageNotFoundException;
import com.example.Outlet_Management.error.LocationNotFoundException;
import com.example.Outlet_Management.error.MerchantNotFoundException;
import com.example.Outlet_Management.mapper.LocationMapper;
import com.example.Outlet_Management.mapper.OnboardingMapper;
import com.example.Outlet_Management.mapper.RegistrationMapper;
import com.example.Outlet_Management.service.ImageService;
import com.example.Outlet_Management.service.ManagementService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.node.ArrayNode;


@Service
@Slf4j
@AllArgsConstructor
public class ManagementServiceImpl implements ManagementService {


    private LocationDao locationDao;
    private AvailabilityDao availabilityDao;
    private ImageService imageService;
    private RegistrationMapper registrationMapper;
    private OnboardingMapper onboardingMapper;
    private LocationMapper locationMapper;

    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public ResponseEntity<String> saveRegistration(RegistrationDTO registrationDTO) throws ImageNotFoundException, AWSImageUploadFailedException, JsonProcessingException {
        MhLocation newLocation = registrationMapper.toMhLocation(registrationDTO);
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode json = objectMapper.createObjectNode();
        json.put("gstNumber", registrationDTO.getGstNumber());
        String updatedJson = objectMapper.writeValueAsString(json);
        newLocation.setAttributes(updatedJson);
        locationDao.save(newLocation);
        imageService.storeImage(newLocation.getId(), registrationDTO.getBase64Image(),"LOGO");

        return ResponseEntity.status(HttpStatus.OK).body(newLocation.getId());
    }

    @Override
    public List<getLocation> getListOfLocation(String merchantId) throws MerchantNotFoundException {
             List<getLocation> locationList = entityManager.createQuery(
                "select new getLocation(l.Id,l.restaurantName) from MhLocation l where l.merchantId = :merchantId", getLocation.class)
                .setParameter("merchantId", merchantId)
                .getResultList();
             if(locationList.isEmpty()){
                 throw new MerchantNotFoundException("No locations found for merchant ID: " + merchantId);
             }
             return locationList;
    }


    @Override
    public List<GetDto> getData(String locationId) throws LocationNotFoundException {
        // Retrieve MhLocation entities
        List<MhLocation> locations = entityManager.createQuery(
                        "SELECT l FROM MhLocation l WHERE l.Id = :locationId", MhLocation.class)
                .setParameter("locationId", locationId)
                .getResultList();

        if (locations.isEmpty()) {
            throw new LocationNotFoundException("Invalid location");
        }
        List<String> locationIds = locations.stream().map(MhLocation::getId).toList();
        // Retrieve MhMedia entities for all locations
        List<MhMedia> mediaList = entityManager.createQuery(
                        "SELECT m FROM MhMedia m WHERE m.entityId IN :locationIds", MhMedia.class)
                .setParameter("locationIds", locationIds)
                .getResultList();

        // Retrieve mhAvailability entities for all locations
        List<mhAvailability> availabilityList = entityManager.createQuery(
                        "SELECT a FROM mhAvailability a WHERE a.locationId IN :locationIds", mhAvailability.class)
                .setParameter("locationIds", locationIds)
                .getResultList();

        return locations.stream()
                .map(location -> {
                    List<MhMedia> locationMedia = mediaList.stream()
                            .filter(media -> media.getEntityId().equals(location.getId()))
                            .collect(Collectors.toList());

                    List<mhAvailability> locationAvailability = availabilityList.stream()
                            .filter(availability -> availability.getLocationId().equals(location.getId()))
                            .collect(Collectors.toList());

                    return locationMapper.toDto(location, locationMedia, locationAvailability);
                }).toList();

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

        Optional<MhLocation> existingLocation = locationDao.findById(basicDetailsDto.getLocationId());

        if (existingLocation.isPresent()) {
            MhLocation location = existingLocation.get();

            if (!sessionDtos.isEmpty()) {
                for (RestaurantSessionDto sessionDto : sessionDtos) {
                    List<BasicTimeDto> basicTimeDtos = sessionDto.getBasicTime();
                    if (basicTimeDtos != null) {
                        for (BasicTimeDto basicTimeDto : basicTimeDtos) {
                            int count = basicTimeDto.getWeekday().size();
                            int len = basicTimeDto.getWeekday().size();

                            while (count > 0) {
                                mhAvailability availability = new mhAvailability();
                                availability.setName(sessionDto.getName());
                                availability.setId(UUID.randomUUID().toString());
                                availability.setLocationId(location.getId());
                                availability.setStartTime(basicTimeDto.getStart_time());
                                availability.setEndTime(basicTimeDto.getEnd_time());
                                availability.setWeekday(numberingDays(basicTimeDto.getWeekday().get(len - count)));
                                availabilityDao.save(availability);
                                count--;
                            }
                        }
                    }


                    ObjectNode attributesNode = objectMapper.createObjectNode();

                    if (basicDetailsDto.getCuisines() != null  &&  !basicDetailsDto.getCuisines().isEmpty()) {
                        ArrayNode cuisinesArray = objectMapper.valueToTree(basicDetailsDto.getCuisines());
                        attributesNode.set("cuisines", cuisinesArray);
                    }else   attributesNode.remove("cuisines");
                    if (basicDetailsDto.getAmenities() != null && !basicDetailsDto.getAmenities().isEmpty()) {
                        ArrayNode amenitiesArray = objectMapper.valueToTree(basicDetailsDto.getAmenities());
                        attributesNode.set("amenities", amenitiesArray);
                    }else   attributesNode.remove("amenities");
                    if (basicDetailsDto.getParking() != null && !basicDetailsDto.getParking().isEmpty()) {
                        ArrayNode parkingArray = objectMapper.valueToTree(basicDetailsDto.getParking());
                        attributesNode.set("parking", parkingArray);
                    }else{
                        attributesNode.remove("parking");
                    }
                    if (basicDetailsDto.getSafetyMeasures() != null) {
                        attributesNode.put("SafetyMeasures",basicDetailsDto.getSafetyMeasures());
                    }

                    String existingAttributes = location.getAttributes();
                    JsonNode oldAttributes = existingAttributes != null ? objectMapper.readTree(existingAttributes) : objectMapper.createObjectNode();
                    JsonNode mergeData = objectMapper.readerForUpdating(oldAttributes).readValue(attributesNode.toString());

                    location.setAttributes(objectMapper.writeValueAsString(mergeData));
                    locationDao.save(location);
                }

            }
        }else {
                throw new LocationNotFoundException();
            }

            return ResponseEntity.status(HttpStatus.OK).body("Success");
        }


    private String numberingDays(String weekday) {
        Map<String, String> dayMap = new HashMap<>();
        dayMap.put("monday", "1");
        dayMap.put("tuesday", "2");
        dayMap.put("wednesday", "3");
        dayMap.put("thursday", "4");
        dayMap.put("friday", "5");
        dayMap.put("saturday", "6");
        dayMap.put("sunday", "7");
        dayMap.put(null, "8");
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
            ObjectNode dineInDtoJsonNode = objectMapper.valueToTree(dineInDto);

            dineInDtoJsonNode.remove("locationId");

            ObjectNode attributesMapNode = objectMapper.createObjectNode();
            attributesMapNode.set("DineInDetails", dineInDtoJsonNode);

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







