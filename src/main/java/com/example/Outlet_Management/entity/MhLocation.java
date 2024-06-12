package com.example.Outlet_Management.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Table(name="mh_location")
@Data
@AllArgsConstructor(staticName = "build")
@RequiredArgsConstructor
public class MhLocation {


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(length=36)
    private String merchantId;  //location id uuid
    @Column(length = 36)
    private String restaurantName;
    @Column(length = 36)
    private String name;
    @Column(length=20)
    private String phone;
    @Column(length=60)
    @Email
    private String email;
    @Column(length=128)
    private String addressLine1;
    @Column(length=128)
    private String addressLine2;
    @Column(length=128)
    private String addressLine3;
    @Column(length=128)
    private String city;
    @Column(length=10)
    private String state;
    @Column(length=10)
    private String pinCode;
    @Column(length=20)
    private String country;
    @Column(columnDefinition = "JSON")
    private String attributes;


}
