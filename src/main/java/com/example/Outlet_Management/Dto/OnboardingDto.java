package com.example.Outlet_Management.Dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OnboardingDto {

        private RestaurantDto restaurant_details;
        private FssaiDto Fssai_details;
        private BankDto bank_details;
        private LocationDto location_Details;

}
