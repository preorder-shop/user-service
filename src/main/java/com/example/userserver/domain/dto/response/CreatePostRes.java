package com.example.userserver.domain.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class CreatePostRes {
    private Long id;
    private String title;
    private String content;
    private String writer;

    @Builder
    public CreatePostRes(Long id, String title,String content,String writer){
        this.title = title;
        this.content = content;
        this.id = id;
        this.writer = writer;

    }
}
