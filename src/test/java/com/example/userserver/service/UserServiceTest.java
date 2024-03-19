package com.example.userserver.service;

import static org.junit.jupiter.api.Assertions.*;

import ch.qos.logback.core.testUtil.MockInitialContext;
import com.example.userserver.common.util.JwtUtil;
import com.example.userserver.domain.dto.request.SignUpReq;
import com.example.userserver.provider.EmailProvider;
import com.example.userserver.repository.EmailRedisRepository;
import com.example.userserver.repository.RefreshTokenRepository;
import com.example.userserver.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

class UserServiceTest {


    @DisplayName("Mock를 사용해 service 단에서 유저 회원가입이 잘 되는지 확인한다.")
    @Test
    void createUserMockTest() {
        //given
        BCryptPasswordEncoder bCryptPasswordEncoder = Mockito.mock(BCryptPasswordEncoder.class);
        EmailProvider emailProvider = Mockito.mock(EmailProvider.class);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        EmailRedisRepository emailRedisRepository = Mockito.mock(EmailRedisRepository.class);
        JwtUtil jwtUtil = Mockito.mock(JwtUtil.class);
        RefreshTokenRepository refreshTokenRepository = Mockito.mock(RefreshTokenRepository.class);
        S3Service s3Service = Mockito.mock(S3Service.class);

        UserService userService = new UserService(
                bCryptPasswordEncoder,
                emailProvider,
                userRepository,
                emailRedisRepository,
                jwtUtil,
                refreshTokenRepository,
                s3Service
        );

        String givenEmail = "test@naver.com";
        String givenName = "test";
        String givenPassword = "1234";
        String givenGreeting = "hello world";

        SignUpReq request = new SignUpReq(givenEmail, givenName, givenPassword, givenGreeting);
        userService.createUser(request);

        Mockito.verify(bCryptPasswordEncoder,Mockito.times(1)).encode(Mockito.any());
        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any());
        Mockito.verify(refreshTokenRepository, Mockito.times(0)).save(Mockito.any());
        Mockito.verify(emailProvider, Mockito.times(0)).sendCertificationMail(Mockito.any(),Mockito.any());

    }
}