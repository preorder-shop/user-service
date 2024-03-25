package com.example.userserver.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "user")
public class User extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // table pk

    @Column(unique = true,nullable = false, name = "user_id")
    private String userId; // UUID

    @Column(nullable = false)
    private String name;

    @Column(nullable = false,unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String greeting;

    @Column(name = "profileImgUrl")
    private String profile_img_url;

    @Column(nullable = false)
    private String role; // 사용자 권한 관련

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private State state = State.ACTIVE;



    @Builder
    public User(String userId,String name, String email, String password, String role, String greeting) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.greeting = greeting;

    }

    public void changeRoleToAdmin() {
        this.role = "ROLE_ADMIN";
    }

    public void changeName(String name){
        this.name = name;
    }

    public void changeGreeting(String greeting){
        this.greeting = greeting;
    }

    public void changePassword(String password){
        this.password = password;
    }

    public void changeProfileImage(String url){
        this.profile_img_url = url;
    }

    public enum State {
        ACTIVE,
        INACTIVE, // 휴면계정 (오랫동안 접속 안했을때)
        BLACK, // 신고로 차단 / 블랙 계정
        DELETE // 탈퇴한 유저
    }

}
