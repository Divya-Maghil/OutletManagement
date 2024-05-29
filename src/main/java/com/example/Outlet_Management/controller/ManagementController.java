package com.example.Outlet_Management.controller;

import com.example.Outlet_Management.Dto.GetDto;
import com.example.Outlet_Management.Dto.OnboardingDto;
import com.example.Outlet_Management.Dto.RegistrationDTO;
import com.example.Outlet_Management.entity.MhLocation;
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
    public ResponseEntity<String> Registration(@RequestBody RegistrationDTO registrationDTO) throws ImageNotFoundException, AWSImageUploadFailedException {
       return managementService.saveRegistration(registrationDTO);
   }

   @PostMapping("/onBoarding")
    public ResponseEntity<String> Onboarding(@RequestBody OnboardingDto onboardingDto) throws JsonProcessingException, AWSImageUploadFailedException {
        return managementService.onboarding(onboardingDto);
   }



}
