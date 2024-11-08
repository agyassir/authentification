package com.example.auth_spring_security.Exception;

import org.springframework.http.HttpStatus;

public class AlreadyExistException extends RuntimeException{
    public String getError() {
        return "This Record Already Exist";
    }
    public HttpStatus getCode(){
        return HttpStatus.FOUND;
    }
}