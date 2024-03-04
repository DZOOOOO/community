package com.zerobase.community.domain.board.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.zerobase.community.domain.board.entity.Board;
import com.zerobase.community.domain.board.repository.BoardRepository;
import com.zerobase.community.domain.member.entity.Member;
import com.zerobase.community.domain.member.repository.MemberRepository;
import com.zerobase.community.domain.member.service.MemberService;
import com.zerobase.community.web.board.dto.request.BoardEditRequestDto;
import com.zerobase.community.web.board.dto.request.BoardWriteRequestDto;
import com.zerobase.community.web.board.dto.response.DetailViewBoardResponse;
import com.zerobase.community.web.board.exception.BoardException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class BoardServiceTest {

  @Autowired
  MemberService memberService;

  @Autowired
  BoardService boardService;

  @Autowired
  BoardRepository boardRepository;

  @Autowired
  MemberRepository memberRepository;

  @AfterEach
  void destroy() {
    boardRepository.deleteAll();
  }

  // dto 생성 클래스
  static class Dto {

    public static BoardWriteRequestDto createDto(String title, String content) {
      return BoardWriteRequestDto.builder()
          .title(title)
          .content(content)
          .build();
    }

    public static BoardEditRequestDto editDto(Long boardId, String title, String content) {
      return BoardEditRequestDto.builder()
          .boardId(boardId)
          .title(title)
          .content(content)
          .build();
    }
  }

  @Test
  @DisplayName("게시글 작성 - 성공")
  void writeBoard_success() {
    // given
    Member member = memberRepository.save(Member.builder()
        .loginId("test")
        .password("test")
        .nickName("test")
        .email("test@test.com")
        .build());
    BoardWriteRequestDto dto = Dto.createDto("write-title", "write-content");

    // when
    Board board = boardService.writeBoard(dto, member);

    // then
    assertThat(board.getTitle()).isEqualTo("write-title");
    assertThat(board.getContent()).isEqualTo("write-content");
  }

  @Test
  @DisplayName("게시글 수정 - 성공")
  void editBoard_success() {
    // given
    Member member = memberRepository.save(Member.builder()
        .loginId("test1")
        .password("test")
        .nickName("test1")
        .email("test@test.com")
        .build());
    BoardWriteRequestDto dto = Dto.createDto("write-title", "write-content");
    Board board = boardService.writeBoard(dto, member);

    BoardEditRequestDto editDto = Dto.editDto(board.getId(), "edit-title", "edit-content");

    // when
    boardService.editBoard(editDto, member);

    // then
    Board target = boardService.findById(board.getId());
    assertThat(target.getTitle()).isEqualTo("edit-title");
    assertThat(target.getContent()).isEqualTo("edit-content");
  }

  @Test
  @DisplayName("게시글이 없는데 게시글 수정 - 실패")
  void editBoard_noBoard_fail() {
    // given
    Member member = memberRepository.save(Member.builder()
        .loginId("test2")
        .password("test")
        .nickName("test2")
        .email("test@test.com")
        .build());

    BoardEditRequestDto editDto = Dto.editDto(999L, "edit-title", "edit-content");

    // when
    // then
    assertThatThrownBy(() -> boardService.editBoard(editDto, member))
        .isInstanceOf(BoardException.class);
  }

  @Test
  @DisplayName("작성자가 다른 게시글 수정 - 실패")
  void editBoard_noWriter_fail() {
    // given
    Member member = memberRepository.save(Member.builder()
        .loginId("test3")
        .password("test")
        .nickName("test3")
        .email("test@test.com")
        .build());
    Member target = memberRepository.save(Member.builder()
        .loginId("test4")
        .password("test")
        .nickName("test4")
        .email("test@test.com")
        .build());
    BoardWriteRequestDto dto = Dto.createDto("write-title", "write-content");
    Board board = boardService.writeBoard(dto, member);

    BoardEditRequestDto editDto = Dto.editDto(board.getId(), "edit-title", "edit-content");

    // when
    assertThatThrownBy(() -> boardService.editBoard(editDto, target))
        .isInstanceOf(BoardException.class);
  }

  @Test
  @DisplayName("게시글 삭제 - 성공")
  void deleteBoard_success() {
    // given
    Member member = memberRepository.save(Member.builder()
        .loginId("test5")
        .password("test")
        .nickName("test5")
        .email("test@test.com")
        .build());
    BoardWriteRequestDto dto = Dto.createDto("write-title", "write-content");
    Board board = boardService.writeBoard(dto, member);

    // when
    boardService.deleteBoard(board.getId(), member);

    // then
    assertThat(boardRepository.count()).isEqualTo(0L);
  }

  @Test
  @DisplayName("작성자가 다른 게시글 삭제 - 실패")
  void deleteBoard_noWriter_fail() {
    // given
    Member member = memberRepository.save(Member.builder()
        .loginId("test6")
        .password("test")
        .nickName("test6")
        .email("test@test.com")
        .build());
    Member target = memberRepository.save(Member.builder()
        .loginId("test7")
        .password("test")
        .nickName("test7")
        .email("test@test.com")
        .build());
    BoardWriteRequestDto dto = Dto.createDto("write-title", "write-content");
    Board board = boardService.writeBoard(dto, member);

    // when
    // then
    assertThatThrownBy(() -> boardService.deleteBoard(board.getId(), target))
        .isInstanceOf(BoardException.class);
  }

  @Test
  @DisplayName("게시글이 없는데 게시글 삭제 - 실패")
  void deleteBoard_noBoard_fail() {
    // given
    Member member = memberRepository.save(Member.builder()
        .loginId("test8")
        .password("test")
        .nickName("test8")
        .email("test@test.com")
        .build());

    // when
    // then
    assertThatThrownBy(() -> boardService.deleteBoard(999L, member))
        .isInstanceOf(BoardException.class);
  }

  @Test
  @DisplayName("게시글 상세조회 - 성공")
  void detailViewBoard_success() {
    // given
    Member member = memberRepository.save(Member.builder()
        .loginId("test9")
        .password("test")
        .nickName("test9")
        .email("test@test.com")
        .build());
    BoardWriteRequestDto dto = Dto.createDto("write-title", "write-content");
    Board board = boardService.writeBoard(dto, member);

    // when
    DetailViewBoardResponse target = boardService.detailViewBoard(board.getId());

    // then
    assertThat(target.getTitle()).isEqualTo("write-title");
    assertThat(target.getContent()).isEqualTo("write-content");
  }

  @Test
  @DisplayName("게시글이 없는 게시글 상세조회 - 실패")
  void detailViewBoard_noBoard_fail() {
    // given
    // when
    // then
    assertThatThrownBy(() -> boardService.detailViewBoard(9999L))
        .isInstanceOf(BoardException.class);
  }
}