package synapps.resona.api.mysql.member.entity.interests;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Hobby {
    @Id
    @GeneratedValue
    @Column(name = "hobby_id")
    private Long id;

    @Column(name = "hobby_name")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "interests_id")
    private Interests interests;

    private Hobby(String name) {
        this.name = name;
    }

    public static Hobby of(String name) {
        return new Hobby(name);
    }
}
