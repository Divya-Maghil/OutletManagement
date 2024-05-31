package com.example.Outlet_Management.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Entity
@AllArgsConstructor
@RequiredArgsConstructor
@Table(name = "mh_availability")
@Data
public class mhAvailability {
    @Id
    private String id;
    private String location_id;
    private String name;
    private String start_time;
    private String end_time;
    private String weekday;
    private Integer is_default;
    private Integer is_enabled;
    private String created_time;
    private String modified_time;
}
