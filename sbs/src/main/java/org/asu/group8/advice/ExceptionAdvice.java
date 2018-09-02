package org.asu.group8.advice;

import org.asu.group8.model.ExceptionResponse;
import org.asu.group8.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@ControllerAdvice
public class ExceptionAdvice {

    @Autowired
    private LogService logService;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> invalidInput(MethodArgumentNotValidException exception) {

        exception.printStackTrace();

        // log the stack trace of the exception
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        exception.printStackTrace(printWriter);

        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode("Validation Error");
        response.setErrorMessage("Invalid inputs.");
        response.setErrors(fromBindingErrors(exception.getBindingResult()));

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<Object> generalException (Exception exception) throws Exception{

        exception.printStackTrace();

        // log the stack trace of the exception
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        exception.printStackTrace(printWriter);

        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
        response.setErrorMessage("The server encountered an error!");
        response.setErrors(new ArrayList<>());

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private List<String> fromBindingErrors(Errors errors) {
        List<String> validErrors = new ArrayList<String>();
        for (ObjectError objectError : errors.getAllErrors()) {
            validErrors.add(objectError.getDefaultMessage());
        }
        return validErrors;
    }
}
