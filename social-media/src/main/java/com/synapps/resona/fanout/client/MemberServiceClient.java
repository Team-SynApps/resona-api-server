package com.synapps.resona.fanout.client;


import com.synapps.resona.fanout.dto.FollowerDto;
import com.synapps.resona.fanout.dto.PaginatedResult;

public interface MemberServiceClient {
  PaginatedResult<FollowerDto> getFollowers(Long memberId, int page, int size);
}

