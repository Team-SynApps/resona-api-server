package com.synapps.resona.retrieval.dto;

import com.synapps.resona.comment.dto.CommentDto;
import com.synapps.resona.query.member.entity.MemberStateDocument;
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

  public static FeedDetailDto of(FeedDto feedDto, Page<CommentDto> comment, MemberStateDocument memberState) {
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
        .build();
  }
}