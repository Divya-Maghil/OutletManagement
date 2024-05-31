package com.example.Outlet_Management.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BankDto {
    @JsonProperty("accountNumber")
    private String accountNumber;
    @JsonProperty("ifscCode")
    private String ifscCode;
    @JsonProperty("AccountHolderName")
    private String AccountHolderName;
}
