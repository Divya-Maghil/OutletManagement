package com.example.Outlet_Management.Dao;

import com.example.Outlet_Management.Dto.GetLocationDto;
import com.example.Outlet_Management.entity.MhLocation;
import java.util.List;
import java.util.Optional;

public interface LocationDao {
    Optional<List<MhLocation>> findByMerchantId(String id);

    void save(MhLocation newLocation);

    Optional<MhLocation> findById(String id);

   // List<GetLocationDto> findLocationDetailsByMerchantId(String merchantId);


    //void save(MhLocation );
}
