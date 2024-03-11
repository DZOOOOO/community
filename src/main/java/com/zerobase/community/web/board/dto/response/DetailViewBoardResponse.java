package com.zerobase.community.web.board.dto.response;

import java.time.LocalDateTime;
import java.util.List;
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
public class DetailViewBoardResponse {

  private String title;

  private String content;

  private String writeMemberNickName;

  private List<String> commentList;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;
}
