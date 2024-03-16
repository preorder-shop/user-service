package com.example.userserver.domain.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class LoginReq {

    @NotNull(message = "Email cannot be null")
    @Email(message = "이메일 양식을 지켜주세요")
    private String email;

    @NotNull(message = "Password cannot be null")
    private String password;
}
