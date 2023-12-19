package com.example.loginpractice.service;

import com.example.loginpractice.Response.BaseException;
import com.example.loginpractice.Response.BaseResponseStatus.*;
import com.example.loginpractice.entity.Shop;
import com.example.loginpractice.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.example.loginpractice.Response.BaseResponseStatus.SHOP_NOT_FOUND;


@Service
@RequiredArgsConstructor
public class ShopService {
    private final ShopRepository shopRepository;

//    public Shop getShop(Long shopId){
//        Optional<Shop> shop = shopRepository.findById(shopId);
//
//        if(shop.isPresent()){
//            return shop.get();
//        }else{
//            throw new BaseException(SHOP_NOT_FOUND);
//        }
//    }

    public Shop findByUid(Long shopUid){
        Optional<Shop> shop = shopRepository.findByShopUid(shopUid);

        if(shop.isPresent()){
            return shop.get();
        }else{
            throw new BaseException(SHOP_NOT_FOUND);
        }
    }

}
