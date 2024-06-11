package com.example.Outlet_Management.error;



public class AWSImageUploadFailedException extends Exception{
    public AWSImageUploadFailedException() {
        super();
    }

    public AWSImageUploadFailedException(String message) {

        super(message);
    }

    public AWSImageUploadFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public AWSImageUploadFailedException(Throwable cause) {
        super(cause);
    }

    protected AWSImageUploadFailedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }


}
