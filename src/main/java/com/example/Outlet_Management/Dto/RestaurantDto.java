package com.example.Outlet_Management.Dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RestaurantDto {

    private String id;
    private String businessLegalName;
    private String phone;
    private String email;
    private String website;
    private String instagramLink;
    private String facebookLink;
    private String restaurantNumber;
    private String whatsappNumber;
}
