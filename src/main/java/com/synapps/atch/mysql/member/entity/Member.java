package com.synapps.atch.mysql.member.entity;

import com.synapps.atch.oauth.entity.ProviderType;
import com.synapps.atch.oauth.entity.RoleType;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 15)
    private String nickname;

    @NotBlank
    @Size(max = 20)
    private String phoneNumber;

    @NotBlank
    @Size(max = 15)
    private Integer timezone;

    @NotBlank
    @Size(max = 15)
    private Integer age;

    @NotBlank
    @Size(max = 512)
    private LocalDateTime birth;

    @NotBlank
    @Size(max = 512)
    private String comment;

    @Enumerated(EnumType.STRING)
    @NotNull
    private Sex sex;

    @NotNull
    private Boolean isOnline;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @NotBlank
    @Size(max = 120)
    private String password;

    @NotNull
    private String location;

    @ElementCollection
    @CollectionTable(name = "member_languages", joinColumns = @JoinColumn(name = "member_id"))
    @Column(name = "language")
    @Enumerated(EnumType.STRING)
    private List<Languages> interestedLanguages;

    @Enumerated(EnumType.STRING)
    @Nullable
    private Category category;

    @Nullable
    @Size(max = 512)
    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    @NotNull
    private ProviderType providerType;

    @Enumerated(EnumType.STRING)
    @NotNull
    private RoleType roleType;

    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    private LocalDateTime modifiedAt;

    @NotNull
    private LocalDateTime lastAccessedAt;

    private Member(String nickname,
                 String email,
                 String profileImageUrl,
                 ProviderType providerType,
                 RoleType roleType,
                 LocalDateTime createdAt,
                 LocalDateTime modifiedAt) {
        this.nickname = nickname;
        this.password = "NO_PASS";
        this.email = email != null ? email : "NO_EMAIL";
        this.profileImageUrl = profileImageUrl != null ? profileImageUrl : "";
        this.providerType = providerType;
        this.roleType = roleType;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }
    private Member(
                 String nickname,
                 String email,
                 ProviderType providerType,
                 RoleType roleType,
                 LocalDateTime createdAt,
                 LocalDateTime modifiedAt) {
        this.nickname = nickname;
        this.password = "NO_PASS";
        this.email = email != null ? email : "NO_EMAIL";
        this.profileImageUrl = "";
        this.providerType = providerType;
        this.roleType = roleType;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static Member of(String nickname,
                          String email,
                          String profileImageUrl,
                          ProviderType providerType,
                          RoleType roleType,
                          LocalDateTime createdAt,
                          LocalDateTime modifiedAt) {
        return new Member(nickname, email, profileImageUrl, providerType, roleType, createdAt, modifiedAt);
    }
    public static Member of(
                          String nickname,
                          String email,
                          ProviderType providerType,
                          RoleType roleType,
                          LocalDateTime createdAt,
                          LocalDateTime modifiedAt) {
        return new Member(nickname,  email, providerType, roleType, createdAt, modifiedAt);
    }
    public void setUserNickname(String name) {
        this.nickname = name;
    }

    public void setProfileImageUrl(String imageUrl) {
        this.profileImageUrl = imageUrl;
    }

    public void encodePassword(String rawPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        this.password = passwordEncoder.encode(rawPassword);
    }
}