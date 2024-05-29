package com.example.Outlet_Management.Dto;

import lombok.Data;

@Data
public class OnboardingDto {

        private RestaurantDto restaurant_details;
        private FssaiDto Fssai_details;
        private BankDto bank_details;
        private LocationDto location_Details;

}
