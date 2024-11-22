package com.trip.tripshorts.good.repository;

import com.trip.tripshorts.good.domain.Good;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GoodRepository extends JpaRepository<Good, Long> {}
