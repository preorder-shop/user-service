package com.example.userserver.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class CreateCommentRes {

    private Long id;
    private Long postId;
    private String comment;
    private String writer;

    @Builder
    public CreateCommentRes(Long id,Long postId,String comment,String writer){
        this.id = id;
        this.postId = postId;
        this.comment = comment;
        this.writer = writer;

    }

}
