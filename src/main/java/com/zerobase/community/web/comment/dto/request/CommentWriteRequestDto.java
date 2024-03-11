package com.zerobase.community.web.comment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentWriteRequestDto {

  @NotBlank(message = "댓글을 입력해주세요.")
  private String content;

  @NotNull(message = "게시글이 없습니다.")
  private Long boardId;
}
