package com.example.Outlet_Management.Dto;

import lombok.Data;

@Data
public class InHouseDto {

    private Boolean isEnabled;
    private String cashOnDelivery;
    private String batchOrder;
    private String defaultCountOfBatchOrder;
    private String feesStructure;
    private String initial2MileAmount;
    private String additional1MileAmount;
    private String maximumRadius;

}
