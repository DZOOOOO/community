package com.zerobase.community.web.search.controller;

import com.zerobase.community.domain.board.service.BoardService;
import com.zerobase.community.web.board.dto.response.BoardInfoResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SearchController {

  private final BoardService boardService;

  // 제목 검색으로 게시글 찾기
  @GetMapping("/search")
  public ResponseEntity<?> search(@RequestParam(name = "keyword") String keyword,
      @PageableDefault(
          page = 0,
          size = 10) Pageable pageable) {
    List<BoardInfoResponse> boardList = boardService.searchBoardTitle(keyword, pageable);
    return new ResponseEntity<>(boardList, HttpStatus.OK);
  }
}
