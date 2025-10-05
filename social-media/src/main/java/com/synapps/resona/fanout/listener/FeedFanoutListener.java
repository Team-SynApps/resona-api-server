package com.synapps.resona.fanout.listener;

import com.synapps.resona.fanout.client.MemberServiceClient;
import com.synapps.resona.fanout.dto.FollowerDto;
import com.synapps.resona.fanout.dto.PaginatedResult;
import com.synapps.resona.feed.event.FeedCreatedEvent;
import com.synapps.resona.service.FollowService;
import java.time.ZoneOffset;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

public class FeedFanoutListener {


}