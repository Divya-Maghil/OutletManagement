package com.example.Outlet_Management.Dto;

import lombok.Data;

@Data
public class MediaDto {
    private String entityId;
    private String entityType;
    private String fileName;
    private String mimeType;
    private Integer sortOrder;
    private String tag;
}
