package com.example.Outlet_Management.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.sql.ast.SqlTreeCreationException;

@Data
@AllArgsConstructor(staticName = "build")
public class RegistrationDTO {
    private String locationId;
    private String restaurantName;
    private String designation;
    private String name;
    private String phone;           //phone
    private String email;           //email
    private String gstNumber;
    private String base64Image;
}
