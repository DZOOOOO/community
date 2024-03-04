package com.zerobase.community.web.board.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class BoardEditRequestDto {

  @NotNull(message = "게시글이 없습니다.")
  private Long boardId;

  @NotBlank(message = "수정 제목을 입력해주세요.")
  private String title;

  @NotBlank(message = "수정 내용을 작성해주세요.")
  private String content;
}
