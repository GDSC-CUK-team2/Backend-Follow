package com.example.loginpractice.dto;

import com.example.loginpractice.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PostUserRes {
    private Long userId;
    private String nickName;

    public PostUserRes(User user){
        this.userId = user.getId();
        this.nickName = user.getNickname();
    }

}
