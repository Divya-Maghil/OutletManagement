package com.example.Outlet_Management.Dao.DaoImpl;

import com.example.Outlet_Management.Dao.LocationDao;
import com.example.Outlet_Management.entity.MhLocation;
import com.example.Outlet_Management.repo.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class LocationDaoImpl implements LocationDao {

    @Autowired
    private LocationRepository locationRepo;

    @Override
    public List<MhLocation> findByMerchantId(String id) {
        return locationRepo.findByMerchantId(id);
    }

    @Override
    public void save(MhLocation newLocation) {

        locationRepo.save(newLocation);
    }
    @Override
    public Optional<MhLocation> findById(String id){
        return locationRepo.findById(id);
    }
}
