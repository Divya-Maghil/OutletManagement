package com.example.Outlet_Management.mapper;

import com.example.Outlet_Management.Dto.RegistrationDTO;
import com.example.Outlet_Management.entity.MhLocation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface RegistrationMapper {
    RegistrationMapper INSTANCE = Mappers.getMapper(RegistrationMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "merchantId", constant = "8dfe7674-709d-431c-a233-628e839ecc76")
    @Mapping(target = "attributes", ignore = true)
     MhLocation toMhLocation(RegistrationDTO registrationDTO);

}
