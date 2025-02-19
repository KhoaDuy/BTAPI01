package com.example.project.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Username không được rỗng")
    private String username;

    @NotBlank(message = "Password không được rỗng")
    private String password;

    @NotBlank(message = "RetypedPassword không được rỗng")
    private String retypedPassword;

    @NotBlank(message = "FirstName không được rỗng")
    private String firstName;

    @NotBlank(message = "LastName không được rỗng")
    private String lastName;

    @NotBlank(message = "Email không được rỗng")
    @Pattern(
            regexp = "^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$",
            message = "Email không đúng định dạng"
    )
    private String email;
}
