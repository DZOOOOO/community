package com.zerobase.community.web.member.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberJoinRequestDto {

    @NotBlank(message = "아이디를 입력해주세요.")
    private String loginId;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;

    @NotBlank(message = "넥네임을 입력해주세요.")
    private String nickName;

    @Email
    @NotBlank(message = "이메일을 입력해주세요.")
    private String email;
}
