package com.example.loginpractice.jwt;

import com.example.loginpractice.dto.JwtResponseDTO;
import com.example.loginpractice.entity.User;
import com.example.loginpractice.service.UtilService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j  //로깅 코드 자동으로 생성
@Component
@RequiredArgsConstructor
public class JwtProvider {
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1 * 24 * 60 * 60 * 1000L; //refreshToken 유효기간 1일
    //  private static final long ACCESS_TOKEN_EXPIRE_TIME = 1 * 60 * 60 * 1000L; //accessToken 유효기간 1시간
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1 * 24 * 60 * 60 * 1000L; //accessToken 유효기간 1일

    // private static final long REFRESH_TOKEN_EXPIRE_TIME = 60 * 1000L; //refreshToken 유효기간 1분,  refrshToken 테스트를 위해 사용
    // private static final long ACCESS_TOKEN_EXPIRE_TIME = 10 * 1000L; //유효기간 10초, refrshToken 테스트를 위해 사용
    private static final String BEARER_TYPE = "Bearer";
    private static final String AUTHORITIES_KEY = "auth";   //내가 추가
    private final UtilService utilService;    //내가 추가

    //비밀키 생성   //secret의 JWT_secret_key를 디코딩
    //비밀키를 암호화하거나 안전하게 저장하려면 base64등의 방법으로 이진데이터로 변환해야
    private Key key = Keys.hmacShaKeyFor     //비밀키 생성. hmac은 주어진 데이터와 비밀키로 암호화 기술
            (Decoders.BASE64URL.decode(Secret.JWT_SECRET_KEY));  //문자열을 이진 데이터로 디코딩 -> hmac키로 사용됨


    //사용자 id로 액세스 토큰 생성
    public String createToken(Long userId) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_TIME); // 만료기간 6시간

        return Jwts.builder()   //builder로 jwts 객체 생성
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE) //jwt헤더 구성
                .claim("userId", userId)    //claim정보(payload에 포함되는) 설정
                .setIssuer("test") // 토큰발급주체(issuer)정보 설정
                .setIssuedAt(now) // 발급시간(iat) 설정
                .setExpiration(expiration) // 만료시간(exp) 설정
                .signWith(key, SignatureAlgorithm.HS256)    //설정 키와 서명 알고리즘(HS256)으로 서명
                .compact();  //생성한 토큰을 문자열 형태로 반환

        //생성된 토큰은 클라이언트에게 제공
        //클라이언트는 이 토큰을 사용하여 서버에 인증 요청, 권한 확인
    }

    public String createRefreshToken(Long userId) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + REFRESH_TOKEN_EXPIRE_TIME); // 만료기간 14일

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE) // (1)
                .claim("userId", userId)
                .setIssuer("test") // 토큰발급자(iss)
                .setIssuedAt(now) // 발급시간(iat)
                .setExpiration(expiration) // 만료시간(exp)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 유저 정보를 가지고 AccessToken, RefreshToken 을 생성하는 메서드
    public JwtResponseDTO.TokenInfo generateToken(Long userId) {
        long now = (new Date()).getTime();

        // Access Token 생성
        String accessToken = Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE) // (1)
                .claim("userId", userId)
                .setExpiration(new Date(now + ACCESS_TOKEN_EXPIRE_TIME)) // 만료시간
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // Refresh Token 생성
        String refreshToken = Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE) // (1)
                .claim("userId", userId)
                .setExpiration(new Date(now + REFRESH_TOKEN_EXPIRE_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return JwtResponseDTO.TokenInfo.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();   //빌더 패턴에서 객체 생성을 마무리하는 메소드 (불변. 설정변경불가)

        //생성된 Access Token과 Refresh Token은
        //JwtResponseDTO.TokenInfo 객체에 담겨 반환
    }

    //주어진 Access Token을 복호화(디코딩), 해당 토큰의 클레임 정보 추출
    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build()  //jwt파서 생성, 파싱시 사용할 서명키(key) 설정
                    .parseClaimsJws(accessToken).getBody();   //AccessToken을 파싱하여 변환한 jws객체에서 클레임 추출
            //jws: jwt의 서명을 나타내는 부분
        } catch (ExpiredJwtException e) {   //토큰 만료시
            return e.getClaims();   //예외 객체에서 만료된 토큰의 클레임 정보 추출
        }
    }

    // JWT 토큰을 복호화하여 토큰에 들어있는 정보(userId 추출하여 Long으로 반환)를 꺼내는 메서드
    public Long getAuthentication(String accessToken) {

        Claims claims = parseClaims(accessToken);   // 토큰 복호화하여 클레임 정보 추출
        String memberId = claims.get("userId").toString();  //클레임 정보 중 userId키에 해당하는 값을 문자열로 변환

        return Long.valueOf(memberId);  //문자열을 Long타입으로 변환하여 반환
    }

//    // JWT 토큰에서 인증 정보 조회 //추가부분
//    public Authentication getAuthenticationa(String token) {
//        UserDetails userDetails = utilService.loadUserByUsername(this.parseClaims(token).getSubject());
//        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
//    }

    // 토큰 정보를 검증하는 메서드
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);  //token검증, 파싱 후 jws 객체 반환
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {    //위 과정에서 발생한 예외 처리
            log.info("Invalid JWT Token", e);   //잘못된 jwt 서명
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);   //만료
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);   //지원 x
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e); //jwt 토큰이 잘못됨
        }
        return false;
    }

    //==토큰 앞 부분('Bearer') 제거 메소드==//
    public String BearerRemove(String token) {
        return token.substring("Bearer ".length());

        //"Bearer ": HTTP 요청의 헤더에 포함된 인증 토큰을 나타내는 표준 방식 중 하나
        //일반적으로 클라이언트가 HTTP 요청의 헤더에 **`Authorization`** 필드에 "Bearer " 문자열을 포함하여 토큰 값을 보냄
        //이때 주어진 토큰에서 "Bearer"제거 후 실제 토큰 값 반환
    }

    //주어진 액세스 토큰의 남은 유효시간 계산
    public Long getExpiration(String accessToken) {
        // accessToken 남은 유효시간
        Date expiration = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(accessToken)
                .getBody()
                .getExpiration();
        // 현재 시간
        Long now = System.currentTimeMillis();
        return (expiration.getTime() - now);
    }

    //추가
    //jwt토큰에서 추출된 정보를 authentication객체로 변환
    //authentication은 사용자의 인증 및 권한을 관리
    public Authentication getAuthenticationa (String token) {
        //토큰을 파싱하고 검증하며 서명이 유효한지 확인
        Claims claims = parseClaims(token);
//
//                Jwts
//                .parserBuilder()
//                .setSigningKey(key)
//                .build()
//                .parseClaimsJws(token)
//                .getBody(); //jwt토큰의 클레임으로 사용자 주체정보, 권한정보 포함 -> 추출

        //권한 정보를 추출하여 컬렉션에저장
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        //주체(subject) 정보로 User객체 생성(이름, 비번, 권한 정보 포함)
        User principal = new User(claims.getSubject(), "", authorities);

        //사용자의 주체(principal), 토큰 및 권한 정보(authorities) 포함한 authentication객체 생성
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
        //생성된 authentication객체는 인증 및 권한 부여 관리에 사용됨
    }

}
