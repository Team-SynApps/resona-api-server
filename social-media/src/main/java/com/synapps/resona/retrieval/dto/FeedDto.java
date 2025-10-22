package com.synapps.resona.retrieval.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.synapps.resona.common.entity.Author;
import com.synapps.resona.feed.command.entity.FeedCategory;
import com.synapps.resona.retrieval.query.entity.FeedDocument.LocationEmbed;
import com.synapps.resona.retrieval.query.entity.FeedDocument.MediaEmbed;
import java.time.LocalDateTime;
import java.util.List;

public record FeedDto(
    Long feedId,
    Author author,
    FeedCategory category,
    String content,
    List<MediaEmbed> medias,
    LocationEmbed location,
    String languageCode,
    long likeCount,
    long commentCount,
    String translatedContent,
    boolean hasLiked,
    boolean hasScraped,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime createdAt,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime modifiedAt
) {}