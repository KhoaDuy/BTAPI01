package com.example.project.services;

import com.example.project.dtos.request.LoginRequest;
import com.example.project.dtos.request.RegisterRequest;
import com.example.project.dtos.request.ResetPasswordRequest;
import com.example.project.dtos.response.UserResponse;
import com.example.project.dtos.response.VerifyOtpResponse;

public interface UserService {
    UserResponse login(LoginRequest loginRequest);

    UserResponse register(RegisterRequest registerRequest);

    UserResponse verifyUser(String email, String otp);

    void sendMailActive(String email);

    void sendMailForgotPassword(String email);
    VerifyOtpResponse verifyOtp(String otp, String email);
    void resetPassword(long id, ResetPasswordRequest ResetPasswordRequest);
}
