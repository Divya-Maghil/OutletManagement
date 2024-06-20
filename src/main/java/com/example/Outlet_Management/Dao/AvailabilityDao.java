package com.example.Outlet_Management.Dao;

import com.example.Outlet_Management.entity.mhAvailability;

import java.util.List;

public interface AvailabilityDao {

    void save(mhAvailability availability);

    List<mhAvailability> findAllByEntityId(String id);

 List<mhAvailability> findByLocationIdAndNameAndWeekdayAndStartTimeAndEndTime(String id, String name, String s, String startTime, String endTime);

    List<mhAvailability> findByLocationId(String id);

    void delete(mhAvailability availability);
}
