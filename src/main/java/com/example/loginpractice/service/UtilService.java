package com.example.loginpractice.service;

import com.example.loginpractice.Response.BaseException;
import com.example.loginpractice.entity.Token;
import com.example.loginpractice.entity.User;
import com.example.loginpractice.repository.TokenRepository;
import com.example.loginpractice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static com.example.loginpractice.Response.BaseResponseStatus.INVALID_USER_JWT;

@Service
@RequiredArgsConstructor
public class UtilService {

    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;

    public User findByUserIdWithValidation(Long userId) throws BaseException {
        return userRepository.findUserById(userId)
                .orElseThrow(() -> new BaseException(INVALID_USER_JWT));
    }

    public Token findTokenByUserIdWithValidation(Long userId) throws BaseException {
        return tokenRepository.findTokenByUserId(userId)
                .orElseThrow(() -> new BaseException(INVALID_USER_JWT));
    }

//    //추가 (jwtfilter에서 authentication 불러오기위해)
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
//        return userRepository.findByUserName(username)
//                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
//    }

}
