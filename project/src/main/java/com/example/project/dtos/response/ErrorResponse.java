package com.example.project.dtos.response;

import lombok.*;

@Getter
@Builder
public class ErrorResponse {
    private int status;
    private String message;
}
