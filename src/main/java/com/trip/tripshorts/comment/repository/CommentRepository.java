package com.trip.tripshorts.comment.repository;

import com.trip.tripshorts.comment.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
