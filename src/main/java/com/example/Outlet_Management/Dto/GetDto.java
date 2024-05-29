package com.example.Outlet_Management.Dto;

import lombok.Data;

@Data
public class GetDto {
    private String id;
    private String merchantId;
    private String restaurantName;
    private String name;
    private String phone;
    private String email;
    private String addressLine1;
    private String addressLine2;
    private String addressLine3;
    private String city;
    private String state;
    private String pinCode;
    private String country;
    private String attributes;
    private MediaDto media;
}
