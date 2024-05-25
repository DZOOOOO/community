package com.zerobase.community.etc;

import com.zerobase.community.domain.board.entity.Board;
import com.zerobase.community.domain.board.repository.BoardRepository;
import com.zerobase.community.domain.member.Grade;
import com.zerobase.community.domain.member.entity.Member;
import com.zerobase.community.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@SpringBootTest
public class IndexSpeedTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    BoardRepository boardRepository;

    @BeforeEach
    void init() {
        Member member = Member.builder()
                .loginId("test1")
                .password("test1")
                .nickName("test1")
                .email("test1@test.com")
                .grade(Grade.USER)
                .build();
        Member save = memberRepository.save(member);
        for (int i = 0; i < 10000; i++) {
            boardRepository.save(Board.builder()
                    .title("test-title" + i)
                    .content("test-content" + i)
                    .member(save)
                    .build());
        }
    }

    @AfterEach
    void tearDown() {
        // 테스트 데이터 삭제
        boardRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("인덱스 적용 전 성능 테스트")
    void testWithoutIndex() {
        // Given
        long startTime = System.currentTimeMillis();

        // When
        Page<Board> boards = boardRepository
                .findByTitleContainingIgnoreCase("999", PageRequest.of(0, 10));

        // Then
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        System.out.println("Execution time without index: " + duration + " ms");
    }

    @Test
    @DisplayName("인덱스 적용 후 성능 테스트")
    void testWithIndex() {
        // Given
        boardRepository.createIndexOnTitle();

        long startTime = System.currentTimeMillis();

        // When
        Page<Board> boards = boardRepository
                .findByTitleContainingIgnoreCase("999", PageRequest.of(0, 10));

        // Then
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        System.out.println("Execution time with index: " + duration + " ms");
    }
}
