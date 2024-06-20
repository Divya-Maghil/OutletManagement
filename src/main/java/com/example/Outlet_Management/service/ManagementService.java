package com.example.Outlet_Management.service;

import com.example.Outlet_Management.Dto.*;
import com.example.Outlet_Management.error.AWSImageUploadFailedException;
import com.example.Outlet_Management.error.ImageNotFoundException;
import com.example.Outlet_Management.error.LocationNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;
import java.util.List;


public interface ManagementService {

    ResponseEntity<String> saveRegistration(RegistrationDTO registrationDTO) throws ImageNotFoundException, AWSImageUploadFailedException, JsonProcessingException;

    List<GetDto> getData(String id) throws LocationNotFoundException;

    ResponseEntity<String> onboarding(OnboardingDto onboardingDto) throws JsonProcessingException, AWSImageUploadFailedException, LocationNotFoundException, ImageNotFoundException;

    ResponseEntity<String> saveBasic(BasicDetailsDto basicDetailsDto) throws Exception;

    ResponseEntity<String> saveRestaurantImg(RestaurantImgDto restaurantImgDTO) throws AWSImageUploadFailedException, ImageNotFoundException;

    ResponseEntity<String> saveDineIn(DineInDto dineInDto) throws Exception;

    ResponseEntity<String> savePickup(PickupDto pickupDto) throws Exception;

    ResponseEntity<String> saveKitchen(KitchenDto kitchenDto) throws Exception;

    ResponseEntity<String> saveDelivery(DeliveryDto deliveryDto) throws Exception;

    List<GetLocationDto> getListOfLocation(String merchantId);
}
