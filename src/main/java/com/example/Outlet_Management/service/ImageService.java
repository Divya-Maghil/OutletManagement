package com.example.Outlet_Management.service;

import com.example.Outlet_Management.error.AWSImageUploadFailedException;
import com.example.Outlet_Management.error.ImageNotFoundException;

public interface ImageService {

    String storeImage(String entityId, String base64Image, String entityType) throws AWSImageUploadFailedException, ImageNotFoundException;
}
