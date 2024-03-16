package com.example.userserver.common.filter;

import com.example.userserver.common.util.JwtUtil;
import com.example.userserver.domain.dto.CustomUserDetails;
import com.example.userserver.domain.entity.User;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil) {

        this.jwtUtil = jwtUtil;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        log.info(">>jwtFilter<<");

        String accessToken = request.getHeader("access");

        if(accessToken==null){ // jwt 토큰이 없다면 다음 filter 진행
            log.info("jwt token is null");
            log.info("do next filter");
            filterChain.doFilter(request,response); // 인증/인가가 필요없는 경로라면 다음 필터로 넘어가도 통과가 될것임.
            return; // 다음 필터로 넘어가고 해당 필터는 더이상 진행할게 없으므로 return 문으로 끝냄
        }


        // jwt 토큰이 만료되었을 경우에 대해서 예외처리를 한다.
        try{
            jwtUtil.isExpired(accessToken);
        }catch (ExpiredJwtException e){

            // response body
            PrintWriter writer = response.getWriter();
            writer.println("this token is expired");

            // response status code
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // client 와 협의한 특정 코드를 보내서 client 에서 refresh token 을 다시 요청하도록 하자
            return;  // 다음 필터로 넘어가지 않고 종료 (인증/인가가 필요한 요청을 보냈는데 해당 access token 이 유효하지 않은것이므로)
        }

        //  access 토큰 인지 확인
        String category = jwtUtil.getCategory(accessToken);

        if(!category.equals("access")){
            // response body
            PrintWriter writer = response.getWriter();
            writer.print("invalid access token");

            // response status code
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // id, role 획득 -> 일시적인 세션 생성
        String userId = jwtUtil.getUserId(accessToken);
        String role = jwtUtil.getRole(accessToken);

        User user = User
                .builder()
                .userId(userId)
                .role(role)
                .build();

   //     CustomUserDetails customUserDetails = new CustomUserDetails(user);
   //     Authentication auth = new UsernamePasswordAuthenticationToken(customUserDetails,null,customUserDetails.getAuthorities());
        Authentication auth = new UsernamePasswordAuthenticationToken(userId,null,null);
        SecurityContextHolder.getContext().setAuthentication(auth);

        log.info("토큰 인증 성공");
        filterChain.doFilter(request,response);



    }
}
