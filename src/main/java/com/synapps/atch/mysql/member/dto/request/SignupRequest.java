package com.synapps.atch.mysql.member.dto.request;

import com.synapps.atch.oauth.entity.ProviderType;
import com.synapps.atch.oauth.entity.RoleType;
import com.synapps.atch.mysql.member.entity.Category;
import com.synapps.atch.mysql.member.entity.Sex;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class SignupRequest {

    @NotBlank
    @Size(max = 15)
    private String nickname;

    @NotBlank
    @Size(max = 20)
    private String phoneNumber;

    @NotNull
    private Integer timezone;

    @NotNull
    private Integer age;

    @NotNull
    private LocalDateTime birth;

    @Size(max = 512)
    private String comment;

    @NotNull
    private String sex;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @NotBlank
    @Size(max = 120)
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,30}$",
            message = "비밀번호는 8~30 자리이면서 1개 이상의 알파벳, 숫자, 특수문자를 포함해야합니다.")
    private String password;

    @NotNull
    private String location;

    private String providerType;

    private String category;

    private String profileImageUrl;

    // code 필드는 유지
    private String code;
}