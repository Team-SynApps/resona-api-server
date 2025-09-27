package com.synapps.resona.fixture;

import com.synapps.resona.entity.member.Member;
import com.synapps.resona.feed.dto.FeedDto;
import com.synapps.resona.feed.dto.SocialMemberDto;
import com.synapps.resona.feed.dto.request.FeedRegistrationRequest;
import com.synapps.resona.feed.dto.request.FeedRequest;
import com.synapps.resona.feed.dto.request.FeedUpdateRequest;
import com.synapps.resona.feed.entity.Feed;
import com.synapps.resona.feed.entity.FeedCategory;
import com.synapps.resona.media.dto.FeedMediaDto;
import java.time.LocalDateTime;
import java.util.List;

public class FeedFixture {

    public static Feed createFeed(Member member, String content) {
        return Feed.of(member, content, FeedCategory.DAILY.name(), "ko");
    }

    public static FeedDto createFeedDto(Long id, String content, LocalDateTime createdAt) {
        return FeedDto.builder()
            .feedId(id)
            .content(content)
            .author(createSocialMemberDto())
            .images(createFeedMediaDtoList())
            .likeCount(5)
            .commentCount(3)
            .createdAt(createdAt)
            .build();
    }

    public static FeedDto createFeedDto(Long feedId, Long authorId, String content, LocalDateTime createdAt) {
        return FeedDto.builder()
            .feedId(feedId)
            .author(createSocialMemberDto(authorId, "AuthorNickname", "http://profile.img/author"))
            .content(content)
            .images(createFeedMediaDtoListForReadFeed())
            .likeCount(10)
            .commentCount(5)
            .hasLiked(true)
            .hasScraped(false)
            .createdAt(createdAt)
            .modifiedAt(createdAt)
            .build();
    }

    public static FeedDto createFeedDtoWithCounts(
        Long feedId, Long authorId, String content, LocalDateTime createdAt,
        long likeCount, long commentCount, boolean hasLiked, boolean hasScraped
    ) {
        return FeedDto.builder()
            .feedId(feedId)
            .author(createSocialMemberDto(authorId, "AuthorNickname", "http://profile.img/author"))
            .content(content)
            .images(createFeedMediaDtoListForReadFeed())
            .likeCount(likeCount)
            .commentCount(commentCount)
            .hasLiked(hasLiked)
            .hasScraped(hasScraped)
            .createdAt(createdAt)
            .modifiedAt(createdAt)
            .build();
    }

    public static SocialMemberDto createSocialMemberDto() {
        return SocialMemberDto.of(100L, "test_user", "url");
    }

    public static SocialMemberDto createSocialMemberDto(Long memberId, String nickname, String profileImageUrl) {
        return SocialMemberDto.of(memberId, nickname, profileImageUrl);
    }

    public static List<FeedMediaDto> createFeedMediaDtoList() {
        return List.of(
            FeedMediaDto.of(1L, "test-url1"),
            FeedMediaDto.of(2L, "test-url2"),
            FeedMediaDto.of(3L, "test-url3")
        );
    }

    public static List<FeedMediaDto> createFeedMediaDtoListForReadFeed() {
        return List.of(
            FeedMediaDto.of(1L, "http://image.url/1"),
            FeedMediaDto.of(2L, "http://image.url/2")
        );
    }

    public static FeedRegistrationRequest createFeedRegistrationRequest() {
        return new FeedRegistrationRequest(List.of(), new FeedRequest());
    }

    public static FeedUpdateRequest createFeedUpdateRequest(String content) {
        return new FeedUpdateRequest(content);
    }
}