package com.example.Outlet_Management.Dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OnboardingDto {
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private RestaurantDto restaurant_details;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private FssaiDto fssai_details;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private BankDto bank_details;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private LocationDto location_Details;

}
