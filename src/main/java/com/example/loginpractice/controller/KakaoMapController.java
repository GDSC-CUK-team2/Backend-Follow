package com.example.loginpractice.controller;

import com.example.loginpractice.Response.BaseException;
import com.example.loginpractice.Response.BaseResponseStatus;
import com.example.loginpractice.service.KakaoMapService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/map")
public class KakaoMapController {
    private static final String KeywordEndPoint = "/v2/local/search/keyword.json";

    private final KakaoMapService kakaoMapService;

    //식당 검색
    @GetMapping("/search")
    public Map<?,?> search(@RequestParam String keyword,
                           @RequestParam(required = false) String page,
                           @RequestParam(required = false) String x,
                           @RequestParam(required = false) String y,
                           @RequestParam(required = false) String radius){
        String query = keyword;

        if(query == null || query.isEmpty()){
            throw new BaseException(BaseResponseStatus.KEYWORD_NULL);
        }
        try{
            Map<?,?> map = kakaoMapService.get(query, page, x, y, radius);
            return map;
        } catch(Exception e){
            throw new BaseException(BaseResponseStatus.SEARCH_FAIL);
        }

    }


}
