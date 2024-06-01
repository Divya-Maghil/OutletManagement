package com.example.Outlet_Management.Dao.DaoImpl;

import com.example.Outlet_Management.Dao.AvailabilityDao;
import com.example.Outlet_Management.entity.mhAvailability;
import com.example.Outlet_Management.repo.AvailabilityRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AvailabilityDaoImpl implements AvailabilityDao {
    @Autowired
    private AvailabilityRepo availabilityRepo;
    @Override
    public void save(mhAvailability availability) {
        availabilityRepo.save(availability);
    }

    @Override
    public List<mhAvailability> findAllByEntityId(String id) {
        return availabilityRepo.findAllByLocationId(id);
    }
}
