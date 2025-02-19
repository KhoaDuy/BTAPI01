package com.example.project.dtos.request;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequest {

    @NotBlank(message = "OtpToken không được trống")
    private String otpToken;

    @NotBlank(message = "NewPassword không được trống")
    private String newPassword;
}