package com.zerobase.community.domain.board.repository;

import com.zerobase.community.domain.board.entity.Board;
import com.zerobase.community.domain.member.entity.Member;

import java.util.List;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    List<Board> findAllByMember(Member loginMember);

    // 게시글 제목으로 찾기
    @Query("SELECT b FROM Board b WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Board> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);

    // Index 생성
    @Transactional
    @Modifying
    @Query(value = "CREATE INDEX idx_board_title ON board (title)", nativeQuery = true)
    void createIndexOnTitle();
}
