package synapps.resona.api.mysql.member.entity.member;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import synapps.resona.api.global.entity.BaseEntity;
import synapps.resona.api.mysql.member.entity.account.AccountInfo;
import synapps.resona.api.mysql.member.entity.member_details.MemberDetails;
import synapps.resona.api.mysql.member.entity.profile.Profile;
import synapps.resona.api.mysql.socialMedia.entity.*;
import synapps.resona.api.mysql.socialMedia.entity.comment.Comment;
import synapps.resona.api.mysql.socialMedia.entity.feed.Feed;
import synapps.resona.api.mysql.socialMedia.entity.feed.Scrap;
import synapps.resona.api.mysql.socialMedia.entity.feedComplaint.FeedComplaint;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @Size(max = 120)
    private String password;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "account_info_id")
    private AccountInfo accountInfo;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "member_details_id")
    private MemberDetails memberDetails;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "profile_id")
    private Profile profile;

    @OneToMany(mappedBy = "follower")
    private List<Follow> followings = new ArrayList<>();

    @OneToMany(mappedBy = "following")
    private List<Follow> followers = new ArrayList<>();
  
    @OneToMany(mappedBy = "member")
    private final List<Feed> feeds = new ArrayList<>();

    @OneToMany(mappedBy = "complainer")
    private List<FeedComplaint> complainers = new ArrayList<>();

    @OneToMany(mappedBy = "complainTo")
    private List<FeedComplaint> complainedMembers = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private final List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private final List<Mention> mentions = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private final List<Scrap> scraps = new ArrayList<>();

    @NotNull
    @Column(name = "last_accessed_at")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime lastAccessedAt;

    private Member(AccountInfo accountInfo,
                   String email,
                   String password,
                   LocalDateTime lastAccessedAt) {
        this.accountInfo = accountInfo;
        this.email = email;
        this.password = password;
        this.lastAccessedAt = lastAccessedAt;
    }

    private Member(AccountInfo accountInfo,
                   MemberDetails memberDetails,
                   Profile profile,
                   String email,
                   String password,
                   LocalDateTime lastAccessedAt) {
        this.accountInfo = accountInfo;
        this.memberDetails = memberDetails;
        this.profile = profile;
        this.email = email;
        this.password = password;
        this.lastAccessedAt = lastAccessedAt;
    }

    public static Member of(AccountInfo accountInfo,
                            String email,
                            String password,
                            LocalDateTime lastAccessedAt) {
        return new Member(accountInfo, email, password, lastAccessedAt);
    }

    public static Member of(AccountInfo accountInfo,
                            MemberDetails memberDetails,
                            Profile profile,
                            String email,
                            String password,
                            LocalDateTime lastAccessedAt) {
        return new Member(accountInfo, memberDetails, profile, email, password, lastAccessedAt);
    }

    public void encodePassword(String rawPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        this.password = passwordEncoder.encode(rawPassword);
    }
}