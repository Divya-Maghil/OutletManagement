package com.example.Outlet_Management.service;

import com.example.Outlet_Management.Dto.GetDto;
import com.example.Outlet_Management.Dto.OnboardingDto;
import com.example.Outlet_Management.Dto.RegistrationDTO;
import com.example.Outlet_Management.entity.MhLocation;
import com.example.Outlet_Management.error.AWSImageUploadFailedException;
import com.example.Outlet_Management.error.ImageNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;


public interface ManagementService {
   // List<MhLocation> getLocations(String id);

    ResponseEntity<String> saveRegistration(RegistrationDTO registrationDTO) throws ImageNotFoundException, AWSImageUploadFailedException;

    List<GetDto> getData(String id);

    ResponseEntity<String> onboarding(OnboardingDto onboardingDto) throws JsonProcessingException, AWSImageUploadFailedException;
}
