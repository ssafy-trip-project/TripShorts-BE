package com.trip.tripshorts.tag.repository;

import com.trip.tripshorts.tag.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
}
