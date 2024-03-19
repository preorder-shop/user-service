package com.example.userserver.domain.dto.request;

import com.example.userserver.domain.entity.User;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class SignUpReq {

    @NotNull(message = "Email cannot be null")
    @Size(min = 2,message = "Email not be less than two characters")
    private String email;

//    @NotNull(message = "certification code cannot be null")
//    private String code;
    @NotNull(message = "name cannot be null")
    private String name;

    @NotNull(message = "Password cannot be null")
    @Size(min = 4,message = "Password must be greater than 4 characters")
    private String password;

    private String greeting;

    public User toEntity(String encryptedPw) {
        return User
                .builder()
                .userId(UUID.randomUUID().toString())
                .name(this.name)
                .email(this.email)
                .password(encryptedPw)
                .role("ROLE_USER")
                .greeting(greeting)
                .build();
    }

}
