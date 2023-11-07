package com.example.loginpractice.service;

import com.example.loginpractice.AES128;
import com.example.loginpractice.Response.BaseException;
import com.example.loginpractice.dto.*;
import com.example.loginpractice.entity.Token;
import com.example.loginpractice.entity.User;
import com.example.loginpractice.jwt.JwtProvider;
import com.example.loginpractice.jwt.JwtService;
import com.example.loginpractice.jwt.Secret;
import com.example.loginpractice.repository.TokenRepository;
import com.example.loginpractice.repository.UserRepository;
import jdk.jshell.execution.Util;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import static com.example.loginpractice.Response.BaseResponseStatus.*;

@EnableTransactionManagement    //트랜잭션 관리를 활성화
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final UtilService utilService;
    private final JwtProvider jwtProvider;
    private final JwtService jwtService;

    /**
     * 유저 생성 후 DB에 저장(회원 가입) with JWT
     */
    @Transactional
    public PostUserRes createUser(PostUserReq postUserReq) throws BaseException {
        if(postUserReq.getPassword().isEmpty()){
            throw new BaseException(PASSWORD_CANNOT_BE_NULL);
        }
        if(!postUserReq.getPassword().equals(postUserReq.getPasswordChk())) {
            throw new BaseException(PASSWORD_MISSMATCH);
        }
        String pwd;
        try{
            pwd = new AES128(Secret.USER_INFO_PASSWORD_KEY).encrypt(postUserReq.getPassword());
            // 회원가입시 입력받은 비번을 암호화하여 db에 저장
        }
        catch (Exception ignored) { // 암호화가 실패하였을 경우 에러 발생
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
        }
        User user = new User(postUserReq.getPhoneNum(), postUserReq.getGender(), postUserReq.getNickname(),
                postUserReq.getBirth(), postUserReq.getRegion(), pwd, postUserReq.getIntroduce());
        userRepository.save(user);

        return new PostUserRes(user);
    }

    /**
     * 닉네임 중복 확인
     */
    @Transactional
    public String checkNickname(String nickname) throws BaseException {
        if(userRepository.existsByNickname(nickname)) {
            return "이미 존재하는 닉네임입니다.";
        }
        return "사용 가능한 닉네임입니다";
    }

    /**
     * 유저 로그인 with JWT
     */
    public PostLoginRes login(PostLoginReq postLoginReq) throws BaseException {
        User user = userRepository.findUserByPhoneNum(postLoginReq.getPhoneNum()).get();
        Token existToken = tokenRepository.findTokenByUserId(user.getId()).orElse(null);

        String password;
        try {
            password = new AES128(Secret.USER_INFO_PASSWORD_KEY).decrypt(user.getPassword());
            //db의 암호화되어있던 비번을 복호화해서 가져옴
        } catch (Exception ignored) {
            throw new BaseException(PASSWORD_DECRYPTION_ERROR);
        }

        JwtResponseDTO.TokenInfo tokenInfo = jwtProvider.generateToken(user.getId());
        //로그인시 해당id로 토근 생성

        if (postLoginReq.getPassword().equals(password)) {
            if (existToken != null) {
                // Update existing token
                existToken.setAccessToken(tokenInfo.getAccessToken());
                existToken.setRefreshToken(tokenInfo.getRefreshToken());
                Token updateToken = tokenRepository.save(existToken);
                return new PostLoginRes(user, updateToken);
            } else {
                // Save new token
                Token newToken = Token.builder()
                        .accessToken(tokenInfo.getAccessToken())
                        .refreshToken(tokenInfo.getRefreshToken())
                        .user(user)     //user정보 저장
                        .build();
                tokenRepository.save(newToken);
                return new PostLoginRes(user, newToken);
            }
        } else {
            throw new BaseException(FAILED_TO_LOGIN);
        }

    }

    /**
     * 유저 로그아웃
     */
    @Transactional
    public String logout(Long userId) throws BaseException {
        try {
            if (userId == 0L) { // 로그아웃 요청은 access token이 만료되더라도 재발급할 필요가 없음.
                User user = tokenRepository.findUserByAccessToken(jwtService.getJwt()).orElse(null);
                if (user != null) {
                    Token token = tokenRepository.findTokenByUserId(user.getId()).orElse(null);
                    tokenRepository.deleteTokenByAccessToken(token.getAccessToken());
                    return "로그아웃 되었습니다.";
                }
                else {  //사용자가 존재하지 않는다면
                    throw new BaseException(INVALID_JWT);
                }
            }
            else { // 토큰이 만료되지 않은 경우
                User logoutUser = utilService.findByUserIdWithValidation(userId);
                //리프레쉬 토큰 삭제
                tokenRepository.deleteTokenByUserId(logoutUser.getId());
                return "로그아웃 되었습니다.";
            }
        } catch (Exception e) {
            throw new BaseException(FAILED_TO_LOGOUT);
        }

    }

}
