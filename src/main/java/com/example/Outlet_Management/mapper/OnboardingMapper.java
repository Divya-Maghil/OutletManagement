package com.example.Outlet_Management.mapper;

import com.example.Outlet_Management.Dto.OnboardingDto;
import com.example.Outlet_Management.entity.MhLocation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface OnboardingMapper {
    @Mapping(source = "restaurant_details.businessLegalName", target = "restaurantName")
    @Mapping(source = "restaurant_details.phone", target = "phone")
    @Mapping(source = "restaurant_details.email", target = "email")
    @Mapping(source = "location_Details.address", target = "addressLine1")
    @Mapping(source = "location_Details.city", target = "city")
    @Mapping(source = "location_Details.state", target = "state")
    @Mapping(source = "location_Details.pinCode", target = "pinCode")
    @Mapping(source = "location_Details.country", target = "country")
    void updateMhLocationFromOnboardingDto(OnboardingDto onboardingDto, @MappingTarget MhLocation location);

    MhLocation toMhLocation(OnboardingDto onboardingDto);
}
