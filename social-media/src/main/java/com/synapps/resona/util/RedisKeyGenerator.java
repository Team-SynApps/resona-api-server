package com.synapps.resona.util;

import com.synapps.resona.feed.command.entity.FeedCategory;

public class RedisKeyGenerator {

    private static final String USER_PREFIX = "user:";
    private static final String FEED_PREFIX = "feed:";
    private static final String TIMELINE_PREFIX = "timeline:";
    private static final String RECENT_FEEDS = "recent:feeds";
    private static final String CATEGORY_PREFIX = "category:";
    private static final String COUNTRY_PREFIX = "country:";

    public static String getMemberLikedFeedKey(Long memberId) {
        return USER_PREFIX + memberId + ":liked_feeds";
    }

    public static String getMemberScrappedFeedKey(Long memberId) {
        return USER_PREFIX + memberId + ":scrapped_feeds";
    }

    public static String getLikedCommentKey(Long memberId) {
        return USER_PREFIX + memberId + ":liked_comments";
    }

    public static String getLikedReplyKey(Long memberId) {
        return USER_PREFIX + memberId + ":liked_replies";
    }

    public static String getUserSeenFeedsKey(Long memberId) {
        return USER_PREFIX + memberId + ":seen_feeds";
    }

    public static String getHiddenFeedsKey(Long memberId) {
        return USER_PREFIX + memberId + ":hidden_feeds";
    }

    public static String getHiddenCommentsKey(Long memberId) {
        return USER_PREFIX + memberId + ":hidden_comments";
    }

    public static String getHiddenRepliesKey(Long memberId) {
        return USER_PREFIX + memberId + ":hidden_replies";
    }

    public static String getBlockedUsersKey(Long memberId) {
        return USER_PREFIX + memberId + ":blocked_users";
    }

    public static String getAllTimelineKey(Long memberId) {
        return TIMELINE_PREFIX + "all:" + memberId;
    }

    public static String getCategoryTimelineKey(Long memberId, FeedCategory category) {
        return TIMELINE_PREFIX + memberId + ":" + category.name().toLowerCase();
    }

    public static String getExploreCountryCategoryKey(String country, FeedCategory category) {
        return TIMELINE_PREFIX + "country:" + country + ":" + category.name().toLowerCase();
    }

    public static String getExploreCountryKey(String country) {
        return TIMELINE_PREFIX + "country:" + country;
    }

    public static String getExploreCategoryKey(FeedCategory category) {
        return TIMELINE_PREFIX + "category:" + category.name().toLowerCase();
    }

    public static String getExploreRecentKey() {
        return RECENT_FEEDS;
    }

    public static String getFanoutCategoryKey(FeedCategory category) {
        return TIMELINE_PREFIX + "category:" + category.name().toLowerCase();
    }

    public static String getFanoutCountryKey(String country) {
        return TIMELINE_PREFIX + "country:" + country;
    }

    public static String getFanoutCountryCategoryKey(String country, FeedCategory category) {
        return TIMELINE_PREFIX + "country:" + country + ":" + category.name().toLowerCase();
    }

    public static boolean isPersonalTimelineKey(String key, Long memberId) {
        String memberIdStr = String.valueOf(memberId);
        return key.equals(getAllTimelineKey(memberId)) || key.startsWith(TIMELINE_PREFIX + memberIdStr + ":");
    }

    public static boolean isExploreCountryCategoryKey(String key) {
        return key.matches(TIMELINE_PREFIX + COUNTRY_PREFIX + "\\w+:\\w+");
    }

    public static boolean isExploreCountryKey(String key) {
        return key.startsWith(TIMELINE_PREFIX + COUNTRY_PREFIX) && !key.contains(":");
    }

    public static boolean isExploreCategoryKey(String key) {
        return key.startsWith(TIMELINE_PREFIX + CATEGORY_PREFIX);
    }
}
