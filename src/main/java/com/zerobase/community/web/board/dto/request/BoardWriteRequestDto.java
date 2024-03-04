package com.zerobase.community.web.board.dto.request;

import jakarta.validation.constraints.NotBlank;
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
public class BoardWriteRequestDto {

  @NotBlank(message = "제목을 입력해주세요.")
  private String title;

  @NotBlank(message = "글을 작성해주세요.")
  private String content;
}
