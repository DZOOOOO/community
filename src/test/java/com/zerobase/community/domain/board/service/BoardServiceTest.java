package com.zerobase.community.domain.board.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.zerobase.community.IntegrationTestSupport;
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

@Slf4j
class BoardServiceTest extends IntegrationTestSupport {

  @Autowired
  BoardRepository boardRepository;

  @Autowired
  BoardService boardService;

  @Autowired
  MemberRepository memberRepository;

  @Autowired
  MemberService memberService;

  @AfterEach
  void tearDown() {
    boardRepository.deleteAllInBatch();
    memberRepository.deleteAllInBatch();
  }

  @Test
  @DisplayName("게시글 작성 - 성공")
  void writeBoard_success() {
    // given
    Member member = createMember(0);
    BoardWriteRequestDto dto = BoardWriteRequestDto.builder()
        .title("write-title")
        .content("write-content")
        .build();

    // when
    Board board = boardService.writeBoard(dto, member);

    // then
    assertThat(board).extracting("title", "content")
        .containsExactlyInAnyOrder("write-title", "write-content");
  }

  @Test
  @DisplayName("게시글 수정 - 성공")
  void editBoard_success() {
    // given
    Member member = createMember(0);
    Board board = createBoard(member);
    BoardEditRequestDto request = BoardEditRequestDto.builder()
        .boardId(board.getId())
        .title("title-edit")
        .content("content-edit")
        .build();

    // when
    boardService.editBoard(request, member);
    Board target = boardRepository.findById(board.getId()).orElse(null);

    // then
    assertThat(target).extracting("title", "content")
        .containsExactlyInAnyOrder("title-edit", "content-edit");
  }

  @Test
  @DisplayName("게시글이 없는데 게시글 수정 - 실패")
  void editBoard_noBoard_fail() {
    // given
    Member member = createMember(0);
    BoardEditRequestDto request = BoardEditRequestDto.builder()
        .boardId(999L)
        .title("title-edit")
        .content("content-edit")
        .build();

    // when
    // then
    assertThatThrownBy(() -> boardService.editBoard(request, member))
        .isInstanceOf(BoardException.class).
        hasMessage("게시글이 없습니다.");

  }

  @Test
  @DisplayName("작성자가 다른 게시글 수정 - 실패")
  void editBoard_noWriter_fail() {
    // given
    Member member = createMember(0);
    Member target = createMember(1);
    Board board = createBoard(member);
    BoardEditRequestDto request = BoardEditRequestDto.builder()
        .boardId(board.getId())
        .title("title-edit")
        .content("content-edit")
        .build();

    // when
    // then
    assertThatThrownBy(() -> boardService.editBoard(request, target))
        .isInstanceOf(BoardException.class)
        .hasMessage("작성자가 다릅니다.");
  }

  @Test
  @DisplayName("게시글 삭제 - 성공")
  void deleteBoard_success() {
    // given
    Member member = createMember(0);
    Board board = createBoard(member);

    // when
    boardService.deleteBoard(board.getId(), member);

    // then
    assertThatThrownBy(() -> boardService.findById(board.getId()))
        .isInstanceOf(BoardException.class).hasMessage("게시글이 없습니다.");

  }

  @Test
  @DisplayName("작성자가 다른 게시글 삭제 - 실패")
  void deleteBoard_noWriter_fail() {
    // given
    Member member = createMember(0);
    Member target = createMember(1);
    Board board = createBoard(member);

    // when
    // then
    assertThatThrownBy(() -> boardService.deleteBoard(board.getId(), target))
        .isInstanceOf(BoardException.class).hasMessage("작성자가 다릅니다.");
  }

  @Test
  @DisplayName("게시글이 없는데 게시글 삭제 - 실패")
  void deleteBoard_noBoard_fail() {
    // given
    Member member = createMember(0);

    // when
    // then
    assertThatThrownBy(() -> boardService.deleteBoard(999L, member))
        .isInstanceOf(BoardException.class);
  }

  @Test
  @DisplayName("게시글 상세조회 - 성공")
  void detailViewBoard_success() {
    // given
    Member member = createMember(0);
    Board board = createBoard(member);

    // when
    DetailViewBoardResponse detailBoard = boardService.detailViewBoard(board.getId());

    // then
    assertThat(detailBoard).extracting("title", "content", "writeMemberNickName")
        .containsExactlyInAnyOrder(
            detailBoard.getTitle(),
            detailBoard.getContent(),
            detailBoard.getWriteMemberNickName());
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


  private Member createMember(int num) {
    return memberRepository.save(Member.builder()
        .loginId("test" + num)
        .password("test" + num)
        .nickName("test" + num)
        .email("test" + num + "@test.com")
        .build());
  }

  private Board createBoard(Member member) {
    Board board = Board.builder()
        .title("board-title")
        .content("board-content")
        .member(member)
        .build();
    return boardRepository.save(board);
  }
}