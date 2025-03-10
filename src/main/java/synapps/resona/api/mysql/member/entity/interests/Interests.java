package synapps.resona.api.mysql.member.entity.interests;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import synapps.resona.api.global.entity.BaseEntity;
import synapps.resona.api.mysql.member.entity.member.Member;
import synapps.resona.api.mysql.member.entity.profile.Language;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Interests extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "interests_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "member_id") // 외래 키 컬럼 이름
    private Member member;

    @ElementCollection
    @CollectionTable(name = "interests", joinColumns = @JoinColumn(name = "interests_id"))
    private Set<Language> interestedLanguages;

    @ElementCollection
    @CollectionTable(name = "hobbies", joinColumns = @JoinColumn(name = "interests_id"))
    private Set<String> hobbies = new HashSet<>();

    private Interests(Member member, List<String> interestedLanguages, List<String> hobbies) {
        this.member = member;
        this.interestedLanguages = parseLanguages(interestedLanguages);
        this.hobbies = new HashSet<>(hobbies);
    }

    public static Interests of(Member member, List<String> interestedLanguages, List<String> hobbies) {
        return new Interests(member, interestedLanguages, hobbies);
    }

    private Set<Language> parseLanguages(List<String> interestedLanguages) {
        Set<Language> languages = new HashSet<>();
        for (String language : interestedLanguages) {
            languages.add(Language.fromCode(language));
        }
        return languages;
    }

    public void modifyInterests(List<String> interestedLanguages, List<String> hobbies) {
        this.interestedLanguages = parseLanguages(interestedLanguages);
        this.hobbies = new HashSet<>(hobbies);
    }
}
