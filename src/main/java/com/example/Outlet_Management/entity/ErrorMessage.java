package com.example.Outlet_Management.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpStatus;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class ErrorMessage {

    private HttpStatus status;
    private String message;

}

