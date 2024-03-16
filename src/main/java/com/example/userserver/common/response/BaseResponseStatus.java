package com.example.userserver.common.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum BaseResponseStatus {

    /**
     * 200 : 요청 성공
     */
    SUCCESS(true,HttpStatus.OK.value(), "요청에 성공하였습니다."),

    /**
     * 400 : Request, Response 오류
     */

    USERS_EMPTY_EMAIL(false, HttpStatus.BAD_REQUEST.value(), "이메일을 입력해주세요."),

    POST_USERS_EXISTS_EMAIL(false,HttpStatus.BAD_REQUEST.value(),"이미 가입한 이메일 입니다."),

    USERS_INVALID_ID(false,HttpStatus.BAD_REQUEST.value(),"잘못된 유저 정보입니다."),
 //   USERS_INVALID_EMAIL(false,HttpStatus.BAD_REQUEST.value(),"이메일 정보를 다시 확인해주세요."),
    USERS_INVALID_PASSWORD(false,HttpStatus.BAD_REQUEST.value(),"비밀번호 정보를 다시 확인해주세요."),

    USERS_EMPTY_PASSWORD(false, HttpStatus.BAD_REQUEST.value(), "비밀번호를 입력해주세요."),

    USERS_EMPTY_USER_NAME(false, HttpStatus.BAD_REQUEST.value(), "사용자 이름을 입력해주세요."),

    USERS_EMPTY_EMAIL_CODE(false, HttpStatus.BAD_REQUEST.value(), "인증 코드 번호를 입력해주세요."),
    USERS_EMPTY_GREETING(false, HttpStatus.BAD_REQUEST.value(), "인사말을 입력해주세요."),

    CERTIF_INVALID_CODE_OR_EMAIL(false,HttpStatus.BAD_REQUEST.value(), "인증번호와 이메일을 다시한번 확인해주세요."),


    CERTIF_INVALID_CODE(false,HttpStatus.BAD_REQUEST.value(), "인증번호 유효시간이 지났습니다."),

    TOKEN_INVALID(false,HttpStatus.BAD_REQUEST.value(), "유효한 토큰이 아닙니다."),
    TOKEN_EXPIRED(false,HttpStatus.BAD_REQUEST.value(), "만료된 토큰입니다."),

    INVALID_LOGIN(false,HttpStatus.BAD_REQUEST.value(), "잘못된 로그인 정보입니다."),

    POST_ID_INVALID(false,HttpStatus.BAD_REQUEST.value(), "유효하지 않은 게시글 아이디입니다."),

    POST_EMPTY_TITLE(false, HttpStatus.BAD_REQUEST.value(), "제목을 입력해 주세요"),
    POST_EMPTY_CONTENT(false, HttpStatus.BAD_REQUEST.value(), "포스트 내용을 입력해 주세요"),

    FOLLOW_INVALID(false,HttpStatus.BAD_REQUEST.value(), "자기 자신은 팔로우할 수 없습니다."),

    COMMENT_EMPTY(false, HttpStatus.BAD_REQUEST.value(), "댓글을 입력해주세요."),

    COMMENT_ID_INVALID(false,HttpStatus.BAD_REQUEST.value(), "유효하지 않은 댓글 아이디입니다."),

    REFRESH_INVALID(false,HttpStatus.BAD_REQUEST.value(), "refresh token 값이 없습니다."),
    COOKIE_NULL(false,HttpStatus.BAD_REQUEST.value(), "전송된 쿠키 값이 null"),






    /**
     * 500 :  Database, Server 오류
     */

    FAIL_SEND_CODE(false,HttpStatus.INTERNAL_SERVER_ERROR.value(), "인증코드 전송을 실패했습니다."),
    FAIL_SAVE_FILE(false,HttpStatus.INTERNAL_SERVER_ERROR.value(), "파일 업로드에 실패했습니다."),

    UNEXPECTED_ERROR(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "예상치 못한 에러가 발생했습니다.");


    private final boolean isSuccess;
    private final int code;
    private final String message;

    private BaseResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}
