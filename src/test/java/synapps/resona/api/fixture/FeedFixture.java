package synapps.resona.api.fixture;

import synapps.resona.api.socialMedia.dto.feed.FeedDto;
import synapps.resona.api.socialMedia.dto.feed.FeedMemberDto;
import synapps.resona.api.socialMedia.dto.feed.request.FeedRegistrationRequest;
import synapps.resona.api.socialMedia.dto.feed.request.FeedRequest;
import synapps.resona.api.socialMedia.dto.feed.request.FeedUpdateRequest;
import synapps.resona.api.socialMedia.dto.feed.response.FeedResponse;
import synapps.resona.api.socialMedia.dto.media.FeedMediaDto;

import java.time.LocalDateTime;
import java.util.List;

public class FeedFixture {

    public static FeedDto createFeedDto(Long id, String content, LocalDateTime createdAt) {
        return FeedDto.builder()
                .feedId(id)
                .content(content)
                .author(createFeedMemberDto())
                .images(createFeedMediaDtoList())
                .likeCount(5)
                .commentCount(3)
                .createdAt(createdAt)
                .build();
    }

    public static FeedDto createFeedDto(Long feedId, Long authorId, String content, LocalDateTime createdAt) {
        return FeedDto.builder()
                .feedId(feedId)
                .author(createFeedMemberDto(authorId, "AuthorNickname", "http://profile.img/author"))
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

    public static FeedMemberDto createFeedMemberDto() {
        return FeedMemberDto.of(100L, "test_user", "url");
    }

    public static FeedMemberDto createFeedMemberDto(Long memberId, String nickname, String profileImageUrl) {
        return FeedMemberDto.of(memberId, nickname, profileImageUrl);
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

    public static FeedResponse createFeedResponse() {
        return FeedResponse.builder().id("1").content("New Feed").build();
    }

    public static FeedUpdateRequest createFeedUpdateRequest() {
        return new FeedUpdateRequest("Updated Content");
    }

    public static FeedResponse createUpdatedFeedResponse() {
        return FeedResponse.builder().id("1").content("Updated Content").build();
    }
}
