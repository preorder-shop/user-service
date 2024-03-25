package com.example.userserver.service;


import com.example.userserver.common.exceptions.BaseException;
import com.example.userserver.common.response.BaseResponseStatus;
import com.example.userserver.domain.dto.CustomUserDetails;
import com.example.userserver.domain.entity.User;
import com.example.userserver.repository.UserRepository;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {

        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("loadUserByUsername email = {}",email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(()->new BaseException(BaseResponseStatus.INVALID_LOGIN));

            return new CustomUserDetails(user);

    }
}
