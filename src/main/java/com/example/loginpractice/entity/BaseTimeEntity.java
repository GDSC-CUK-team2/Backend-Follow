package com.example.loginpractice.entity;


import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
//JPA에서 엔티티가 상속 관계를 가질 때, 사용하여 부모 클래스로만 사용하고 테이블을 생성하지 않도록 지정
@EntityListeners(AuditingEntityListener.class)  //jpa의 자동 감사(auditing) 활성화 -> 필드 자동 업데이트
public abstract class BaseTimeEntity {
    // 생성일, 수정일, 생성자, 수정자 정보를 자동으로 관리해줌
    // 이 클래스를 extends 하는 클래스는 BaseTimeEntity의 필드(생성일, 수정일) 상속받아서 자동으로 관리

    @CreatedDate    //엔티티 생성시 자동으로 현재 날짜 및 시간으로 설정
    private LocalDateTime createDate;
    //localdatetime: 시간정보를 년,월,일,시,분..로 표현

    @LastModifiedDate   //엔티티 수정시 자동으로 현재 날짜 및 시간으로 설정
    private LocalDateTime modifiedDate;
}
