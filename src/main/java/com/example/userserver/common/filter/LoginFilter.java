package com.example.userserver.common.filter;

import com.example.userserver.common.util.JwtUtil;
import com.example.userserver.domain.entity.RefreshToken;
import com.example.userserver.repository.RefreshTokenRepository;
import com.example.userserver.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    private final UserRepository userRepository;

    public LoginFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil, RefreshTokenRepository refreshTokenRepository,
                       UserRepository userRepository) {

        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

//        String email = obtainUsername(request);
//        String password = obtainPassword(request);

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        System.out.println(email);
        System.out.println(password);

        // 인증 객체에 저장
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, password,
                null);

        return authenticationManager.authenticate(authToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) {

        // 유저 정보를 꺼냄
        String email = authentication.getName();
        String userId = userRepository.findByEmail(email).get().getUserId(); // UUID

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        // 토큰 생성
        String access = jwtUtil.createJwt("access",userId,role); // 10 minutes
        String refresh = jwtUtil.createJwt("refresh",userId,role); // 24 hours

        // Refresh token 저장
        addRefreshTokenToDB(userId,refresh,86400000L);

        // 응답 설정
        response.setHeader("access",access);
        response.addCookie(createCookie("refresh",refresh));
        response.setStatus(HttpStatus.OK.value());
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {

        response.setStatus(401);
    }

    private Cookie createCookie(String key, String value){
        Cookie cookie = new Cookie(key,value);
        cookie.setMaxAge(24*60*60); // 24 hours
        //  cookie.setSecure(true); // https 통신 할때 설정
        cookie.setHttpOnly(true);
        return cookie;
    }

    private void addRefreshTokenToDB(String userId, String refresh, Long expiredMs){
        Date date = new Date(System.currentTimeMillis()+expiredMs);

        RefreshToken token = RefreshToken
                .builder()
                .userId(userId)
                .token(refresh)
                .expiredDate(date.toString())
                .build();

        refreshTokenRepository.save(token);
    }


}
