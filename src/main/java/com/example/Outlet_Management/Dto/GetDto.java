package com.example.Outlet_Management.Dto;

import com.example.Outlet_Management.entity.MhLocation;
import lombok.Data;
import java.util.*;

@Data
public class GetDto {

    private MhLocation location;
    private List<MediaDto> media;
    private List<AvailabilityDto> availabilityDtos;
}
