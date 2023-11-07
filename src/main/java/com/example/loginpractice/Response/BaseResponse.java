package com.example.loginpractice.Response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.example.loginpractice.Response.BaseResponseStatus.SUCCESS;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"isSuccess", "code", "message", "result"})  //JSON으로 변환될 때 프로퍼티의 순서를 지정
public class BaseResponse<T> {
    //API 응답을 위한 기본 응답 클래스

    @JsonProperty("isSuccess")      //해당 필드를 JSON 객체의 특정 키와 매핑하도록
    private final Boolean isSuccess;
    private final String message;
    private final int code;
    @JsonInclude(JsonInclude.Include.NON_NULL)      //해당 필드의 값이 null이 아닌 경우에만 해당 필드를 JSON으로 직렬화
    private T result;

    // 요청에 성공한 경우
    public BaseResponse(T result) {
        this.isSuccess = SUCCESS.isSuccess();
        this.message = SUCCESS.getMessage();
        this.code = SUCCESS.getCode();
        this.result = result;
    }

    // 요청에 실패한 경우
    public BaseResponse(BaseResponseStatus status) {
        this.isSuccess = status.isSuccess();
        this.message = status.getMessage();
        this.code = status.getCode();
    }
}
