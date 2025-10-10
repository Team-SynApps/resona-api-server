package com.synapps.resona.retrieval.dto;

import com.synapps.resona.common.entity.Author;
import com.synapps.resona.retrieval.query.entity.FeedDocument.LocationEmbed;
import com.synapps.resona.retrieval.query.entity.FeedDocument.MediaEmbed;
import java.util.List;

public record FeedDto(
    Long feedId,
    Author author,
    String content,
    List<MediaEmbed> medias,
    LocationEmbed location,
    String category,
    String language,
    long likeCount,
    long commentCount,
    String translatedContent
) {}