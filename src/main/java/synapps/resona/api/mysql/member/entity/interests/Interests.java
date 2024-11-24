package synapps.resona.api.mysql.member.entity.interests;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import synapps.resona.api.mysql.member.entity.Language;
import synapps.resona.api.mysql.member.entity.Member;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Interests {
    @Id
    @GeneratedValue
    @Column(name = "interests_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "member_id") // 외래 키 컬럼 이름
    private Member member;

    @ElementCollection
    @CollectionTable(name = "interests", joinColumns = @JoinColumn(name = "interests_id"))
    private Set<Language> interestedLanguages;

    @OneToMany(mappedBy = "interests", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Hobby> hobbies = new HashSet<>();

    private Interests(Member member, Set<Language> interestedLanguages, Set<Hobby> hobbies) {
        this.member = member;
        this.interestedLanguages = interestedLanguages;
        this.hobbies = hobbies;
    }

    public static Interests of(Member member, Set<Language> interestedLanguages, Set<Hobby> hobbies) {
        return new Interests(member, interestedLanguages, hobbies);
    }
}
