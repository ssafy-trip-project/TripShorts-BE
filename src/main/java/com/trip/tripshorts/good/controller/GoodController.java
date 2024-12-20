package com.trip.tripshorts.good.controller;

import com.trip.tripshorts.good.dto.GoodStatusResponse;
import com.trip.tripshorts.good.service.GoodService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/goods")
@AllArgsConstructor
@Slf4j
public class GoodController {
    private GoodService goodService;

    @PostMapping("/{videoId}/like")
    public ResponseEntity<Void> addGood(@PathVariable Long videoId) {
        log.debug("Adding good {}", videoId);
        System.out.println("좋아요 눌렀네요 ");
        goodService.addGood(videoId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{videoId}/like")
    public ResponseEntity<Void> removeGood(@PathVariable Long videoId) {
        log.debug("Removing good {}", videoId);
        goodService.removeGood(videoId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{videoId}/status")
    public ResponseEntity<GoodStatusResponse> getGoodStatus(@PathVariable Long videoId) {
        log.debug("Get good status {}", videoId);
        GoodStatusResponse goodStatusResponse = goodService.getGoodStatus(videoId);
        return ResponseEntity.ok(goodStatusResponse);
    }
}
