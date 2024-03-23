package com.zerobase.community;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobase.community.domain.board.repository.BoardRepository;
import com.zerobase.community.domain.board.service.BoardService;
import com.zerobase.community.domain.comment.repositroy.CommentRepository;
import com.zerobase.community.domain.comment.service.CommentService;
import com.zerobase.community.domain.member.repository.MemberRepository;
import com.zerobase.community.domain.member.service.MemberService;
import com.zerobase.community.web.board.controller.BoardController;
import com.zerobase.community.web.comment.controller.CommentController;
import com.zerobase.community.web.exception.GlobalExceptionController;
import com.zerobase.community.web.member.controller.MemberController;
import com.zerobase.community.web.search.controller.SearchController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {
    GlobalExceptionController.class,
    MemberController.class,
    BoardController.class,
    SearchController.class,
    CommentController.class
})
public abstract class ControllerTestSupport {

  @Autowired
  protected MockMvc mockMvc;

  @Autowired
  protected ObjectMapper mapper;

  @MockBean
  protected MemberService memberService;

  @MockBean
  protected MemberRepository memberRepository;

  @MockBean
  protected BoardService boardService;

  @MockBean
  protected BoardRepository boardRepository;

  @MockBean
  protected CommentService commentService;

  @MockBean
  protected CommentRepository commentRepository;
}
