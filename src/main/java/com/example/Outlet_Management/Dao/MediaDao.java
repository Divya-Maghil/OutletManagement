package com.example.Outlet_Management.Dao;


import com.example.Outlet_Management.entity.MhMedia;



public interface MediaDao {
    void saveMedia(MhMedia media);

 //   MhMedia findByEntityId(String id);
 //   MhMedia findByEntityId(String id);

    MhMedia findByEntityId(String id);

  //  void save(MhMedia media);


    // findByMerchantId(String id);
}
