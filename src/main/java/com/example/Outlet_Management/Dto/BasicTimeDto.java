package com.example.Outlet_Management.Dto;

import lombok.Data;

import java.util.List;

@Data
public class BasicTimeDto {

    private String start_time;
    private String end_time;
    private List<String> weekday;
}
