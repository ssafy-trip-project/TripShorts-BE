package com.trip.tripshorts.good.controller;

import com.trip.tripshorts.good.service.GoodService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/goods")
@AllArgsConstructor
@Slf4j
public class GoodController {
    private GoodService goodService;

    @PostMapping("/{videoId}/like")
    public ResponseEntity<Void> addGood(@PathVariable Long videoId) {
        log.debug("Adding good {}", videoId);
        System.out.println("Adding good : "+ videoId);
        goodService.addGood(videoId);
        return ResponseEntity.ok().build();
    }
}
