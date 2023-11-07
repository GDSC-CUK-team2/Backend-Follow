package com.example.loginpractice.jwt;

import com.example.loginpractice.Response.BaseException;
import com.example.loginpractice.entity.Token;
import com.example.loginpractice.entity.User;
import com.example.loginpractice.repository.TokenRepository;
import com.example.loginpractice.repository.UserRepository;
import com.example.loginpractice.service.UtilService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.security.Key;

import static com.example.loginpractice.Response.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
public class JwtService {
    private Key key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(Secret.JWT_SECRET_KEY));    //JWT 토큰의 서명 키를 설정
    private final JwtProvider jwtProvider;
    private final UtilService utilService;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    //액세스 토큰을 가져옴
    public String getJwt(){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        //클라이언트로부터 서버로 요청이 들어오면 서버에서 HttpServletRequest 생성
        // 현재 요청의 ServletRequest를 가져옴
        //reqeustcontextholder: request에 대한 정보를 가져올 때 사용

        return request.getHeader("Authorization");
        //HTTP 헤더에서 "Authorization" 헤더의 값(액세스 토큰)을 가져옴
    }

    //리프레시 토큰을 가져옴
    public String getRefJwt(){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        return request.getHeader("AuthorizationRef");
    }

    //jwt에서 userId 추출
    public Long getUserIdx() throws BaseException {
        // 1. JWT 추출
        String accessToken = getJwt();
        if (accessToken == null || accessToken.length() == 0) {
            throw new BaseException(EMPTY_JWT);
        }
        try {
            // 2. JWT parsing
            Jws<Claims> claims = Jwts.parserBuilder()   //Jws<Claims>는 JWS로 서명된 JSON 데이터를 나타내며 이 데이터에는 클레임이 포함
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken);

            // 3. userId 추출
            Long userId = claims.getBody().get("userId", Long.class);
            //User user = utilService.findByUserIdWithValidation(userId);
            return userId;
        } catch (ExpiredJwtException e) {
            // access token이 만료된 경우

            User user = tokenRepository.findUserByAccessToken(accessToken).orElse(null);
            if (user == null) {
                throw new BaseException(INVALID_JWT);
            }

            // 4. Refresh Token을 사용하여 새로운 Access Token 발급
            Token token = tokenRepository.findTokenByUserId(user.getId()).orElse(null);
            String refreshToken = token.getRefreshToken();
            if (refreshToken != null) {
                String newAccessToken = refreshAccessToken(user, refreshToken);

                // 새로운 Access Token으로 업데이트된 JWT를 사용하여 userId 추출
                Jws<Claims> newClaims = Jwts.parserBuilder()
                        .setSigningKey(key)
                        .build()
                        .parseClaimsJws(newAccessToken);
                return newClaims.getBody().get("userId", Long.class);   //userId반환
            } else {
                throw new BaseException(EMPTY_JWT);
            }
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            throw new BaseException(INVALID_JWT);
        } catch (Exception ignored) {
            throw new BaseException(INVALID_JWT);
        }
    }

    /**
     * 로그아웃 전용 userId 추출 메서드
     */
    // 로그아웃을 시도할 때는 accsee token과 refresh 토큰이 만료되었어도
    // 형식만 유효하다면 토큰 재발급 없이 로그아웃 할 수 있어야 함.
    // 위에서와 다르게 만료된 토큰일 경우 0L 반환해주고 끝.
    public Long getLogoutUserIdx() throws BaseException {

        // 1. JWT 추출
        String accessToken = getJwt();
        if (accessToken == null || accessToken.length() == 0) {
            throw new BaseException(EMPTY_JWT);
        }

        try {
            // 2. JWT parsing
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken);
            // 3. userId 추출
            return claims.getBody().get("userId", Long.class);
        } catch (ExpiredJwtException e) {
            // access token이 만료된 경우 -> 로그아웃 상태로 간주
            return 0L;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            throw new BaseException(INVALID_JWT);
        } catch (Exception ignored) {
            throw new BaseException(INVALID_JWT);
        }

    }

    /**
     * 액세스 토큰 재발급
     */
    private String refreshAccessToken(User user, String refreshToken) throws BaseException {
        try {
            // 리프레시 토큰이 만료 등의 이유로 유효하지 않은 경우
            if (!jwtProvider.validateToken(refreshToken)) {
                throw new BaseException(INVALID_JWT);
            }
            else { // 리프레시 토큰이 유효한 경우
                Long userId = user.getId();
                String refreshedAccessToken = jwtProvider.createToken(userId);
                // 액세스 토큰 재발급에 성공한 경우
                if (refreshedAccessToken != null) {
                    Token token = utilService.findTokenByUserIdWithValidation(userId);
                    token.updateAccessToken(refreshedAccessToken);
                    tokenRepository.save(token);
                    return refreshedAccessToken;
                }
                throw new BaseException(FAILED_TO_REFRESH);
            }
        } catch (BaseException exception) {
            throw new BaseException(exception.getStatus());
        }
    }






}
