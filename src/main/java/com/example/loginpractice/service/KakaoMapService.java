package com.example.loginpractice.service;

import com.example.loginpractice.entity.Shop;
import com.example.loginpractice.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
//@RequiredArgsConstructor
public class KakaoMapService {
    @Value("${kakao.api-key}")
    private String kakaoApiUrl;

    @Value("${kakao.rest-api-key}")
    private String kakaoApiKey;

    private final ShopRepository shopRepository;
    private final WebClient webClient;
    //WebClient는 http 클라이언트
    // 요청을 나타내고 전송하게 해주는 빌더 방식의 인터페이스를 사용

    public KakaoMapService(ShopRepository shopRepository) {
        this.shopRepository = shopRepository;
        this.webClient = WebClient.create();
    }

    public Map<?, ?> get(String query, String page, String x, String y, String radius){

        String url = kakaoApiUrl + "/v2/local/search/keyword.json?";
        url += "category_group_code=FD6&";  //음식점 필터

        if (page != null && !page.isEmpty()) {
            url += "page=" + page + "&";
        }
        if (x != null && !x.isEmpty()) {
            url += "x=" + x + "&";
        }
        if (y != null && !y.isEmpty()) {
            url += "y=" + y + "&";
        }
        if (radius != null && !radius.isEmpty()) {
            url += "radius=" + radius + "&";
        }

        //json형식으로 반환되기에 map 사용
        Map<?, ?> response = webClient
                        .get()
                        .uri(url + "query=" + query)    //webclient로 get요청
                        .header("Authorization", "KakaoAK " + kakaoApiKey)  //요청 헤더 설정
                        .retrieve() //응답 받아오기 위한
                        .bodyToMono(Map.class)  //응답 데이터를 mono로 변환
                        .block();   //mono의 비동기 처리를 기다려 결과를 얻는데 사용


        List<?> document = Objects.requireNonNull(response).get("documents") != null ? (List<?>) response.get("documents") : null;
        //response가 null이 아니면 'documents' 필드의 값을 리스트로 읽어옴

        if (document != null) {
            for (Object o : document) {
                Map<String, String> map = (Map<String, String>) o;
                Long shopUid = Long.parseLong(map.get("id"));
                String address = map.get("address_name");
                String name = map.get("place_name");
                String foodType = map.get("category_name");

                if (shopRepository.findByShopUid(shopUid).isPresent()) { //이미 db에 식당 저장되어있으면
                    continue;
                }

                Shop shop = Shop.createShop(shopUid, address, name, foodType);
                shopRepository.save(shop);
            }
        }

        return response;
    }


}
