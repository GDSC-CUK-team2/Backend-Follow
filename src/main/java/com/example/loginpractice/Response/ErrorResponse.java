package com.example.loginpractice.Response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

@Getter
@Builder
@JsonPropertyOrder({"code", "message"})
public class ErrorResponse {
    private int code;
    private String message;

    public static ResponseEntity<ErrorResponse> toResponseEntity(BaseResponseStatus e) {
        return ResponseEntity
                .status(e.getCode())
                .body(ErrorResponse.builder()
                        .code(e.getCode())
                        .message(e.getMessage())
                        .build());
    }
}
