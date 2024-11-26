package com.trip.tripshorts.video.dto;

import com.trip.tripshorts.member.domain.Member;
import com.trip.tripshorts.tag.dto.TagListResponse;
import com.trip.tripshorts.tag.dto.TagResponseDto;
import com.trip.tripshorts.tour.domain.Tour;
import com.trip.tripshorts.video.domain.Video;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class VideoResponse {
    private Long id;
    private String videoUrl;
    private String thumbnailUrl;
    private VideoCreatorDto creator;
    private int likeCount;
    private int commentCount;
    private LocalDateTime createdAt;
    private boolean liked;
    private TagListResponse tags;

    public static VideoResponse from(Video video, Member currentMember) {
        List<TagResponseDto> tagDtos = video.getTags().stream()
                .map(TagResponseDto::from)
                .toList();

        return VideoResponse.builder()
                .id(video.getId())
                .videoUrl(video.getVideoUrl())
                .thumbnailUrl(video.getThumbnailUrl())
                .creator(VideoCreatorDto.from(video.getMember()))
                .likeCount(video.getLikes().size())
                .commentCount(video.getComments().size())
                .createdAt(video.getCreatedDate())
                .liked(video.getLikes().stream()
                        .anyMatch(like -> like.getMember().getId().equals(currentMember.getId())))
                .tags(TagListResponse.from(tagDtos))
                .build();
    }
}
