//package com.synapps.resona.controller.likes;
//
//import static org.mockito.ArgumentMatchers.anyLong;
//import static org.mockito.BDDMockito.given;
//import static org.mockito.Mockito.verify;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//import com.synapps.resona.code.SocialSuccessCode;
//import com.synapps.resona.config.WithMockUserPrincipal;
//import com.synapps.resona.config.server.ServerInfoConfig;
//import com.synapps.resona.command.controller.likes.LikesController;
//import com.synapps.resona.likes.dto.response.CommentLikesResponse;
//import com.synapps.resona.likes.dto.response.FeedLikesResponse;
//import com.synapps.resona.likes.dto.response.ReplyLikesResponse;
//import com.synapps.resona.query.service.LikeService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Disabled;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.ResultActions;
//
//@WebMvcTest(
//    controllers = LikesController.class,
//    excludeAutoConfiguration = { OAuth2ClientAutoConfiguration.class }
//)
//@MockBean(JpaMetamodelMappingContext.class)
//@Disabled
//class LikesControllerResponseTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private LikeService likeService;
//
//    @MockBean
//    private ServerInfoConfig serverInfo;
//
//    // 테스트에 사용될 공통 ID
//    private final Long MEMBER_ID = 1L;
//
//    @BeforeEach
//    void setUp() {
//        given(serverInfo.getApiVersion()).willReturn("v1");
//        given(serverInfo.getServerName()).willReturn("test-server");
//    }
//
//    @Nested
//    @DisplayName("피드(Feed) 좋아요/취소")
//    class FeedLikeTest {
//
//        private final Long FEED_ID = 10L;
//
//        @Test
//        @DisplayName("피드 좋아요 등록 시, FeedLikesResponse DTO를 반환한다")
//        @WithMockUserPrincipal
//        void likeFeed_success() throws Exception {
//            // given
//            FeedLikesResponse responseDto = FeedLikesResponse.of(FEED_ID, 15L, true);
//            given(likeService.likeFeed(anyLong(), anyLong())).willReturn(responseDto);
//
//            // when
//            ResultActions actions = mockMvc.perform(post("/feeds/{feedId}/like", FEED_ID)
//                .with(csrf())
//                .contentType(MediaType.APPLICATION_JSON));
//
//            // then
//            actions.andExpect(status().isOk())
//                .andExpect(jsonPath("$.meta.status").value(SocialSuccessCode.LIKE_FEED_SUCCESS.getStatusCode()))
//                .andExpect(jsonPath("$.data.feedId").value(FEED_ID))
//                .andExpect(jsonPath("$.data.likesCount").value(15))
//                .andExpect(jsonPath("$.data.isLiked").value(true))
//                .andDo(print());
//
//            verify(likeService).likeFeed(FEED_ID, MEMBER_ID);
//        }
//
//        @Test
//        @DisplayName("피드 좋아요 취소 시, FeedLikesResponse DTO를 반환한다")
//        @WithMockUserPrincipal
//        void unlikeFeed_success() throws Exception {
//            // given
//            FeedLikesResponse responseDto = FeedLikesResponse.of(FEED_ID, 14L, false);
//            given(likeService.unlikeFeed(anyLong(), anyLong())).willReturn(responseDto);
//
//            // when
//            ResultActions actions = mockMvc.perform(delete("/feeds/{feedId}/like", FEED_ID)
//                .with(csrf())
//                .contentType(MediaType.APPLICATION_JSON));
//
//            // then
//            actions.andExpect(status().isOk())
//                .andExpect(jsonPath("$.meta.status").value(SocialSuccessCode.UNLIKE_FEED_SUCCESS.getStatusCode()))
//                .andExpect(jsonPath("$.data.feedId").value(FEED_ID))
//                .andExpect(jsonPath("$.data.likesCount").value(14))
//                .andExpect(jsonPath("$.data.liked").value(false))
//                .andDo(print());
//
//            verify(likeService).unlikeFeed(FEED_ID, MEMBER_ID);
//        }
//    }
//
//    @Nested
//    @DisplayName("댓글(Comment) 좋아요/취소")
//    class CommentLikeTest {
//
//        private final Long COMMENT_ID = 20L;
//
//        @Test
//        @DisplayName("댓글 좋아요 등록 시, CommentLikesResponse DTO를 반환한다")
//        @WithMockUserPrincipal
//        void likeComment_success() throws Exception {
//            // given
//            CommentLikesResponse responseDto = CommentLikesResponse.of(COMMENT_ID, 8L, true);
//            given(likeService.likeComment(anyLong(), anyLong())).willReturn(responseDto);
//
//            // when
//            ResultActions actions = mockMvc.perform(post("/comments/{commentId}/like", COMMENT_ID)
//                .with(csrf())
//                .contentType(MediaType.APPLICATION_JSON));
//
//            // then
//            actions.andExpect(status().isOk())
//                .andExpect(jsonPath("$.meta.status").value(SocialSuccessCode.LIKE_COMMENT_SUCCESS.getStatusCode()))
//                .andExpect(jsonPath("$.data.commentId").value(COMMENT_ID))
//                .andExpect(jsonPath("$.data.likesCount").value(8))
//                .andExpect(jsonPath("$.data.isLiked").value(true))
//                .andDo(print());
//
//            verify(likeService).likeComment(COMMENT_ID, MEMBER_ID);
//        }
//
//        @Test
//        @DisplayName("댓글 좋아요 취소 시, CommentLikesResponse DTO를 반환한다")
//        @WithMockUserPrincipal
//        void unlikeComment_success() throws Exception {
//            // given
//            CommentLikesResponse responseDto = CommentLikesResponse.of(COMMENT_ID, 7L, false);
//            given(likeService.unlikeComment(anyLong(), anyLong())).willReturn(responseDto);
//
//            // when
//            ResultActions actions = mockMvc.perform(delete("/comments/{commentId}/like", COMMENT_ID)
//                .with(csrf())
//                .contentType(MediaType.APPLICATION_JSON));
//
//            // then
//            actions.andExpect(status().isOk())
//                .andExpect(jsonPath("$.meta.status").value(SocialSuccessCode.UNLIKE_COMMENT_SUCCESS.getStatusCode()))
//                .andExpect(jsonPath("$.data.commentId").value(COMMENT_ID))
//                .andExpect(jsonPath("$.data.likesCount").value(7))
//                .andExpect(jsonPath("$.data.isLiked").value(false))
//                .andDo(print());
//
//            verify(likeService).unlikeComment(COMMENT_ID, MEMBER_ID);
//        }
//    }
//
//    @Nested
//    @DisplayName("답글(Reply) 좋아요/취소")
//    class ReplyLikeTest {
//
//        private final Long REPLY_ID = 30L;
//
//        @Test
//        @DisplayName("답글 좋아요 등록 시, ReplyLikesResponse DTO를 반환한다")
//        @WithMockUserPrincipal
//        void likeReply_success() throws Exception {
//            // given
//            ReplyLikesResponse responseDto = ReplyLikesResponse.of(REPLY_ID, 3L, true);
//            given(likeService.likeReply(anyLong(), anyLong())).willReturn(responseDto);
//
//            // when
//            ResultActions actions = mockMvc.perform(post("/replies/{replyId}/like", REPLY_ID)
//                .with(csrf())
//                .contentType(MediaType.APPLICATION_JSON));
//
//            // then
//            actions.andExpect(status().isOk())
//                .andExpect(jsonPath("$.meta.status").value(SocialSuccessCode.LIKE_REPLY_SUCCESS.getStatusCode()))
//                .andExpect(jsonPath("$.data.replyId").value(REPLY_ID))
//                .andExpect(jsonPath("$.data.likesCount").value(3))
//                .andExpect(jsonPath("$.data.isLiked").value(true))
//                .andDo(print());
//
//            verify(likeService).likeReply(REPLY_ID, MEMBER_ID);
//        }
//
//        @Test
//        @DisplayName("답글 좋아요 취소 시, ReplyLikesResponse DTO를 반환한다")
//        @WithMockUserPrincipal
//        void unlikeReply_success() throws Exception {
//            // given
//            ReplyLikesResponse responseDto = ReplyLikesResponse.of(REPLY_ID, 2L, false);
//            given(likeService.unlikeReply(anyLong(), anyLong())).willReturn(responseDto);
//
//            // when
//            ResultActions actions = mockMvc.perform(delete("/replies/{replyId}/like", REPLY_ID)
//                .with(csrf())
//                .contentType(MediaType.APPLICATION_JSON));
//
//            // then
//            actions.andExpect(status().isOk())
//                .andExpect(jsonPath("$.meta.status").value(SocialSuccessCode.UNLIKE_REPLY_SUCCESS.getStatusCode()))
//                .andExpect(jsonPath("$.data.replyId").value(REPLY_ID))
//                .andExpect(jsonPath("$.data.likesCount").value(2))
//                .andExpect(jsonPath("$.data.isLiked").value(false))
//                .andDo(print());
//
//            verify(likeService).unlikeReply(REPLY_ID, MEMBER_ID);
//        }
//    }
//}
