package synapps.resona.api.mysql.member.entity.hobby;

import lombok.Getter;
import synapps.resona.api.mysql.member.entity.profile.Gender;

import java.util.Arrays;

@Getter
public enum GivenHobby {
    SWIMMING(0, "Swimming"),
    NOT_GIVEN(-1, "not given");

    private int num;
    private String name;

    GivenHobby(int num, String name) {
        this.num = num;
        this.name = name;
    }

    public static GivenHobby of(String name) {
        return Arrays.stream(GivenHobby.values())
                .filter(r -> r.toString().equals(name))
                .findAny()
                .orElse(NOT_GIVEN);
    }
}
