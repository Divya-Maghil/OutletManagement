package com.example.Outlet_Management.error;

public class MerchantNotFoundException extends Exception{
    public MerchantNotFoundException() {
        super();
    }

    public MerchantNotFoundException(String message) {
        super(message);
    }

    public MerchantNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public MerchantNotFoundException(Throwable cause) {
        super(cause);
    }

    protected MerchantNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
