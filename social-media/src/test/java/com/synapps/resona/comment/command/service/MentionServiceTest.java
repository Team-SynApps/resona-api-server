
package com.synapps.resona.comment.command.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.synapps.resona.comment.query.entity.MentionedMember;
import com.synapps.resona.entity.member.Member;
import com.synapps.resona.service.MemberService;
import fixture.MemberFixture;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

@ExtendWith(MockitoExtension.class)
class MentionServiceTest {

    @InjectMocks
    private MentionService mentionService;

    @Mock
    private MemberService memberService;

    @Test
    @DisplayName("사용자 ID 목록을 성공적으로 MentionedMember 목록으로 변환한다.")
    void parseMentions_Success() throws NoSuchFieldException, IllegalAccessException {
        // given
        Member member1 = MemberFixture.createProfileTestMember("test1@gmail.com");
        Field field1 = member1.getClass().getDeclaredField("id");
        field1.setAccessible(true);
        field1.set(member1, 1L);

        Member member2 = MemberFixture.createProfileTestMember("test2@gmail.com");
        Field field2 = member2.getClass().getDeclaredField("id");
        field2.setAccessible(true);
        field2.set(member2, 2L);

        List<Long> memberIds = List.of(member1.getId(), member2.getId());

        when(memberService.getMemberWithProfile(member1.getId())).thenReturn(member1);
        when(memberService.getMemberWithProfile(member2.getId())).thenReturn(member2);

        // when
        List<MentionedMember> result = mentionService.parseMentions(memberIds);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getMemberId()).isEqualTo(member1.getId());
        assertThat(result.get(0).getNickname()).isEqualTo(member1.getProfile().getNickname());
        assertThat(result.get(1).getMemberId()).isEqualTo(member2.getId());
        assertThat(result.get(1).getNickname()).isEqualTo(member2.getProfile().getNickname());
    }

    @Test
    @DisplayName("빈 사용자 ID 목록을 받으면 빈 목록을 반환한다.")
    void parseMentions_EmptyList() {
        // given
        List<Long> memberIds = List.of();

        // when
        List<MentionedMember> result = mentionService.parseMentions(memberIds);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("null을 받으면 빈 목록을 반환한다.")
    void parseMentions_Null() {
        // when
        List<MentionedMember> result = mentionService.parseMentions(null);

        // then
        assertThat(result).isEmpty();
    }
}
