package com.example.Outlet_Management.Dto;

import lombok.Data;
import org.hibernate.sql.ast.SqlTreeCreationException;

@Data
public class RegistrationDTO {

    private String restaurantName; //Business_legal_name
    private String designation;
    private String name;
    private String phone;           //phone
    private String email;           //email
    private String gstNumber;
    private String base64Image;
}
