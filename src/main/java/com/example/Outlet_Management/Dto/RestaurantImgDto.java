package com.example.Outlet_Management.Dto;

import lombok.Data;

import java.util.List;

@Data
public class RestaurantImgDto {
    private String locationId;
    private String profileImg;
    private List<String> restaurantImgs;
}
