package com.example.project.dtos.response;

import lombok.*;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VerifyOtpResponse {
    private Long userId;
    private String otpToken;
}