package com.example.Outlet_Management.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "mh_location_delivery_providers")
@AllArgsConstructor
@RequiredArgsConstructor
public class mhLocationDeliveryProviders {
    @Id
    private String deliveryProviderId;
    private String locationId;
    private String attributes;
    private String isDefault;
    private String createdTime;
    private String modifiedTime;
}
