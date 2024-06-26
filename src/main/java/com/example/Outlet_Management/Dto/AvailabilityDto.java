package com.example.Outlet_Management.Dto;

import lombok.Data;

import java.sql.Time;

@Data
public class AvailabilityDto {
    private String createdTime;
    private Time endTime;
    private String name;
    private String startTime;
    private String weekday;

}
