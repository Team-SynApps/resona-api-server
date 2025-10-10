package com.synapps.resona.retrieval.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.synapps.resona.comment.dto.CommentDto;
import com.synapps.resona.query.member.entity.MemberStateDocument;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
@Builder
public class FeedDetailDto {

  private FeedDto feed;
  private Page<CommentDto> comment;

  private boolean isLiked;
  private boolean isScrapped;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime createdAt;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime modifiedAt;

  public static FeedDetailDto of(FeedDto feedDto, Page<CommentDto> comment, MemberStateDocument memberState, LocalDateTime createdAt, LocalDateTime modifiedAt) {
    boolean liked = false;
    boolean scrapped = false;

    if (memberState != null) {
      liked = memberState.isFeedLiked(feedDto.feedId());
      scrapped = memberState.isScrapped(feedDto.feedId());
    }

    return FeedDetailDto.builder()
        .feed(feedDto)
        .comment(comment)
        .isLiked(liked)
        .isScrapped(scrapped)
        .createdAt(createdAt)
        .modifiedAt(modifiedAt)
        .build();
  }
}