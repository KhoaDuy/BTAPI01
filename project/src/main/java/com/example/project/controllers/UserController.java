package com.example.project.controllers;

import com.example.project.dtos.request.LoginRequest;
import com.example.project.dtos.request.RegisterRequest;
import com.example.project.dtos.request.ResetPasswordRequest;
import com.example.project.dtos.response.SuccessResponse;
import com.example.project.dtos.response.UserResponse;
import com.example.project.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<SuccessResponse> login(@Valid @RequestBody LoginRequest loginRequest){
        return ResponseEntity.ok().body(SuccessResponse.builder()
                .message("Đăng nhập thành công")
                .status(HttpStatus.OK.value())
                .data(userService.login(loginRequest))
                .build());
    }

    @PostMapping("/register")
    public ResponseEntity<SuccessResponse> register(@Valid @RequestBody RegisterRequest registerRequest){

        UserResponse userResponse = userService.register(registerRequest);
        this.sendMailVerify(userResponse.getEmail());
        return ResponseEntity.ok().body(SuccessResponse.builder()
                .message("Xác thực user")
                .status(HttpStatus.OK.value())
                .build());
    }

    @GetMapping("/send-mail-verify/{email}")
    public ResponseEntity<SuccessResponse> sendMailVerify(@PathVariable("email") String email){
        userService.sendMailActive(email);
        return ResponseEntity.ok().body(SuccessResponse.builder()
                .message("Gửi yêu cầu xác thực user thành công")
                .status(HttpStatus.OK.value())
                .build());
    }

    @GetMapping("/verify-account/{email}/{otp}")
    public ResponseEntity<SuccessResponse> verifyAccount(@PathVariable("email") String email,
                                                         @PathVariable("otp") String otp){
        return ResponseEntity.ok().body(SuccessResponse.builder()
                .message("Xác thực user thành công")
                .status(HttpStatus.OK.value())
                .data(userService.verifyUser(email, otp))
                .build());
    }

    @PostMapping("/verify-mail/{email}")
    public ResponseEntity<SuccessResponse> sendMailForgotPassword(@PathVariable String email){
        userService.sendMailForgotPassword(email);
        return ResponseEntity.ok().body(SuccessResponse.builder()
                .message("Gửi yêu cầu quên mật khẩu thành công")
                .status(HttpStatus.OK.value())
                .build());
    }

    @PostMapping("/verify-otp/{otp}/{email}")
    public ResponseEntity<SuccessResponse> verifyOtp(@PathVariable("otp") String otp,
                                                     @PathVariable("email") String email){
        return ResponseEntity.ok().body(SuccessResponse.builder()
                .message("Xác thực mã OTP thành công")
                .status(HttpStatus.OK.value())
                .data(userService.verifyOtp(otp, email))
                .build());
    }

    @PutMapping("/reset-password/{id}")
    public ResponseEntity<SuccessResponse> resetPassword(@PathVariable long id,
                                                         @Valid @RequestBody ResetPasswordRequest resetPasswordRequest){
        userService.resetPassword(id, resetPasswordRequest);
        return ResponseEntity.ok().body(SuccessResponse.builder()
                .message("Đặt lại mật khẩu thành công")
                .status(HttpStatus.OK.value())
                .build());
    }




}
