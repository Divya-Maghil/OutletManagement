package com.example.Outlet_Management.repo;

import com.example.Outlet_Management.entity.mhAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Time;
import java.util.List;

@Repository
public interface AvailabilityRepo extends JpaRepository<mhAvailability,String> {


    List<mhAvailability> findAllByLocationId(String id);

//    List<mhAvailability> findByLocationIdAndNameAndWeekdayAndStartTimeAndEndTime(String id, String name, String weekday, Time startTime, Time endTime);


    @Query(value = "select * from mh_availability where id=:id and name=:name and weekday=:weekday and start_ime=:startTime and end_time=:endTime",nativeQuery = true)
    List<mhAvailability> getByLocationIdAndNameAndWeekdayAndStartTimeAndEndTime(String id, String name, String weekday, Time startTime, Time endTime);


    void delete(mhAvailability availability);
}
