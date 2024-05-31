package com.example.Outlet_Management.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name="mh_media")
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class MhMedia {
    @Id
    private String id;
    private String entityId;
    private String entityType;
    private String fileName;
    private String mimeType;
    private Integer sortOrder;
    private String tag;

}
