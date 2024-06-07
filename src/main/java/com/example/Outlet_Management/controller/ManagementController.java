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


    @GetMapping("/{id}")
    public List<GetDto> getLocation(@PathVariable String id) throws LocationNotFoundException {
        return managementService.getData(id);
    }

    @PostMapping("/registration")
    public ResponseEntity<String> Registration(@Valid @RequestBody RegistrationDTO registrationDTO) throws ImageNotFoundException, AWSImageUploadFailedException, JsonProcessingException {
       return managementService.saveRegistration(registrationDTO);
   }

   @PostMapping("/onBoarding")
    public ResponseEntity<String> Onboarding(@Valid @RequestBody OnboardingDto onboardingDto) throws JsonProcessingException, AWSImageUploadFailedException, LocationNotFoundException, ImageNotFoundException {
        return managementService.onboarding(onboardingDto);
   }

   @PostMapping("/basicDetails/properties")
    public ResponseEntity<String> postBasicDetails(@RequestBody BasicDetailsDto basicDetailsDto) throws Exception {
        return managementService.saveBasic(basicDetailsDto);
   }

   @PostMapping("/restImg/properties")
    public ResponseEntity<String> saveImg(@RequestBody RestaurantImgDto restaurantImgDTO) throws AWSImageUploadFailedException, ImageNotFoundException {
        return managementService.saveRestaurantImg(restaurantImgDTO);
   }
   @PostMapping("/dineIn/properties")
    public ResponseEntity<String> saveDineIn(@RequestBody DineInDto dineInDto) throws Exception {
        return managementService.saveDineIn(dineInDto);
   }
   @PostMapping("/pickUp/properties")
    public ResponseEntity<String> savePickup(@RequestBody PickupDto pickupDto) throws Exception {
        return managementService.savePickup(pickupDto);
   }

   @PostMapping("/kitchen/properties")
    public ResponseEntity<String> saveKitchen(@RequestBody KitchenDto kitchenDto) throws Exception {
        return managementService.saveKitchen(kitchenDto);
   }

   @PostMapping("/delivery/properties")
    public ResponseEntity<String> saveDelivery(@RequestBody DeliveryDto deliveryDto) throws Exception {
        return managementService.saveDelivery(deliveryDto);
   }
}
