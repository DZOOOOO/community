package com.zerobase.community.web.board.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardListViewResponse {
  // 게시글 번호
  private Long boardId;

  // 게시글 제목
  private String title;

  // 작성된 댓글 갯수
  private Integer commentCount;

  // 작성된 날짜
  private LocalDateTime createdAt;
}
