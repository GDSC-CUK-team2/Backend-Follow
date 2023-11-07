package com.example.loginpractice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseTimeEntity{
    @Column
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 멤버의 식별자

    @Column(nullable = false)
    private String phoneNum; // 유저의 휴대폰번호

    @Column(nullable = false)
    private String gender; // 유저의 성별

    @Column(nullable = false)
    private String nickname; // 유저의 닉네임

    @Column(nullable = false)
    private String birth; // 유저의 생년

    @Column(nullable = false)
    private String region; // 유저의 지역(시/군/구)

    @Column(nullable = false)
    private String password; // 비밀번호

    @Column(nullable = false)
    private String introduce; // 한줄소개

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Token token; // 토큰과 일대일 매핑

//    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
//    private Profile profile; // 프로필 사진과 일대일 매핑

    @Builder
    //필요한 데이터만 설정하기 위해 builder사용
    public User(String phoneNum, String gender, String nickname, String birth, String region, String password, String introduce) {
        this.phoneNum = phoneNum;
        this.gender = gender;
        this.nickname = nickname;
        this.birth = birth;
        this.region = region;
        this.password = password;
        this.introduce = introduce;
    }

    //
    public User(String subject, String s, Collection<? extends GrantedAuthority> authorities) {
        super();
    }
}
