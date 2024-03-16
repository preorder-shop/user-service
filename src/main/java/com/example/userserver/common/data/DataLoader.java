package com.example.userserver.common.data;

import com.example.userserver.domain.entity.User;
import com.example.userserver.repository.UserRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class DataLoader implements ApplicationRunner {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder encoder;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        User user = User.builder()
                .userId(UUID.randomUUID().toString())
                .name("홍길동")
                .email("test@naver.com")
                .password(encoder.encode("1234"))
                .role("ROLE_USER")
                .greeting("hello world~!")
                .build();

        userRepository.save(user);
        log.info("유저 데이터 초기화");

    }
}
