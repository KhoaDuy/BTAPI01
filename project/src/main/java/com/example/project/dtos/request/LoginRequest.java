package com.example.project.dtos.request;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "Username không được rỗng")
    private String username;

    @NotBlank(message = "Password không được rỗng")
    private String password;

}
