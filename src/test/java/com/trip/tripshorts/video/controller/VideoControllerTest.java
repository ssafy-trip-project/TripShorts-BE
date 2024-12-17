package com.trip.tripshorts.video.controller;

import com.trip.tripshorts.auth.service.AuthService;
import com.trip.tripshorts.member.domain.Member;
import com.trip.tripshorts.member.repository.MemberRepository;
import com.trip.tripshorts.video.repository.VideoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
class VideoControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private MemberRepository memberRepository;

    @MockBean
    private AuthService authService;

    private Member testMember;

    @BeforeEach
    void setUp() {
        // 테스트 전에 실행될 설정
        testMember = memberRepository.findByEmail("test@test.com")
                .orElseThrow(() -> new RuntimeException("Test member not found"));

        // AuthService 모킹 설정
        when(authService.getCurrentMember()).thenReturn(testMember);
    }

    @Test
    @DisplayName("비디오 스트리밍 - recent 정렬, 중간 비디오 요청")
    void getVideoPageByRecent_MiddleVideo() throws Exception {
        // given
        Long middleVideoId = 3L;

        // when
        ResultActions result = mockMvc.perform(get("/api/v1/shorts/feed")
                .param("sortby", "recent")
                .param("cursorid", String.valueOf(middleVideoId))
                .param("size", "5"));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.currentVideo").exists())
                .andExpect(jsonPath("$.currentVideo.id").value(middleVideoId))
                .andExpect(jsonPath("$.previousVideoIds").isArray())
                .andExpect(jsonPath("$.previousVideoIds", hasSize(2)))
                .andExpect(jsonPath("$.previousVideoIds[0]").value(5))
                .andExpect(jsonPath("$.previousVideoIds[1]").value(4))
                .andExpect(jsonPath("$.nextVideoIds").isArray())
                .andExpect(jsonPath("$.nextVideoIds", hasSize(2)))
                .andExpect(jsonPath("$.nextVideoIds[0]").value(2))
                .andExpect(jsonPath("$.nextVideoIds[1]").value(1))
                .andDo(print());
    }

    @Test
    @DisplayName("좋아요 순 정렬")
    void getVideoPageByLikes() throws Exception {
        // given
        Long videoId = 1L;  // has 1 like

        // when & then
        mockMvc.perform(get("/api/v1/shorts/feed")
                        .param("sortby", "likes")
                        .param("cursorid", String.valueOf(videoId))
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentVideo.id").value(1))
                .andExpect(jsonPath("$.currentVideo.likeCount").value(1))
                .andDo(print());
    }

    @Test
    @DisplayName("비디오 스트리밍 - views 정렬, 중간 비디오 요청")
    void getVideoPageByViews_MiddleVideo() throws Exception {
        // given
        Long middleVideoId = 2L;

        // when
        ResultActions result = mockMvc.perform(get("/api/v1/shorts/feed")
                .param("sortby", "views")
                .param("cursorid", String.valueOf(middleVideoId))
                .param("size", "5"));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.currentVideo").exists())
                .andExpect(jsonPath("$.currentVideo.id").value(middleVideoId))
                .andExpect(jsonPath("$.previousVideoIds").isArray())
                .andExpect(jsonPath("$.previousVideoIds", hasSize(2)))
                .andExpect(jsonPath("$.previousVideoIds[0]").value(1))
                .andExpect(jsonPath("$.previousVideoIds[1]").value(3))
                .andExpect(jsonPath("$.nextVideoIds").isArray())
                .andExpect(jsonPath("$.nextVideoIds", hasSize(2)))
                .andExpect(jsonPath("$.nextVideoIds[0]").value(5))
                .andExpect(jsonPath("$.nextVideoIds[1]").value(4))
                .andDo(print());
    }
}