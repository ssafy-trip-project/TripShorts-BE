package com.trip.tripshorts.tag.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class TagListResponse {
    private List<TagResponseDto> tags;

    public static TagListResponse from(List<TagResponseDto> tags) {
        return TagListResponse.builder()
                .tags(tags)
                .build();
    }
}
