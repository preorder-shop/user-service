package com.example.userserver.service;

import static com.example.userserver.common.response.BaseResponseStatus.CERTIF_INVALID_CODE_OR_EMAIL;
import static com.example.userserver.common.response.BaseResponseStatus.FAIL_SEND_CODE;
import static com.example.userserver.common.response.BaseResponseStatus.POST_USERS_EXISTS_EMAIL;
import static com.example.userserver.common.response.BaseResponseStatus.REFRESH_INVALID;
import static com.example.userserver.common.response.BaseResponseStatus.TOKEN_INVALID;
import static com.example.userserver.common.response.BaseResponseStatus.USERS_INVALID_ID;

import com.example.userserver.client.ActivityServiceClient;
import com.example.userserver.common.exceptions.BaseException;
import com.example.userserver.common.mail.CertificationNumber;
import com.example.userserver.common.util.JwtUtil;
import com.example.userserver.domain.dto.TokenDto;
import com.example.userserver.domain.dto.request.EmailAndCodeReq;
import com.example.userserver.domain.dto.request.EmailCertificationReq;
import com.example.userserver.domain.dto.request.PatchPasswordReq;
import com.example.userserver.domain.dto.request.SignUpReq;
import com.example.userserver.domain.dto.response.GetFollowerRes;
import com.example.userserver.domain.dto.response.SignUpRes;
import com.example.userserver.domain.dto.response.UserDto;
import com.example.userserver.domain.entity.RefreshToken;
import com.example.userserver.domain.entity.User;
import com.example.userserver.provider.EmailProvider;
import com.example.userserver.repository.EmailRedisRepository;
import com.example.userserver.repository.RefreshTokenRepository;
import com.example.userserver.repository.UserRepository;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class UserService {

    private final BCryptPasswordEncoder encoder;
    private final EmailProvider emailProvider;
    private final UserRepository userRepository;
    private final EmailRedisRepository emailRedisRepository;
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final S3Service s3Service;
 //   private final ActivityServiceClient activityServiceClient;

    public SignUpRes createUser(SignUpReq signUpReq) {

        if (checkEmailDuplication(signUpReq.getEmail())) // 이미 가입된 이메일인지 확인
        {
            throw new BaseException(POST_USERS_EXISTS_EMAIL);
        }

//        boolean result = emailRedisRepository.checkEmailCertificationNumber(signUpReq.getEmail(), signUpReq.getCode());
//        if(!result){
//            throw new BaseException(CERTIF_INVALID_CODE_OR_EMAIL);
//        }

        // 회원가입 진행
        User user = signUpReq.toEntity(encoder.encode(signUpReq.getPassword())); // 회원 entity 생성
        User save = userRepository.save(user);// db에 push

        emailRedisRepository.deleteEmailCertificationNumber(signUpReq.getEmail());

        return new SignUpRes(save.getUserId(), save.getName(), save.getEmail(), save.getGreeting());
    }

    public String emailCertificate(EmailCertificationReq emailCertificationReq) {

        String email = emailCertificationReq.getEmail();

        if (checkEmailDuplication(email)) { // 이미 가입된 이메일인지 확인
            throw new BaseException(POST_USERS_EXISTS_EMAIL);
        }

        String certificationNumber = CertificationNumber.getCertificationNumber(); // 인증번호
        boolean isSucceed = emailProvider.sendCertificationMail(email, certificationNumber);

        if (!isSucceed) {
            throw new BaseException(FAIL_SEND_CODE);
        }

        emailRedisRepository.saveEmailCertificationNumber(email,certificationNumber);

        return "해당 이메일로 인증코드를 전송했습니다.";

    }

    public void emailAndCodeCheck(EmailAndCodeReq emailAndCodeReq) {

        boolean result = emailRedisRepository.checkEmailCertificationNumber(emailAndCodeReq.getEmail(), emailAndCodeReq.getCode());
        if(!result){
            throw new BaseException(CERTIF_INVALID_CODE_OR_EMAIL);
        }
    }

    public UserDto getUserInfo(String userId) {

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new BaseException(USERS_INVALID_ID));

        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .greeting(user.getGreeting())
                .profileImg(user.getProfile_img_url())
                .build();

    }

    public TokenDto reissueToken(String refreshToken){
        jwtUtil.validateToken(refreshToken);
        String category = jwtUtil.getCategory(refreshToken);

        if(!category.equals("refresh")){
            throw new BaseException(REFRESH_INVALID);
        }

        boolean exists = refreshTokenRepository.existsByToken(refreshToken);
        if(!exists){
            throw new BaseException(TOKEN_INVALID);
        }

        // reissue access token
        String userId = jwtUtil.getUserId(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        String newAccess = jwtUtil.createJwt("access",userId,role);
        String newRefresh = jwtUtil.createJwt("refresh",userId,role);

        deleteRefreshToken(refreshToken); // 기존 토큰 삭제 (DB)
        addRefreshTokenToDB(userId,refreshToken); // 새 토큰 저장

        TokenDto tokenDto = new TokenDto();
        tokenDto.setAccessToken(newAccess);
        tokenDto.setRefreshToken(newRefresh);

        return tokenDto;

    }

    private void addRefreshTokenToDB(String userId, String refresh){
        Date date = new Date(System.currentTimeMillis()+86400000L);

        RefreshToken token = RefreshToken
                .builder()
                .userId(userId)
                .token(refresh)
                .expiredDate(date.toString())
                .build();

        refreshTokenRepository.save(token);
    }

    public void deleteAllRefreshToken(String userId) {

        refreshTokenRepository.deleteByUserId(userId);

    }

    public void deleteRefreshToken(String token) {

        refreshTokenRepository.deleteByToken(token);

    }

    public String patchUserInfo(String userId, String name, String greeting, String image_url) {

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new BaseException(USERS_INVALID_ID));

        if (name != null && !name.isBlank()) {
            user.changeName(name);

        }
        if (greeting != null && !greeting.isBlank()) {
            user.changeGreeting(greeting);

        }
        if (image_url != null) {

            String profile_img_url = user.getProfile_img_url();

            if (profile_img_url != null && !profile_img_url.isBlank()) {
                //todo : 삭제 안되는 원인 찾기
                s3Service.deleteImage(profile_img_url);

            }
            user.changeProfileImage(image_url);
        }

        return "유저 정보를 변경했습니다.";

    }

    public String patchPassword(String userId,String password) {

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new BaseException(USERS_INVALID_ID));

        String newPassword = encoder.encode(password);

        user.changePassword(newPassword);

        deleteAllRefreshToken(userId); // 전체 기기 로그아웃

        return "비밀번호 변경을 완료했습니다.";

    }

//    public List<String> getFollowers(String userId) {
//        User user = userRepository.findByUserId(userId)
//                .orElseThrow(() -> new BaseException(USERS_INVALID_ID));
//
//        List<GetFollowerRes> getFollowerResList = activityServiceClient.getFollowers(userId);
//        List<String> longList = new ArrayList<>();
//        getFollowerResList.forEach(v -> longList.add(v.getFollowerId()));
//        return longList;
//
//    }

    private boolean checkEmailDuplication(String email) {
        return userRepository.existsByEmail(email);
    }


    public void validateUserId(String userId){
        userRepository.findByUserId(userId)
                .orElseThrow(()->new BaseException(TOKEN_INVALID));

    }

    public void logout(String refreshToken) {
        jwtUtil.validateToken(refreshToken);
        deleteRefreshToken(refreshToken);
    }

    public void logoutAll(String userId,String refreshToken) {
        jwtUtil.validateToken(refreshToken);
        deleteAllRefreshToken(userId);
    }


}
