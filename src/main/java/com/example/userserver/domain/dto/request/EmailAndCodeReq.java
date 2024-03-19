package com.example.userserver.domain.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
public class EmailAndCodeReq {

    @NotNull(message = "email cannot be null")
    @Email
    private String email;

    @NotNull(message = "code cannot be null")
    private String code;
}
