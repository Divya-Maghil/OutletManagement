package com.example.Outlet_Management.mapper;

import com.example.Outlet_Management.Dto.AvailabilityDto;
import com.example.Outlet_Management.Dto.GetDto;
import com.example.Outlet_Management.Dto.MediaDto;
import com.example.Outlet_Management.entity.MhLocation;
import com.example.Outlet_Management.entity.MhMedia;
import com.example.Outlet_Management.entity.mhAvailability;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface LocationMapper {

    LocationMapper INSTANCE = Mappers.getMapper(LocationMapper.class);

    @Mappings({
            @Mapping(source = "location.id", target = "id"),
            @Mapping(source = "location.merchantId", target = "merchantId"),
            @Mapping(source = "mediaList", target = "media"),
            @Mapping(source = "availabilityList", target = "availabilityDtos")
    })
    GetDto toDto(MhLocation location, List<MhMedia> mediaList, List<mhAvailability> availabilityList);

    MediaDto toMediaDto(MhMedia media);

    AvailabilityDto toAvailabilityDto(mhAvailability availability);
}
