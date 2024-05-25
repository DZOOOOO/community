package com.zerobase.community.web.comment.controller;

import com.zerobase.community.domain.comment.entity.Comment;
import com.zerobase.community.domain.comment.service.CommentService;
import com.zerobase.community.domain.member.entity.Member;
import com.zerobase.community.web.comment.dto.request.CommentEditRequestDto;
import com.zerobase.community.web.comment.dto.request.CommentWriteRequestDto;
import com.zerobase.community.web.comment.dto.response.CommentResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

@Slf4j
@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {

  private final CommentService commentService;

  // 댓글 작성 API
  @PostMapping
  public ResponseEntity<?> writeComment(@Validated @RequestBody CommentWriteRequestDto dto,
      @SessionAttribute(name = "loginMember", required = false) Member loginMember) {

    // 1. 로그인 체크
    if (loginMember == null) {
      log.error("게시글 작성 API - 로그인 X");
      return new ResponseEntity<>(CommentResponseDto.builder()
          .message("로그인 후, 다시 시도해주세요.")
          .build(), HttpStatus.BAD_REQUEST);
    }

    // 2. 댓글 작성 로직
    Comment comment = commentService.writeComment(dto, loginMember);
    return new ResponseEntity<>(CommentResponseDto.builder()
        .message("댓글 작성완료.")
        .build(), HttpStatus.OK);
  }

  // 댓글 수정 API
  @PutMapping
  public ResponseEntity<?> editComment(@Validated @RequestBody CommentEditRequestDto dto,
      @SessionAttribute(name = "loginMember", required = false) Member loginMember) {

    // 1. 로그인 체크
    if (loginMember == null) {
      log.error("게시글 작성 API - 로그인 X");
      return new ResponseEntity<>(CommentResponseDto.builder()
          .message("로그인 후, 다시 시도해주세요.")
          .build(), HttpStatus.BAD_REQUEST);
    }

    // 2. 댓글 수정 로직
    commentService.editComment(dto, loginMember);

    return new ResponseEntity<>(CommentResponseDto.builder()
        .message("댓글 수정완료.")
        .build(), HttpStatus.OK);
  }

  // 댓글 삭제 API
  @DeleteMapping("/{commentId}")
  public ResponseEntity<?> deleteComment(
      @PathVariable("commentId") Long commentId,
      @SessionAttribute(name = "loginMember", required = false) Member loginMember) {

    // 1. 로그인 체크
    if (loginMember == null) {
      log.error("게시글 작성 API - 로그인 X");
      return new ResponseEntity<>(CommentResponseDto.builder()
          .message("로그인 후, 다시 시도해주세요.")
          .build(), HttpStatus.BAD_REQUEST);
    }

    // 2. 댓글 삭제 로직
    commentService.deleteComment(commentId, loginMember);

    return new ResponseEntity<>(CommentResponseDto.builder()
        .message("댓글 삭제완료.")
        .build(), HttpStatus.OK);
  }

}
