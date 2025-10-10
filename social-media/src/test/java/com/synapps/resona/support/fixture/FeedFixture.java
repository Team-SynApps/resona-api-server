package com.synapps.resona.support.fixture;

import com.synapps.resona.entity.member.Member;
import com.synapps.resona.feed.command.entity.Feed;
import com.synapps.resona.feed.dto.request.FeedRequest;
import com.synapps.resona.feed.dto.request.LocationRequest;
import com.synapps.resona.file.dto.FileMetadataDto;
import java.time.LocalDateTime;

public class FeedFixture {

  public static final String CONTENT = "피드 내용입니다.";
  public static final String CATEGORY = "DAILY";
  public static final String LANGUAGE_CODE = "ko";
  public static final String LOCATION_NAME = "서울";
  public static final String ADDRESS = "서울특별시 강남구";
  public static final String COORDINATE = "37.4979, 127.0276";
  public static final String FINAL_FILE_URL = "https://cdn.resona.com/images/final.jpg";
  public static final String DELETE_CONTENT = "삭제될 피드";

  public static LocationRequest createLocationRequest() {
    return new LocationRequest(COORDINATE, ADDRESS, LOCATION_NAME);
  }

  public static FeedRequest createFeedRequest() {
    return new FeedRequest(CONTENT, CATEGORY, createLocationRequest(), LANGUAGE_CODE);
  }

  public static FileMetadataDto createFileMetadataDto() {
    return FileMetadataDto.builder()
        .originalFileName("original.jpg")
        .temporaryFileName("temp.jpg")
        .contentType("image/jpeg")
        .uploadTime(LocalDateTime.now().toString())
        .width(1080).height(1920).fileSize(1000L).index(0)
        .build();
  }

  public static Feed createDeletableFeed(Member member) {
    return Feed.of(member, DELETE_CONTENT, CATEGORY, LANGUAGE_CODE);
  }
}