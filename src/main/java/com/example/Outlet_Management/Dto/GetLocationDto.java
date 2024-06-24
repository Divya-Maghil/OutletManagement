package com.example.Outlet_Management.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetLocationDto {

    private String locationId;
    private String restaurantName;
}
