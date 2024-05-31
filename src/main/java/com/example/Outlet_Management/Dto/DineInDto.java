package com.example.Outlet_Management.Dto;

import lombok.Data;


@Data
public class DineInDto {
    private String locationId;
    private String dineIn;
    private String highChair;
    private String interactiveDineIn;
    private String merchant4DigitValidation;
    private CheckInDto checkIn;
    private ReservationDto reservation;
}
