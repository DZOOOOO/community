package com.zerobase.community.web.member.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.zerobase.community.ControllerTestSupport;
import com.zerobase.community.domain.member.entity.Member;
import com.zerobase.community.web.member.dto.request.MemberEditRequestDto;
import com.zerobase.community.web.member.dto.request.MemberJoinRequestDto;
import com.zerobase.community.web.member.dto.request.MemberLoginRequestDto;
import com.zerobase.community.web.member.dto.request.MemberPasswordRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;

class MemberControllerTest extends ControllerTestSupport {

  @Test
  @DisplayName("회원가입 API")
  void joinMember() throws Exception {
    // given
    MemberJoinRequestDto request = MemberJoinRequestDto.builder()
        .loginId("test-loginId")
        .password("test-password")
        .nickName("test-nickName")
        .email("test-email@test.com")
        .build();

    // when
    // then
    mockMvc.perform(post("/member/join")
            .content(mapper.writeValueAsString(request))
            .contentType(APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("회원가입 완료."));
  }

  @Test
  @DisplayName("로그인 API")
  void loginMember() throws Exception {
    //// 질문 --> Session 에 넣어주는데.. 테스트 작성은 따로 못하는지?

    // given
    MemberLoginRequestDto request = MemberLoginRequestDto.builder()
        .loginId("test-loginId")
        .password("test-password")
        .build();

    // when
    // then
    mockMvc.perform(post("/member/login")
            .content(mapper.writeValueAsString(request))
            .contentType(APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("로그인 성공."));
  }

  @Test
  @DisplayName("로그아웃 API - 유저 정보가 세션에 있는 경우")
  void logout_success() throws Exception {

    Member member = Member.builder()
        .loginId("test-loginId")
        .password("test-password")
        .nickName("test-nickName")
        .email("test@test.com")
        .build();
    MockHttpSession session = new MockHttpSession();
    session.setAttribute("loginMember", member);

    mockMvc.perform(post("/member/logout").session(session))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("로그아웃 성공."))
        .andExpect(request().sessionAttributeDoesNotExist());
  }

  @Test
  @DisplayName("로그아웃 API - 유저 정보가 세션에 없는 경우")
  void logout_fail() throws Exception {
    mockMvc.perform(post("/member/logout"))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("잘못된 요청입니다."));
  }

  @Test
  @DisplayName("회원수정 API - 정상 성공")
  void editMember() throws Exception {
    // given
    MemberEditRequestDto request = MemberEditRequestDto.builder()
        .nickName("edit-nickName")
        .email("edit@test.com")
        .build();
    Member member = Member.builder()
        .loginId("test-loginId")
        .password("test-password")
        .nickName("test-nickName")
        .email("test@test.com")
        .build();

    MockHttpSession session = new MockHttpSession();
    session.setAttribute("loginMember", member);

    // when
    // then
    mockMvc.perform(put("/member/edit")
            .session(session)
            .contentType(APPLICATION_JSON)
            .content(mapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("회원정보 수정 완료."))
        .andExpect(request().sessionAttribute("loginMember", member));
  }

  @Test
  @DisplayName("회원수정 API - 로그인 x")
  void editMember_fail() throws Exception {
    // given
    MemberEditRequestDto request = MemberEditRequestDto.builder()
        .nickName("edit-nickName")
        .email("edit@test.com")
        .build();

    // when
    // then
    mockMvc.perform(put("/member/edit")
            .contentType(APPLICATION_JSON)
            .content(mapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("다시 입력해주세요."));
  }

  @Test
  @DisplayName("비밀번호 수정 API - 정상 성공")
  void editPassword() throws Exception {
    // given
    MemberPasswordRequestDto request = MemberPasswordRequestDto.builder()
        .password("edit-password")
        .build();

    Member member = Member.builder()
        .loginId("test-loginId")
        .password("test-password")
        .nickName("test-nickName")
        .email("test@test.com")
        .build();

    MockHttpSession session = new MockHttpSession();
    session.setAttribute("loginMember", member);

    // when
    // then
    mockMvc.perform(put("/member/edit/password")
            .session(session)
            .contentType(APPLICATION_JSON)
            .content(mapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(jsonPath("$.message").value("비밀번호 수정 완료."))
        .andExpect(request().sessionAttribute("loginMember", member));
  }

  @Test
  @DisplayName("비밀번호 수정 API - 로그인 x")
  void editPassword_fail() throws Exception {
    // given
    MemberPasswordRequestDto request = MemberPasswordRequestDto.builder()
        .password("edit-password")
        .build();

    // when
    // then
    mockMvc.perform(put("/member/edit/password")
            .contentType(APPLICATION_JSON)
            .content(mapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(jsonPath("$.message").value("다시 입력해주세요."))
        .andExpect(request().sessionAttributeDoesNotExist());
  }

  @Test
  @DisplayName("회원탈퇴 API")
  void deleteMember() throws Exception {
    // given
    Member member = Member.builder()
        .id(1L)
        .loginId("test-loginId")
        .password("test-password")
        .nickName("test-nickName")
        .email("test@test.com")
        .build();

    MockHttpSession session = new MockHttpSession();
    session.setAttribute("loginMember", member);

    // when
    // then
    mockMvc.perform(delete("/member/delete/1")
        .session(session))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("회원탈퇴 완료."));
  }

  @Test
  @DisplayName("회원탈퇴 API - 로그인 x")
  void deleteMember_fail() throws Exception {
    // given
    // when
    // then
    mockMvc.perform(delete("/member/delete/1"))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("다시 입력해주세요."));
  }
}