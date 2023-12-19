package com.example.loginpractice.controller;

import com.example.loginpractice.Response.BaseException;
import com.example.loginpractice.Response.BaseResponseStatus;
import com.example.loginpractice.dto.GetListResponse;
import com.example.loginpractice.dto.KakaoShop;
import com.example.loginpractice.entity.Shop;
import com.example.loginpractice.repository.ShopRepository;
import com.example.loginpractice.service.KakaoMapService;
import com.example.loginpractice.service.ShopService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/restaurants")
public class ShopController {

    private final KakaoMapService kakaoMapService;
    private final ShopService shopService;
    private final ShopRepository shopRepository;

    @GetMapping("")
    public GetListResponse search(@RequestParam String keyword,
                                  @RequestParam(required = false) String page,
                                  @RequestParam(required = false) String x,
                                  @RequestParam(required = false) String y,
                                  @RequestParam(required = false) String radius) {
        if (keyword == null || keyword.isEmpty()) {
            throw new BaseException(BaseResponseStatus.KEYWORD_NULL);
        }
        try {
            Map<?, ?> kakaoMapData = kakaoMapService.get(keyword, page, x, y, radius);
            List<?> document = Objects.requireNonNull(kakaoMapData).get("documents") != null ? (List<?>) kakaoMapData.get("documents") : null;
            Map<String, ?> meta = Objects.requireNonNull(kakaoMapData).get("meta") != null ? (Map<String, ?>) kakaoMapData.get("meta") : null;
            Integer page_count = (Integer) meta.get("pageable_count");
            Integer count = (Integer) meta.get("total_count");

            List<KakaoShop> results = new ArrayList<>();
            if (document != null) {
                for (Object o : document) {
                    Map<String, String> map = (Map<String, String>) o;
                    Long uid = Long.parseLong(map.get("id"));
                    Shop shop = shopService.findByUid(uid);
                    KakaoShop kakaoShop = new KakaoShop(map, shop);
                    results.add(kakaoShop);
                }
            }
            return new GetListResponse(page_count, count, results);

        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.SEARCH_FAIL);
        }
    }

    @GetMapping("/{shopId}")
    public KakaoShop searchOne(@PathVariable Long shopId){
        try{
            Shop shop = shopService.findByUid(shopId);
            shop.addView();
            shopRepository.save(shop);   //db반영

            return new KakaoShop(shop);
        } catch (Exception e){
            throw new BaseException(BaseResponseStatus.SEARCH_FAIL);
        }
    }

}
