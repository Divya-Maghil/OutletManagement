package com.example.Outlet_Management.error;

import com.example.Outlet_Management.entity.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@ResponseStatus
public class RestResponseEntityException extends ResponseEntityExceptionHandler{

    @ExceptionHandler(AWSImageUploadFailedException.class)
    public ResponseEntity<ErrorMessage> awsImageUploadFailed(AWSImageUploadFailedException exception , WebRequest request){
        ErrorMessage errorMessage = new ErrorMessage();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
    }


    @ExceptionHandler(ImageNotFoundException.class)
    public ResponseEntity<ErrorMessage> imageNotPresentException(ImageNotFoundException exception , WebRequest request){
        ErrorMessage errorMessage = new ErrorMessage(HttpStatus.NOT_FOUND,exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
    }

    @ExceptionHandler(LocationNotFoundException.class)
    public ResponseEntity<ErrorMessage> locationNotFoundException(LocationNotFoundException exception,WebRequest request){
        ErrorMessage errorMessage=new ErrorMessage(HttpStatus.NOT_FOUND, exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
    }
}
