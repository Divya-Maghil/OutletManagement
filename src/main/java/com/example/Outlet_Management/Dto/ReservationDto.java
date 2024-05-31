package com.example.Outlet_Management.Dto;

import lombok.Data;
import java.util.List;

@Data
public class ReservationDto {

    private String minimumPeopleAllowed;
    private String maximumPeopleAllowed;
    private String reservationServiceTimeFrom;
    private String reservationServiceTimeTo;
    private List<String> days;
    private Integer bufferDays;
}
