package com.example.userserver.controller;

import static com.example.userserver.common.response.BaseResponseStatus.*;

import com.example.userserver.common.exceptions.BaseException;
import com.example.userserver.common.response.BaseResponse;
import com.example.userserver.common.util.JwtUtil;
import com.example.userserver.domain.dto.TokenDto;
import com.example.userserver.domain.dto.request.EmailCertificationReq;
import com.example.userserver.domain.dto.request.SignUpReq;
import com.example.userserver.domain.dto.response.SignUpRes;
import com.example.userserver.domain.dto.response.UserDto;
import com.example.userserver.service.S3Service;
import com.example.userserver.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/users")
@RestController
public class UserController {

    private final UserService userService;
    private final S3Service s3Service;

    @PostMapping("/signup")
    public BaseResponse<SignUpRes> createUser(@RequestBody SignUpReq signUpReq){

        // 형식적 validation
        checkUsernameValidation(signUpReq.getName());
        checkEmailValidation(signUpReq.getEmail());
        checkCodeValidation(signUpReq.getCode());
        checkPasswordValidation(signUpReq.getPassword());
        checkGreetingValidation(signUpReq.getGreeting());

        SignUpRes signUpRes = userService.createUser(signUpReq);

        return new BaseResponse<>(signUpRes);
    }

    @GetMapping("")
    public BaseResponse<UserDto> getUserInfo(){
        String userId = getUserIdFromAuthentication();
        log.info("UserController getUserInfo userId : {}",userId);
        UserDto userDto = userService.getUserInfo(userId);

        return new BaseResponse<>(userDto);

    }

    @PostMapping("/email-certification") // 사용자의 이메일로 인증코드를 정송해 해당 이메일이 사용가능한 메일인지 검사
    public BaseResponse<String> emailCertificate(@RequestBody EmailCertificationReq emailCertificationReq){

        String result = userService.emailCertificate(emailCertificationReq);

        return new BaseResponse<>(result);
    }

    @PatchMapping("")
    public BaseResponse<String> patchUserInfo(MultipartFile profileImage, String name, String greeting){

        String userId = getUserIdFromAuthentication();
        log.info("UserController patchUserInfo userId :{}",userId);
        String img_url=null;

        if(profileImage!=null && !profileImage.isEmpty()){
            img_url = s3Service.uploadImage(profileImage);
        }

        String result = userService.patchUserInfo(userId, name, greeting, img_url);

        return new BaseResponse<>(result);
    }

    @PostMapping("/reissue") // access token 재발급
    public BaseResponse<?> reissue(HttpServletRequest request, HttpServletResponse response){
        String refreshToken = getRefreshInCookie(request);
        log.info("UserController reissue refreshToken :{}",refreshToken);

        if(refreshToken==null){
            throw new BaseException(REFRESH_INVALID);
        }
        log.info("refresh token: {}",refreshToken);

        TokenDto tokenDto = userService.reissueToken(refreshToken);
        response.setHeader("access",tokenDto.getAccessToken());
        response.addCookie(createCookie("refresh",tokenDto.getRefreshToken()));

        return new BaseResponse<>("토큰 재발급을 완료했습니다.");

    }

    @GetMapping("/logout") // 해당 브라우저 로그아웃
    public BaseResponse<String> logout(HttpServletRequest request, HttpServletResponse response){ // -> 로그아웃 요청시 access token + refresh token 도 같이 보내도록함.

        String refreshToken = getRefreshInCookie(request);

        if(refreshToken==null){
            throw new BaseException(REFRESH_INVALID);
        }

        userService.logout(refreshToken);

        expireCookie(response,"refresh");
        response.addHeader("access",""); // delete access

        return new BaseResponse<>("로그아웃을 완료했습니다.");
    }

    @GetMapping("/logout-all") // 전체 브라우저 로그아웃
    public BaseResponse<String> logoutAll(HttpServletRequest request, HttpServletResponse response){ // -> 로그아웃 요청시 access token + refresh token 도 같이 보내도록함.

        String userId = getUserIdFromAuthentication();

        String refreshToken = getRefreshInCookie(request);

        if(refreshToken==null){
            throw new BaseException(REFRESH_INVALID);
        }

        userService.logoutAll(userId,refreshToken);

        expireCookie(response,"refresh");
        response.addHeader("access",""); // delete access

        return new BaseResponse<>("전체 로그아웃을 완료했습니다.");
    }

    @PatchMapping("/password")
    public BaseResponse<?> changePassword(String password){
        String userId = getUserIdFromAuthentication();
        log.info("UserController patchUserInfo userId :{}",userId);
        checkPasswordValidation(password);

        userService.patchPassword(userId,password);
        return new BaseResponse<>("비밀번호 변경을 완료했습니다.");
    }


    private String getRefreshInCookie(HttpServletRequest request){
        String refresh = null;
        Cookie[] cookies = request.getCookies();
        if(cookies==null){
            throw new BaseException(COOKIE_NULL);
        }
        for (Cookie cookie : cookies) {
            if(cookie.getName().equals("refresh")){
                refresh = cookie.getValue();
            }
        }
        return refresh;
    }

    private String getUserIdFromAuthentication(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }

    private Cookie createCookie(String key, String value){
        Cookie cookie = new Cookie(key,value);
        cookie.setMaxAge(24*60*60); // 24 hours
        //  cookie.setSecure(true); // https 통신 할때 설정
        cookie.setHttpOnly(true);
        return cookie;
    }

    private void expireCookie(HttpServletResponse response,String name) {
        Cookie cookie=new Cookie(name, null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    private void checkUsernameValidation(String name){
        if(name==null || name.isBlank())
            throw new BaseException(USERS_EMPTY_USER_NAME);

    }
    private void checkEmailValidation(String email){
        if(email==null || email.isBlank()){
            throw new BaseException(USERS_EMPTY_EMAIL);
        }

    }

    private void checkCodeValidation(String code){
        if(code==null || code.isBlank()){
            throw new BaseException(USERS_EMPTY_EMAIL_CODE);
        }

    }

    private void checkPasswordValidation(String pd){
        if(pd==null || pd.isBlank()){
            throw new BaseException(USERS_EMPTY_PASSWORD);
        }
    }

    private void checkGreetingValidation(String greeting){
        if(greeting==null || greeting.isBlank()){
            throw new BaseException(USERS_EMPTY_GREETING);
        }
    }

}
