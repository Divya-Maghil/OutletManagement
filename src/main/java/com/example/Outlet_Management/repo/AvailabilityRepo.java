package com.example.Outlet_Management.repo;

import com.example.Outlet_Management.entity.mhAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AvailabilityRepo extends JpaRepository<mhAvailability,String> {


    List<mhAvailability> findAllByLocationId(String id);

    List<mhAvailability> findByLocationIdAndNameAndWeekdayAndStartTimeAndEndTime(String id, String name, String s, String startTime, String endTime);

    void delete(mhAvailability availability);
}
