package com.example.userserver.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRes { // 회원가입 응답

    private String userId;
    private String name;
    private String email;
    private String greeting;

}
