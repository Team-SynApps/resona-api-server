package synapps.resona.api.mysql.social_media.entity;

import java.util.Arrays;

public enum FeedCategory {
    EX1, EX2, EX3, NULL;

    public static FeedCategory of(String s) {
        return Arrays.stream(FeedCategory.values())
                .filter(r -> r.toString().equals(s))
                .findAny().orElse(NULL);
    }
}
