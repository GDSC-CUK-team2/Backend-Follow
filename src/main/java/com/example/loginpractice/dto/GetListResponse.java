package com.example.loginpractice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class GetListResponse {
    private Integer page_count;
    private Integer count;
    private List<KakaoShop> results;
}
