package com.trip.tripshorts.good.dto;

import lombok.*;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class GoodStatusResponse {
    private boolean liked;
    private long totalLikes;
}
