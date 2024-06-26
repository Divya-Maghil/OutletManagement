package com.example.Outlet_Management.Dto;

import lombok.Data;

import java.sql.Time;
import java.util.List;

@Data
public class BasicTimeDto {

    private Time start_time;
    private Time end_time;
    private List<String> weekday;
}
