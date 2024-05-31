package com.example.Outlet_Management.repo;

import com.example.Outlet_Management.entity.mhAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AvailabilityRepo extends JpaRepository<mhAvailability,String> {
}
