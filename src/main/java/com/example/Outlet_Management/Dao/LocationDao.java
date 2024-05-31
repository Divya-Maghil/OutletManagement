package com.example.Outlet_Management.Dao;

import com.example.Outlet_Management.entity.MhLocation;
import java.util.List;
import java.util.Optional;

public interface LocationDao {
    List<MhLocation> findByMerchantId(String id);

    void save(MhLocation newLocation);

    Optional<MhLocation> findById(String id);


    //void save(MhLocation );
}
