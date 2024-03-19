package com.zerobase.community.web.member.dto.request;

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
public class MemberEditRequestDto {

  @NotBlank(message = "닉네임을 입력해주세요.")
  private String nickName;

  @NotBlank(message = "이메일을 입력해주세요.")
  private String email;


}
