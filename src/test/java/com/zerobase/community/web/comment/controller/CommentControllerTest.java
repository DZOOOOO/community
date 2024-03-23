package com.zerobase.community.web.comment.controller;

import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.zerobase.community.ControllerTestSupport;
import com.zerobase.community.domain.board.entity.Board;
import com.zerobase.community.domain.comment.entity.Comment;
import com.zerobase.community.domain.member.entity.Member;
import com.zerobase.community.web.board.exception.BoardException;
import com.zerobase.community.web.comment.dto.request.CommentEditRequestDto;
import com.zerobase.community.web.comment.dto.request.CommentWriteRequestDto;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;

class CommentControllerTest extends ControllerTestSupport {

  @Test
  @DisplayName("댓글 작성 - 정상 성공")
  void writeComment() throws Exception {
    // given
    Member member = Member.builder()
        .loginId("test-loginId")
        .password("test-password")
        .nickName("test-nickName")
        .email("test@test.com")
        .build();

    MockHttpSession session = new MockHttpSession();
    session.setAttribute("loginMember", member);

    Board board = Board.builder()
        .id(1L)
        .member(member)
        .title("test-title")
        .content("test-content")
        .commentList(List.of())
        .build();

    CommentWriteRequestDto request = CommentWriteRequestDto.builder()
        .boardId(1L)
        .content("test-comment")
        .build();

    // when
    when(commentService.writeComment(request, member)).thenReturn(Comment.builder()
        .id(1L)
        .member(member)
        .board(board)
        .content(request.getContent())
        .build());

    // then
    mockMvc.perform(post("/comment")
        .session(session)
        .contentType(APPLICATION_JSON)
        .content(mapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("댓글 작성완료."));
  }

  @Test
  @DisplayName("댓글 작성 - 로그인 x")
  void writeComment_login_fail() throws Exception {
    // given
    Member member = Member.builder()
        .loginId("test-loginId")
        .password("test-password")
        .nickName("test-nickName")
        .email("test@test.com")
        .build();

    Board board = Board.builder()
        .id(1L)
        .member(member)
        .title("test-title")
        .content("test-content")
        .commentList(List.of())
        .build();

    CommentWriteRequestDto request = CommentWriteRequestDto.builder()
        .boardId(1L)
        .content("test-comment")
        .build();

    // when
    when(commentService.writeComment(request, member)).thenReturn(Comment.builder()
        .id(1L)
        .member(member)
        .board(board)
        .content(request.getContent())
        .build());

    // then
    mockMvc.perform(post("/comment")
            .contentType(APPLICATION_JSON)
            .content(mapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("로그인 후, 다시 시도해주세요."));
  }

  @Test
  @DisplayName("댓글 작성 - 없는 게시글에 댓글 작성")
  void writeComment_board_fail() throws Exception {
    // given
    Member member = Member.builder()
        .loginId("test-loginId")
        .password("test-password")
        .nickName("test-nickName")
        .email("test@test.com")
        .build();

    MockHttpSession session = new MockHttpSession();
    session.setAttribute("loginMember", member);

    CommentWriteRequestDto request = CommentWriteRequestDto.builder()
        .boardId(null)
        .content("test-comment")
        .build();

    // when
    when(commentService.writeComment(request, member))
        .thenThrow(new BoardException("게시글이 없습니다."));

    // then
    mockMvc.perform(post("/comment")
            .session(session)
            .contentType(APPLICATION_JSON)
            .content(mapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("[게시글이 없습니다.]"));
  }

  @Test
  @DisplayName("댓글 수정 - 정상 성공")
  void editComment() throws Exception {
    // given
    Member member = Member.builder()
        .loginId("test-loginId")
        .password("test-password")
        .nickName("test-nickName")
        .email("test@test.com")
        .build();

    MockHttpSession session = new MockHttpSession();
    session.setAttribute("loginMember", member);

    Board board = Board.builder()
        .id(1L)
        .member(member)
        .title("test-title")
        .content("test-content")
        .commentList(List.of())
        .build();

    CommentEditRequestDto request = CommentEditRequestDto.builder()
        .boardId(board.getId())
        .commentId(1L)
        .content("edit-comment")
        .build();

    // when
    // then
    mockMvc.perform(put("/comment")
        .session(session)
        .contentType(APPLICATION_JSON)
        .content(mapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("댓글 수정완료."));

  }

  @Test
  @DisplayName("댓글 수정 - 로그인 x")
  void editComment_login_fail() throws Exception {
    // given
    Member member = Member.builder()
        .loginId("test-loginId")
        .password("test-password")
        .nickName("test-nickName")
        .email("test@test.com")
        .build();

    Board board = Board.builder()
        .id(1L)
        .member(member)
        .title("test-title")
        .content("test-content")
        .commentList(List.of())
        .build();

    CommentEditRequestDto request = CommentEditRequestDto.builder()
        .boardId(board.getId())
        .commentId(1L)
        .content("edit-comment")
        .build();

    // when
    // then
    mockMvc.perform(put("/comment")
            .contentType(APPLICATION_JSON)
            .content(mapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("로그인 후, 다시 시도해주세요."));

  }

  @Test
  @DisplayName("댓글 삭제 - 정상 성공")
  void deleteComment() throws Exception {
    // given
    Member member = Member.builder()
        .loginId("test-loginId")
        .password("test-password")
        .nickName("test-nickName")
        .email("test@test.com")
        .build();

    MockHttpSession session = new MockHttpSession();
    session.setAttribute("loginMember", member);

    mockMvc.perform(delete("/comment/1")
        .session(session)
        .contentType(APPLICATION_JSON))
        .andDo(print())
        .andExpect(jsonPath("$.message").value("댓글 삭제완료."));
  }

  @Test
  @DisplayName("댓글 삭제 - 로그인 x")
  void deleteComment_login_fail() throws Exception {

    mockMvc.perform(delete("/comment/1")
            .contentType(APPLICATION_JSON))
        .andDo(print())
        .andExpect(jsonPath("$.message").value("로그인 후, 다시 시도해주세요."));
  }
}