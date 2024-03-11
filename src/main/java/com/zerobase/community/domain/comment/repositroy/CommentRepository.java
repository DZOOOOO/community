package com.zerobase.community.domain.comment.repositroy;

import com.zerobase.community.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

  boolean existsByBoardIdAndId(Long boardId, Long id);
}
