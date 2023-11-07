package com.example.loginpractice.jwt;

//jwt 생성, 검증 위한 비밀 키 저장
//jwt는 사용자 정보와 권한을 전달하기 위해 사용됨
public class Secret {
    //문자열 형태로 보이지만 실제로는 이진데이터로 사용됨
    public static String JWT_SECRET_KEY = "135b8378904571a649516713c9b3bbffc14f3464a3131504aec324cde5327b4d";
    public static String USER_INFO_PASSWORD_KEY = "49e1b884c9469230ae83cd13ff41a03edcfba4288ba75be7ad336c6d8e88d249";
    public static String ROOM_PASSWORD_KEY = "4ff7e6d380cd64c20d7b3c5d60263f5c3a69e2b8457e496e551a93e5f81923df";
}
