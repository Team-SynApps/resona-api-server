package synapps.resona.api.mysql.member.entity.personal_info;

import java.util.Arrays;

public enum Gender {
    MAN, WOMAN, OTHER;

    public static Gender of(String sex) {
        return Arrays.stream(Gender.values())
                .filter(r -> r.toString().equals(sex))
                .findAny()
                .orElse(OTHER);
    }
}
