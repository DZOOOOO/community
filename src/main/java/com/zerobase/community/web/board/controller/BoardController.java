package com.zerobase.community.web.board.controller;

import com.zerobase.community.domain.board.entity.Board;
import com.zerobase.community.domain.board.service.BoardService;
import com.zerobase.community.domain.member.entity.Member;
import com.zerobase.community.web.board.dto.request.BoardEditRequestDto;
import com.zerobase.community.web.board.dto.request.BoardWriteRequestDto;
import com.zerobase.community.web.board.dto.response.BoardResponseDto;
import com.zerobase.community.web.board.dto.response.DetailViewBoardResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

@Slf4j
@RestController
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {

  private final BoardService boardService;

  // 게시글 작성 API
  @PostMapping
  public ResponseEntity<?> writeBoard(@Validated @RequestBody BoardWriteRequestDto dto,
      @SessionAttribute(name = "loginMember", required = false) Member loginMember) {

    // 1. 로그인 체크
    if (loginMember == null) {
      log.error("게시글 작성 API - 로그인 X");
      return new ResponseEntity<>(BoardResponseDto.builder()
          .message("로그인 후, 다시 시도해주세요.")
          .build(), HttpStatus.BAD_REQUEST);
    }

    // 2. 게시글 작성 로직
    Board board = boardService.writeBoard(dto, loginMember);

    return new ResponseEntity<>(BoardResponseDto.builder()
        .message("게시글 작성완료.")
        .build(), HttpStatus.OK);
  }

  // 게시글 수정 API
  @PutMapping
  public ResponseEntity<?> editBoard(@Validated @RequestBody BoardEditRequestDto dto,
      @SessionAttribute(name = "loginMember", required = false) Member loginMember) {

    // 1. 로그인 체크
    if (loginMember == null) {
      log.error("사용자 오류 - 로그인 X");
      return new ResponseEntity<>(BoardResponseDto.builder()
          .message("로그인 후, 다시 시도해주세요.")
          .build(), HttpStatus.BAD_REQUEST);
    }

    // 2. 게시글 수정 로직
    boardService.editBoard(dto, loginMember);

    return new ResponseEntity<>(BoardResponseDto.builder()
        .message("게시글 수정완료.")
        .build(), HttpStatus.OK);
  }

  // 게시글 삭제 API
  @DeleteMapping("/{boardId}")
  public ResponseEntity<?> deleteBoard(
      @PathVariable("boardId") Long boardId,
      @SessionAttribute(name = "loginMember", required = false) Member loginMember) {

    // 1. 로그인 체크
    if (loginMember == null) {
      log.error("사용자 오류 - 로그인 X");
      return new ResponseEntity<>(BoardResponseDto.builder()
          .message("로그인 후, 다시 시도해주세요.")
          .build(), HttpStatus.BAD_REQUEST);
    }

    // 2. 게시글 삭제 로직 실행
    boardService.deleteBoard(boardId, loginMember);

    return new ResponseEntity<>(BoardResponseDto.builder()
        .message("삭제 완료.")
        .build(), HttpStatus.OK);
  }

  // 게시글 상세조회 API
  @GetMapping("/detail/{boardId}")
  public ResponseEntity<?> detailViewBoard(@PathVariable("boardId") Long boardId) {
    // 1. 게시글 조회
    DetailViewBoardResponse target = boardService.detailViewBoard(boardId);

    return new ResponseEntity<>(target, HttpStatus.OK);
  }

  // 게시글 목록조회 API - 페이징 처리 -> id 기준 내림차순
  @GetMapping("/list")
  public ResponseEntity<?> boardViewList(
      @PageableDefault(
          page = 0,
          size = 10,
          sort = {"id"},
          direction = Direction.DESC) Pageable pageable) {
    return new ResponseEntity<>(boardService.boardListview(pageable), HttpStatus.OK);
  }
}
