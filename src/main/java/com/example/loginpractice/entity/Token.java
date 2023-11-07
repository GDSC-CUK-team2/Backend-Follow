package com.example.loginpractice.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Token {

    @Column
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int tokenId; // 토큰의 식별자

    @Column(nullable = true)
    private String accessToken;

    @Column(nullable = true)
    private String refreshToken;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public void updateAccessToken(String accessToken){
        this.accessToken = accessToken;
    }

    public void updateRefreshToken(String refreshToken){
        this.refreshToken = refreshToken;
    }

    public void updateUser(User user){
        this.user = user;
    }
}
