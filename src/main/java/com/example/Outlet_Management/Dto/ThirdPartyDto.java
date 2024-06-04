package com.example.Outlet_Management.Dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.List;

@Data
@Getter
@Setter
public class ThirdPartyDto {

    private Boolean isEnabled;
    private List<String> thirdPartyList;
    private String swiggyId;

//    public Boolean getIsEnabled() {
//        return isEnabled;
//    }
}
