package com.example.loginpractice.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtFilter extends GenericFilterBean {

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);
    public static final String AUTHORIZATION_HEADER = "Authorization";
    private final JwtProvider jwtProvider;

    @Override   //클라이언트의 http요청 처리
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain
    ) throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;    //현재 요청에 대한 httpservletrequest객체로 형변환
        String jwt = resolveToken(httpServletRequest);  //http요청에서 jwt토큰 추출
        String requestURI = httpServletRequest.getRequestURI(); //현재 요청 uri를 가져옴

        //jwt토큰 존재하고 유효한 경우
        if (StringUtils.hasText(jwt) && jwtProvider.validateToken(jwt)) {
            Authentication authentication = jwtProvider.getAuthenticationa(jwt);    //추가한 메소드로
            //유효한 jwt토큰 기반으로 사용자 인증, authentication객체로 가져옴
            SecurityContextHolder.getContext().setAuthentication(authentication);
            //인증 정보 설정
            logger.debug("Security Context에 '{}' 인증 정보를 저장했습니다, uri: {}", authentication.getName(), requestURI);
        } else {
            logger.debug("유효한 JWT 토큰이 없습니다, uri: {}", requestURI);
        }

        filterChain.doFilter(servletRequest, servletResponse);  //필터 체인으로 요청 전달
    }

    //http요청 헤더에서 jwt토큰 추출
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);   //헤더에서 authorization헤더를 가져옴

        //헤더에서 jwt토큰 추출위해 "bearer"로 시작하는지 확인
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);    //bearer제외한 토큰 문자열 반환
        }

        return null;    //토큰이 존재하지 않으면 null반환
    }
}
