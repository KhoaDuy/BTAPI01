package com.example.project.exceptions;

public class InvalidParamException extends RuntimeException{
    public InvalidParamException(String message){
        super(message);
    }
}
