package com.example.Outlet_Management.service;

import com.example.Outlet_Management.Dto.*;
import com.example.Outlet_Management.error.AWSImageUploadFailedException;
import com.example.Outlet_Management.error.ImageNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;
import java.util.List;


public interface ManagementService {

    ResponseEntity<String> saveRegistration(RegistrationDTO registrationDTO) throws ImageNotFoundException, AWSImageUploadFailedException, JsonProcessingException;

    List<GetDto> getData(String id);

    ResponseEntity<String> onboarding(OnboardingDto onboardingDto) throws JsonProcessingException, AWSImageUploadFailedException;

    ResponseEntity<String> saveBasic(BasicDetailsDto basicDetailsDto) throws Exception;

    ResponseEntity<String> saveRestaurantImg(RestaurantImgDto restaurantImgDTO) throws AWSImageUploadFailedException;

    ResponseEntity<String> saveDineIn(DineInDto dineInDto) throws Exception;

    ResponseEntity<String> savePickup(PickupDto pickupDto) throws Exception;

    ResponseEntity<String> saveKitchen(KitchenDto kitchenDto) throws Exception;
}
