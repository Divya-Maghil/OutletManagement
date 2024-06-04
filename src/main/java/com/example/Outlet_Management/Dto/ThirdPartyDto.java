package com.example.Outlet_Management.Dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.List;

@Data
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ThirdPartyDto {

    private Boolean isEnabled;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> thirdPartyList;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String dunzoId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String doorDashId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String uberEatsId;


}
