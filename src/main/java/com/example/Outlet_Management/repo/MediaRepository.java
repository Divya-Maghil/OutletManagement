package com.example.Outlet_Management.repo;

import com.example.Outlet_Management.entity.MhMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MediaRepository extends JpaRepository<MhMedia,String> {
    List<MhMedia> findAllByEntityId(String id);

}
