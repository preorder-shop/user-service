package com.example.userserver.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.Date;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)  // 생성자를 통해서 값 변경 목적으로 접근하는 메시지들 차단
@Getter
@Entity
public class RefreshToken extends BaseEntity{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userId; // 유저 식별값 (UUID)
    private String token;
    private String expiredDate;

    @Builder
    public RefreshToken(String token, String userId, String expiredDate){
        this.token = token;
        this.userId = userId;
        this.expiredDate = expiredDate;
    }


}
