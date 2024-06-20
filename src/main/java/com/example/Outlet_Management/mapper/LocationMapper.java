package com.example.Outlet_Management.mapper;

import com.example.Outlet_Management.Dto.AvailabilityDto;
import com.example.Outlet_Management.Dto.GetDto;
import com.example.Outlet_Management.Dto.LocationDto;
import com.example.Outlet_Management.Dto.MediaDto;
import com.example.Outlet_Management.entity.mhAvailability;
import com.example.Outlet_Management.entity.MhLocation;
import com.example.Outlet_Management.entity.MhMedia;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import java.util.List;


@Mapper(componentModel = "spring")
public interface LocationMapper {

    @Mappings({
            @Mapping(source = "location",target = "location"),
            @Mapping(source = "mediaList", target = "media"),
            @Mapping(source = "availabilityList", target = "availabilityDtos")
    })
    GetDto toDto(MhLocation location, List<MediaDto> mediaList, List<AvailabilityDto> availabilityList);

    MediaDto toMediaDto(MhMedia media);

    AvailabilityDto toAvailabilityDto(mhAvailability availability);


}
