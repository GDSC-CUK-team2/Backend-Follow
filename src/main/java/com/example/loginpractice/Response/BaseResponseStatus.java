package com.example.loginpractice.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 에러 코드 관리
 */
@Getter
@AllArgsConstructor
public enum BaseResponseStatus {
    //enum:열거형, 서로 연관된 상수들의 집합
    // 컴파일시 우리가 기대하지 않는 상수값의 할당을 사전에 체크
    /**
     * 200 : 요청 성공
     */
    SUCCESS(true,HttpStatus.OK.value(), "요청에 성공하였습니다."),


    /**
     * 400 : Request 오류, Response 오류
     */
    // Common
    REQUEST_ERROR(false, HttpStatus.BAD_REQUEST.value(), "입력값을 확인해주세요."),
    EMPTY_JWT(false, HttpStatus.UNAUTHORIZED.value(), "JWT를 입력해주세요."),
    INVALID_JWT(false, HttpStatus.UNAUTHORIZED.value(), "유효하지 않은 JWT입니다."),
    INVALID_USER_JWT(false,HttpStatus.FORBIDDEN.value(),"권한이 없는 유저의 접근입니다."),
    RESPONSE_ERROR(false, HttpStatus.NOT_FOUND.value(), "값을 불러오는데 실패하였습니다."),
    REQUIRED_REFRESH(false, HttpStatus.NOT_FOUND.value(), "액세스 토큰이 유효하지 않습니다. 리프레시 토큰을 가지고 액세스 토큰을 재발급해주세요."),

    // users
    USERS_EMPTY_USER_ID(false, HttpStatus.BAD_REQUEST.value(), "유저 아이디 값을 확인해주세요."),

    // [POST] /users
    POST_USERS_EMPTY_EMAIL(false, HttpStatus.BAD_REQUEST.value(), "학번을 입력해주세요."),
    POST_USERS_EXISTS_STUDENTNUM(false,HttpStatus.BAD_REQUEST.value(),"중복된 학번입니다."),
    FAILED_TO_LOGIN(false,HttpStatus.NOT_FOUND.value(),"없는 아이디거나 비밀번호가 틀렸습니다."),
    FAILED_TO_REFRESH(false,HttpStatus.NOT_FOUND.value(),"토큰 재발급에 실패하였습니다."),
    FAILED_TO_LOGOUT(false,HttpStatus.NOT_FOUND.value(),"로그아웃에 실패하였습니다."),
    //추가
    USER_NOT_FOUND(false, HttpStatus.NOT_FOUND.value(), "사용자를 찾을 수 없습니다."),
    PASSWORD_CANNOT_BE_NULL(false, HttpStatus.NOT_FOUND.value(), "비밀번호를 입력해주세요"),
    PASSWORD_MISSMATCH(false, HttpStatus.NOT_FOUND.value(), "비밀번호가 일치하지 않습니다."),
    DUPLICATED_NICKNAME(false, HttpStatus.NOT_FOUND.value(), "이미 존재하는 닉네임입니다."),
    ALREADY_LOGIN(false, HttpStatus.NOT_FOUND.value(), "이미 로그인한 사용자입니다."),
    NONE_NEAR(false, HttpStatus.NOT_FOUND.value(), "근처 사용자가 없습니다."),
    NONE_RECEIVED(false, HttpStatus.NOT_FOUND.value(), "요청받은 매칭이 없습니다."),
    NONE_FREIND(false, HttpStatus.NOT_FOUND.value(), "매칭된 친구가 없습니다."),


    /**
     * 50 : Database, Server 오류
     */
    DATABASE_ERROR(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "데이터베이스 연결에 실패하였습니다."),
    SERVER_ERROR(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버와의 연결에 실패하였습니다."),

    //[PATCH] /users/{userIdx}
    MODIFY_FAIL_USERNAME(false,HttpStatus.INTERNAL_SERVER_ERROR.value(),"유저네임 수정 실패"),

    PASSWORD_ENCRYPTION_ERROR(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "비밀번호 암호화에 실패하였습니다."),
    PASSWORD_DECRYPTION_ERROR(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "비밀번호 복호화에 실패하였습니다."),
    //매칭실패 예외
    MATCHING_NOT_FOUND(false, HttpStatus.NOT_FOUND.value(), "해당 매칭을 찾을 수 없습니다."),
    //동일 매칭 요청 예외처리
    DUPLICATE_MATCHING_REQUEST(false, HttpStatus.NOT_FOUND.value(), "이미 요청된 매칭입니다."),

    //매칭 요청시 이미 친구 일때
    ALREADY_FRIEND_REQUEST(false, HttpStatus.NOT_FOUND.value(), "이미 매칭된 친구입니다."),
    //매칭 요청시 이미 상대에게서 온 요청이 있을때
    ALREADY_RECEIVED_REQUEST(false, HttpStatus.NOT_FOUND.value(), "이미 상대로부터 받은 요청이 있습니다.");

    private final boolean isSuccess;
    private final int code;
    private final String message;

    public String toString() {
        return "{" +
                "\"isSuccess\" : " + "\""+isSuccess+"\"" +
                "\"code\" : " + "\""+code+"\"" +
                "\"message\" : " + "\""+message+"\"" +
                "}";
    }

}
