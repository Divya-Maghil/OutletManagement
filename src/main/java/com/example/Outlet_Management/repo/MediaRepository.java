package com.example.Outlet_Management.repo;

import com.example.Outlet_Management.entity.MhMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MediaRepository extends JpaRepository<MhMedia,String> {
    MhMedia findByEntityId(String id);
}
