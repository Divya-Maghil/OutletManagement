package com.example.Outlet_Management.controller;

import com.example.Outlet_Management.Dto.*;
import com.example.Outlet_Management.error.AWSImageUploadFailedException;
import com.example.Outlet_Management.error.ImageNotFoundException;
import com.example.Outlet_Management.error.LocationNotFoundException;
import com.example.Outlet_Management.service.ManagementService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/outlets")
@CrossOrigin("*")
public class ManagementController {
    @Autowired
    private ManagementService managementService;


    @GetMapping("/outlet/{merchantId}")
    public List<GetDto> getLocation(@PathVariable String merchantId) throws LocationNotFoundException {
        return managementService.getData(merchantId);
    }

    @PostMapping("/outlet/registration")
    public ResponseEntity<String> Registration(@Valid @RequestBody RegistrationDTO registrationDTO) throws ImageNotFoundException, AWSImageUploadFailedException, JsonProcessingException {
       return managementService.saveRegistration(registrationDTO);
   }

   @PostMapping("/outlet/onBoarding")
    public ResponseEntity<String> Onboarding(@Valid @RequestBody OnboardingDto onboardingDto) throws JsonProcessingException, AWSImageUploadFailedException, LocationNotFoundException, ImageNotFoundException {
        return managementService.onboarding(onboardingDto);
   }

   @PostMapping("/outlet/basicDetails/properties")
    public ResponseEntity<String> postBasicDetails(@RequestBody BasicDetailsDto basicDetailsDto) throws Exception {
        return managementService.saveBasic(basicDetailsDto);
   }

   @PostMapping("/outlet/restImg/properties")
    public ResponseEntity<String> saveImg(@RequestBody RestaurantImgDto restaurantImgDTO) throws AWSImageUploadFailedException, ImageNotFoundException {
        return managementService.saveRestaurantImg(restaurantImgDTO);
   }
   @PostMapping("/outlet/dineIn/properties")
    public ResponseEntity<String> saveDineIn(@RequestBody DineInDto dineInDto) throws Exception {
        return managementService.saveDineIn(dineInDto);
   }
   @PostMapping("/outlet/pickUp/properties")
    public ResponseEntity<String> savePickup(@RequestBody PickupDto pickupDto) throws Exception {
        return managementService.savePickup(pickupDto);
   }

   @PostMapping("/outlet/kitchen/properties")
    public ResponseEntity<String> saveKitchen(@RequestBody KitchenDto kitchenDto) throws Exception {
        return managementService.saveKitchen(kitchenDto);
   }

   @PostMapping("/outlet/delivery/properties")
    public ResponseEntity<String> saveDelivery(@RequestBody DeliveryDto deliveryDto) throws Exception {
        return managementService.saveDelivery(deliveryDto);
   }
}
