package com.example.Outlet_Management.controller;

import com.example.Outlet_Management.Dto.*;
import com.example.Outlet_Management.error.AWSImageUploadFailedException;
import com.example.Outlet_Management.error.ImageNotFoundException;
import com.example.Outlet_Management.service.ManagementService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/outlet")
@CrossOrigin("*")
public class ManagementController {
    @Autowired
    private ManagementService managementService;


    @GetMapping("/{id}")
    public List<GetDto> getLocation(@PathVariable String id){
        return managementService.getData(id);
    }

    @PostMapping("/registration")
    public ResponseEntity<String> Registration(@RequestBody RegistrationDTO registrationDTO) throws ImageNotFoundException, AWSImageUploadFailedException, JsonProcessingException {
       return managementService.saveRegistration(registrationDTO);
   }

   @PostMapping("/onBoarding")
    public ResponseEntity<String> Onboarding(@RequestBody OnboardingDto onboardingDto) throws JsonProcessingException, AWSImageUploadFailedException {
        return managementService.onboarding(onboardingDto);
   }

   @PostMapping("/basicDetails")
    public ResponseEntity<String> postBasicDetails(@RequestBody BasicDetailsDto basicDetailsDto) throws Exception {
        return managementService.saveBasic(basicDetailsDto);
   }

   @PostMapping("/restImg")
    public ResponseEntity<String> saveImg(@RequestBody RestaurantImgDto restaurantImgDTO) throws AWSImageUploadFailedException {
        return managementService.saveRestaurantImg(restaurantImgDTO);
   }
   @PostMapping("/dineIn")
    public ResponseEntity<String> saveDineIn(@RequestBody DineInDto dineInDto) throws Exception {
        return managementService.saveDineIn(dineInDto);
   }
   @PostMapping("/pickUp")
    public ResponseEntity<String> savePickup(@RequestBody PickupDto pickupDto) throws Exception {
        return managementService.savePickup(pickupDto);
   }

   @PostMapping("/kitchen")
    public ResponseEntity<String> saveKitchen(@RequestBody KitchenDto kitchenDto) throws Exception {
        return managementService.saveKitchen(kitchenDto);
   }
}
