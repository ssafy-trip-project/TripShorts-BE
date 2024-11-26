package com.trip.tripshorts.tag.dto;

import com.trip.tripshorts.tag.domain.Tag;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TagResponseDto {
    private String name;

    public static TagResponseDto from(Tag tag){
        return TagResponseDto.builder()
                .name(tag.getName())
                .build();
    }
}
