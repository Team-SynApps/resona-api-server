package synapps.resona.api.mysql.social_media.entity;

import java.util.Arrays;

public enum MediaType {
    IMAGE, VIDEO, OTHER;

    public static MediaType of(String s) {
        return Arrays.stream(MediaType.values())
                .filter(m -> m.toString().equals(s))
                .findAny().orElse(OTHER);
    }
}
