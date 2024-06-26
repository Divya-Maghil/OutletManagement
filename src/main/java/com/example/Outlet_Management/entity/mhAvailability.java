package com.example.Outlet_Management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Time;
import java.sql.Timestamp;

@Entity
@AllArgsConstructor
@RequiredArgsConstructor
@Table(name = "mh_availability")
@Data
public class mhAvailability {
        @Id
        private String id;
        @Basic
        @Column(name = "location_id", nullable = false, length = 36)
        private String locationId;
        @Basic
        @Column(name = "name", nullable = false, length = 60)
        private String name;
        @Basic
        @Column(name = "start_time", nullable = false)
        private Time startTime;
        @Basic
        @Column(name = "end_time", nullable = false)
        private Time endTime;
        @Column(name = "weekday")
        private String weekday;
        @Basic
        @Column(name = "is_enabled", nullable = true, columnDefinition = "BIT")
        private Byte isEnabled;
        @Basic
        @Column(name = "created_time", nullable = false)
        @CreationTimestamp
        private Timestamp createdTime;
        @Basic
        @Column(name = "modified_time", nullable = true)
        @UpdateTimestamp
        private Timestamp modifiedTime;

}
