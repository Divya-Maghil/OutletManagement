package com.example.Outlet_Management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.UUID;

@Entity
@Table(name="mh_media")
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class MhMedia {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Basic
    @Column(name = "entity_id", nullable = true)
    private String entityId;
    @Basic
    @Column(name = "entity_type", nullable = true)
    private String entityType;
    @Basic
    @Column(name = "file_name", nullable = true)
    private String fileName;
    @Basic
    @Column(name = "mime_type", nullable = true)
    private String mimeType;
    @Basic
    @Column(name = "sort_order", nullable = true)
    private Integer sortOrder;
    @Basic
    @Column(name = "tag", nullable = true)
    private String tag;
    @Basic
    @Column(name = "created_time", nullable = false)
    @CreationTimestamp
    private java.util.Date createdTime;
    @Basic
    @Column(name = "modified_time", nullable = true)
    @UpdateTimestamp
    private java.util.Date modifiedTime;

}
