package com.example.userserver.service;

import static org.junit.jupiter.api.Assertions.*;

import com.example.userserver.common.util.JwtUtil;
import com.example.userserver.provider.EmailProvider;
import com.example.userserver.repository.EmailRedisRepository;
import com.example.userserver.repository.RefreshTokenRepository;
import com.example.userserver.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

class UserServiceTest {


    @DisplayName("Mock를 사용해 service 단에서 유저 회원가입이 잘 되는지 확인한다.")
    @Test
    void createUser(){
        //given
        UserService userService = new UserService(
                Mockito.mock(BCryptPasswordEncoder.class),
                Mockito.mock(EmailProvider.class),
                Mockito.mock(UserRepository.class),
                Mockito.mock(EmailRedisRepository.class),
                Mockito.mock(JwtUtil.class),
                Mockito.mock(RefreshTokenRepository.class),
                Mockito.mock(S3Service.class)

        );
        //when
        String givenEmail = "둘리";
        String code = "";

        //then

    }
}