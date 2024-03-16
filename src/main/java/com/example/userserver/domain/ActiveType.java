package com.example.userserver.domain;

import lombok.Getter;

@Getter
public enum ActiveType {

    FOLLOW("팔로우"),
    CANCEL_FOLLOW("팔로우 취소"),
    LIKE_POST("포스트 좋아요"),
    CANCEL_LIKE_POST("포스트 좋아요 취소"),
    LIKE_COMMENT("댓글 좋아요"),
    CANCEL_LIKE_COMMENT("댓글 좋아요 취소"),
    WRITE_POST("포스트 작성"),
    WRITE_COMMENT("댓글 작성");

    private String text;

    ActiveType(String text){
        this.text = text;

    }



}
