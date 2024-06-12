package com.example.Outlet_Management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
    private String locationId;
    private String name;
    private String startTime;
    private String endTime;
    private String weekday;
    private Integer isDefault;
    private Integer isEnabled;
    private String createdTime;
    private String modifiedTime;

}
