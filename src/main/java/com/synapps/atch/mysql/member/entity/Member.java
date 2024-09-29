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

    private Integer timezone;

    private Integer age;

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
//
//    @Enumerated(EnumType.STRING)
//    private List<Languages> interestedLanguages;

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
                   String phoneNumber,
                   Integer timezone,
                   LocalDateTime birth,
                   String comment,
                   Sex sex,
                   Boolean isOnline,
                   String email,
                   String password,
                   String location,
                   ProviderType providerType,
                   RoleType roleType,
                   LocalDateTime createdAt,
                   LocalDateTime modifiedAt,
                   LocalDateTime lastAccessedAt) {
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.timezone = timezone;
        this.birth = birth;
        this.age = birthToAge(birth);
        this.comment = comment;
        this.sex = sex;
        this.isOnline = isOnline;
        this.email = email;
        this.password = password;
        this.location = location;
        this.providerType = providerType;
        this.roleType = roleType;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.lastAccessedAt = lastAccessedAt;
    }

    public static Member of(String nickname,
                            String phoneNumber,
                            Integer timezone,
                            LocalDateTime birth,
                            String comment,
                            Sex sex,
                            Boolean isOnline,
                            String email,
                            String password,
                            String location,
                            ProviderType providerType,
                            RoleType roleType,
                            LocalDateTime createdAt,
                            LocalDateTime modifiedAt,
                            LocalDateTime lastAccessedAt) {
        return new Member(nickname, phoneNumber, timezone, birth, comment, sex, isOnline, email, password, location, providerType, roleType, createdAt, modifiedAt, lastAccessedAt);
    }

    // 선택적 필드를 위한 추가 of 메소드
    public static Member ofWithOptionals(String nickname,
                                         String phoneNumber,
                                         Integer timezone,
                                         LocalDateTime birth,
                                         String comment,
                                         Sex sex,
                                         Boolean isOnline,
                                         String email,
                                         String password,
                                         String location,
                                         ProviderType providerType,
                                         RoleType roleType,
                                         LocalDateTime createdAt,
                                         LocalDateTime modifiedAt,
                                         LocalDateTime lastAccessedAt,
                                         Category category,
                                         String profileImageUrl) {
        Member member = new Member(nickname, phoneNumber, timezone, birth, comment, sex, isOnline, email, password, location, providerType, roleType, createdAt, modifiedAt, lastAccessedAt);
        member.category = category;
        member.profileImageUrl = profileImageUrl;
        return member;
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
    private Integer birthToAge(LocalDateTime birth) {
        Integer birthYear = birth.getYear();
        return LocalDateTime.now().getYear() - birthYear;
    }
}