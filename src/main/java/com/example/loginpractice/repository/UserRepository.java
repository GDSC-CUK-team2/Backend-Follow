package com.example.loginpractice.repository;

import com.example.loginpractice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select u from User u where u.id = :id")
    Optional<User> findUserById(@Param("id") Long id);

    Optional<User> findUserByPhoneNum(@Param("phoneNum") String phoneNum);

    boolean existsByNickname(String nickName);

    List<User> findByRegionAndIdNot(String region, Long userId);

    //추가
//    Optional<UserDetails> findByUserName(String username);
}
