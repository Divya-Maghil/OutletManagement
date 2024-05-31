package com.example.Outlet_Management.Dto;

import lombok.Data;

@Data
public class CheckInDto {
    private String maximumPeopleAllowedOnline;
    private String maximumPeopleAllowedOffline;
    private String autoAssign;

}
