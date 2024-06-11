package com.example.Outlet_Management.Dto;

import lombok.Data;

import java.util.List;

@Data
public class RestaurantSessionDto {
    private String name;
    private List<BasicTimeDto> BasicTime;
}
