package com.example.userserver.common.config;

import com.example.userserver.common.filter.AuthenticationFilter;
import com.example.userserver.common.filter.JwtFilter;
import com.example.userserver.common.filter.LoginFilter;
import com.example.userserver.common.util.JwtUtil;
import com.example.userserver.repository.RefreshTokenRepository;
import com.example.userserver.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public SecurityConfig(AuthenticationConfiguration authenticationConfiguration, JwtUtil jwtUtil,
                          RefreshTokenRepository refreshTokenRepository, UserRepository userRepository) {

        this.authenticationConfiguration = authenticationConfiguration;
        this.jwtUtil = jwtUtil;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {

        return configuration.getAuthenticationManager();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers(new AntPathRequestMatcher("/h2-console/**"))
                .requestMatchers(new AntPathRequestMatcher("/favicon.ico"))
                .requestMatchers(new AntPathRequestMatcher("/css/**"))
                .requestMatchers(new AntPathRequestMatcher("/img/**"));
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
                .csrf(AbstractHttpConfigurer::disable);

        httpSecurity
                .formLogin(AbstractHttpConfigurer::disable);

        httpSecurity
                .httpBasic(AbstractHttpConfigurer::disable);

        // 인증 인가 관련
        httpSecurity
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/users/signup", "/users/email-certification",
                                "/users/reissue", "/users/internal/**", "/users/test/**", "/main"
                        ).permitAll()
                        .requestMatchers("/admin").hasRole("ADMIN")
                        .anyRequest().authenticated()
                );

        // 세션 설정
        httpSecurity
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

//        httpSecurity
//                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

//        httpSecurity
//                .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil),
//                        UsernamePasswordAuthenticationFilter.class);

        LoginFilter loginFilter = new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil,
                refreshTokenRepository, userRepository);
        loginFilter.setFilterProcessesUrl("/users/login");

        httpSecurity
                .addFilterBefore(new AuthenticationFilter(), LoginFilter.class); // 로그인 필터 앞단에 배치

//        httpSecurity
//                .addFilterBefore(new JwtFilter(jwtUtil), LoginFilter.class); // 로그인 필터 앞단에 배치

        httpSecurity
                .addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();

    }

//    @Bean
//    public FilterRegistrationBean<JwtFilter> jwtFilterFilterRegistrationBean() {
//        FilterRegistrationBean<JwtFilter> registrationBean = new FilterRegistrationBean<>();
//        registrationBean.setFilter(jwtFilter);
//        registrationBean.addUrlPatterns("/users/*"); // 필터를 어떤 URL에 적용할지 지정
//        return registrationBean;
//    }


}
