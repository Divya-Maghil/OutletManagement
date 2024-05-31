package com.example.Outlet_Management.Dto;

import lombok.Data;

import java.util.List;

@Data
public class PickupDto {
    private String locationId;
    private String serviceTimeFrom;
    private String serviceTimeTo;
    private List<String> payment;
    private String scheduledDuration;
    private String packagingCharge;
    private String eta;

}
