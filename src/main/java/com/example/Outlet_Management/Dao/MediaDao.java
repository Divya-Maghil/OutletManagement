package com.example.Outlet_Management.Dao;


import com.example.Outlet_Management.entity.MhMedia;

import java.util.List;


public interface MediaDao {
    void saveMedia(MhMedia media);


    List<MhMedia> findByEntityId(String id);

}
