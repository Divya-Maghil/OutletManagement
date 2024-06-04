package com.example.Outlet_Management.Dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DineInDto {
    private String locationId;
    private String dineIn;
    private String highChair;
    private String interactiveDineIn;
    private String merchant4DigitValidation;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private CheckInDto checkIn;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ReservationDto reservation;
}
