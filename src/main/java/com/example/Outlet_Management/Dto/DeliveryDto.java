package com.example.Outlet_Management.Dto;


import lombok.Data;

import java.util.List;

@Data
public class DeliveryDto {

    private String locationId;
    private List<DeliverySettingTime> deliverySettingTime;
    private List<String> deliveryPayment;
    private String scheduledDelivery;
    private String minimumOrderPrice;
    private String maximumOrderPrice;
    private String scheduledDeliveryDuration;
    private String packagingCharge;
    private DeliverySettingDto deliveryOption;

}
