package com.tenco.blog_v1.board;

import com.tenco.blog_v1.user.User;
import lombok.Data;

public class BoardDTO {
    @Data
    public static  class SaveDTO{
        private String title;
        private String content;


        public Board toEntity(User user){
            return  Board.builder()
                    .title(this.title)
                    .content(this.content)
                    .user(user)
                    .build();

        }


        }

    // DTO 는 데이터 전달 목적 (정적내부클래스)
    @Data // 수정이니 getter setter 필수!
    public static class UpdateDTO{
        private String username;
        private String title;
        private String content;
    }
    }

