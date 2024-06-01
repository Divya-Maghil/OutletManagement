package com.example.Outlet_Management.Dao;

import com.example.Outlet_Management.entity.mhAvailability;

import java.util.List;

public interface AvailabilityDao {

    void save(mhAvailability availability);

    List<mhAvailability> findAllByEntityId(String id);
}
