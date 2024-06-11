package com.example.Outlet_Management.Dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RestaurantDto {

    private String locationId;
    private String businessLegalName;
    private String phone;
    private String email;
    private String website;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String instagramLink;
    private String facebookLink;
    private String restaurantNumber;
    private String whatsappNumber;


}
