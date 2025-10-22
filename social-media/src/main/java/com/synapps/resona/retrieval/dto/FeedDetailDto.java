package com.synapps.resona.retrieval.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.synapps.resona.common.dto.CommentDto;
import com.synapps.resona.query.entity.MemberStateDocument;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
@Builder
public class FeedDetailDto {

  private FeedDto feed;
  private Page<CommentDto> comment;

  private boolean hasLiked;
  private boolean hasScrapped;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime createdAt;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime modifiedAt;

  public static FeedDetailDto of(FeedDto feedDto, Page<CommentDto> comment, LocalDateTime createdAt, LocalDateTime modifiedAt) {

    return FeedDetailDto.builder()
        .feed(feedDto)
        .comment(comment)
        .hasLiked(feedDto.hasLiked())
        .hasScrapped(feedDto.hasScraped())
        .createdAt(createdAt)
        .modifiedAt(modifiedAt)
        .build();
  }
}