package com.example.userserver.domain.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter @Setter
public class GetUserInfoRes {

    private Long id;
    private String username;
    private String email;
    private String greeting;
    private String profileImg;
}
