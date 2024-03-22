package com.zerobase.community.web.board.controller;

import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.zerobase.community.ControllerTestSupport;
import com.zerobase.community.domain.member.entity.Member;
import com.zerobase.community.web.board.dto.request.BoardEditRequestDto;
import com.zerobase.community.web.board.dto.request.BoardWriteRequestDto;
import com.zerobase.community.web.board.dto.response.BoardInfoResponse;
import com.zerobase.community.web.board.dto.response.BoardListViewResponse;
import com.zerobase.community.web.board.dto.response.DetailViewBoardResponse;
import com.zerobase.community.web.board.exception.BoardException;
import com.zerobase.community.web.search.dto.response.SearchPageResponse;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockHttpSession;

class BoardControllerTest extends ControllerTestSupport {

  @Test
  @DisplayName("게시글 작성 API - 정상 성공")
  void writeBoard() throws Exception {
    Member member = Member.builder()
        .loginId("test-loginId")
        .password("test-password")
        .nickName("test-nickName")
        .email("test@test.com")
        .build();

    MockHttpSession session = new MockHttpSession();
    session.setAttribute("loginMember", member);

    BoardWriteRequestDto request = BoardWriteRequestDto.builder()
        .title("test-title")
        .content("test-content")
        .build();

    mockMvc.perform(post("/board")
            .session(session)
            .contentType(APPLICATION_JSON)
            .content(mapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("게시글 작성완료."));
  }

  @Test
  @DisplayName("게시글 작성 API - 로그인 x")
  void writeBoard_fail() throws Exception {
    BoardWriteRequestDto request = BoardWriteRequestDto.builder()
        .title("test-title")
        .content("test-content")
        .build();

    mockMvc.perform(post("/board")
            .contentType(APPLICATION_JSON)
            .content(mapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("로그인 후, 다시 시도해주세요."));
  }

  @Test
  @DisplayName("게시글 수정 - 정상 성공")
  void editBoard() throws Exception {
    Member member = Member.builder()
        .loginId("test-loginId")
        .password("test-password")
        .nickName("test-nickName")
        .email("test@test.com")
        .build();

    MockHttpSession session = new MockHttpSession();
    session.setAttribute("loginMember", member);

    BoardEditRequestDto request = BoardEditRequestDto.builder()
        .boardId(1L)
        .title("edit-test")
        .content("edit-content")
        .build();

    mockMvc.perform(put("/board")
            .session(session)
            .contentType(APPLICATION_JSON)
            .content(mapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("게시글 수정완료."));
  }

  @Test
  @DisplayName("게시글 수정 - 로그인 x")
  void editBoard_fail() throws Exception {

    BoardEditRequestDto request = BoardEditRequestDto.builder()
        .boardId(1L)
        .title("edit-test")
        .content("edit-content")
        .build();

    mockMvc.perform(put("/board")
            .contentType(APPLICATION_JSON)
            .content(mapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("로그인 후, 다시 시도해주세요."));
  }

  @Test
  @DisplayName("게시글 삭제 - 정상 성공")
  void deleteBoard() throws Exception {
    Member member = Member.builder()
        .loginId("test-loginId")
        .password("test-password")
        .nickName("test-nickName")
        .email("test@test.com")
        .build();

    MockHttpSession session = new MockHttpSession();
    session.setAttribute("loginMember", member);

    mockMvc.perform(delete("/board/1")
            .session(session))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("삭제 완료."));
  }

  @Test
  @DisplayName("게시글 삭제 - 로그인 x")
  void deleteBoard_fail() throws Exception {

    mockMvc.perform(delete("/board/1"))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("로그인 후, 다시 시도해주세요."));
  }

  @Test
  @DisplayName("게시글 상세조회 - 정상 성공")
  void detailViewBoard() throws Exception {

    DetailViewBoardResponse board = DetailViewBoardResponse.builder()
        .title("test-title")
        .content("test-content")
        .writeMemberNickName("memberA")
        .commentList(List.of())
        .build();

    when(boardService.detailViewBoard(1L)).thenReturn(board);

    mockMvc.perform(get("/board/1"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title").value("test-title"))
        .andExpect(jsonPath("$.content").value("test-content"))
        .andExpect(jsonPath("$.writeMemberNickName").value("memberA"))
        .andExpect(jsonPath("$.commentList").isArray());
  }

  @Test
  @DisplayName("게시글 상세조회 - 게시글이 없는 경우")
  void detailViewBoard_fail() throws Exception {

    when(boardService.detailViewBoard(1L)).thenThrow(new BoardException("게시글이 없습니다."));

    mockMvc.perform(get("/board/1"))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("게시글이 없습니다."));
  }

  @Test
  @DisplayName("게시글 목록 조회 - 정상 성공")
  void boardViewList() throws Exception {
    Pageable pageable = PageRequest.of(1, 10);

    when(boardService.boardListview(pageable)).thenReturn(new BoardListViewResponse());
    mockMvc.perform(get("/board/list?page=1&size=10"))
        .andDo(print());

    // 코드 질문
//    BoardService mock = mock(BoardService.class);
//    when(mock.boardListview(pageable)).thenReturn(any());
//
//    mockMvc.perform(get("/board/list?page=1&size=10"))
//        .andDo(print())
//        .andExpect(jsonPath("$.content").isArray())
//        .andExpect(jsonPath("$.pageNo").value(1))
//        .andExpect(jsonPath("$.pageSize").value(10))
//        .andExpect(jsonPath("$.totalElements").value(anyInt()))
//        .andExpect(jsonPath("$.totalPages").value(anyInt()))
//        .andExpect(jsonPath("$.last").isBoolean());

  }

  @Test
  @DisplayName("게시판 키워드로 검색")
  void search_board() throws Exception {
    // given
    // when
    when(boardService.searchBoardTitle(anyString(), any()))
        .thenReturn(SearchPageResponse.builder()
            .content(List.of(BoardInfoResponse.builder()
                    .id(1L)
                    .title("test-title")
                    .commentCount(10)
                    .createdAt(null)
                .build()))
            .pageNo(1)
            .pageSize(10)
            .totalElements(0)
            .totalPages(1)
            .last(true)
            .build());

    // then
    mockMvc.perform(get("/board/search?keyword=test&page=1&size=10")
            .contentType(APPLICATION_JSON))
        .andDo(print())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.pageNo").value(1))
        .andExpect(jsonPath("$.pageSize").value(10))
        .andExpect(jsonPath("$.totalElements").value(0))
        .andExpect(jsonPath("$.totalPages").value(1))
        .andExpect(jsonPath("$.last").value(true));
  }
}