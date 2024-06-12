package com.example.Outlet_Management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

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
    @NotBlank(message = "please send the location id.,")
    private String entityId;
    private String entityType;
    private String fileName;
    private String mimeType;
    private Integer sortOrder;
    private String tag;

}
