package com.synapps.atch.mysql.member.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class MemberRefreshToken {
    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long refreshTokenSeq;

    @Column(length = 64, unique = true)
    @NotNull
    @Size(max = 64)
    private String memberId;

    @Column(length = 256)
    @NotNull
    @Size(max = 256)
    private String refreshToken;

    public MemberRefreshToken(
            @NotNull @Size(max = 64) String memberId,
            @NotNull @Size(max = 256) String refreshToken
    ) {
        this.memberId = memberId;
        this.refreshToken = refreshToken;
    }
}