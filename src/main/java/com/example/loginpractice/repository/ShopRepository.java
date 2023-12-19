package com.example.loginpractice.repository;

import com.example.loginpractice.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {
    public Optional<Shop> findByShopUid(Long shopUid);

    boolean existsByShopUid(Long shopUid);
}
