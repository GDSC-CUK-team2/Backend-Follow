package com.example.loginpractice.dto;

import com.example.loginpractice.entity.Shop;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data       //@AllArgsConstructor, @NoArgsConstructor, @Getter 등 여러 기능 포함
public class KakaoShop {
    private Long id;
    private String name;
    private String rating;
    private String address;
    private String food_type;
    private Integer view;
    private Integer review;

    public KakaoShop(Map<String, String> map, Shop shop) {
        this.id = shop.getShopUid();
        this.name = map.get("place_name");
        if (shop.getReview() == 0) {
            this.rating = "0.0";
        } else {
            this.rating = String.format("%.1f",(float) shop.getSumRating() / (float) shop.getReview());
        }
        this.address = map.get("address_name");
        this.food_type = map.get("category_name");
        this.view = shop.getView();
        this.review = shop.getReview();
    }
    public KakaoShop(Shop shop) {
        this.id = shop.getShopUid();
        this.name = shop.getName();
        if (shop.getReview() == 0) {
            this.rating = "0.0";
        } else {
            this.rating = String.format("%.1f",(float) shop.getSumRating() / (float) shop.getReview());
        }
        this.address = shop.getAddress();
        this.food_type = shop.getFoodType();
        this.view = shop.getView();
        this.review = shop.getReview();
    }
}
