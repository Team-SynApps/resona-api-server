package com.synapps.resona.mysql.member.service;

import com.synapps.resona.mysql.member.dto.request.DuplicateIdRequest;
import com.synapps.resona.mysql.member.dto.request.SignupRequest;
import com.synapps.resona.mysql.member.dto.response.MemberDto;
import com.synapps.resona.mysql.member.entity.Member;
import com.synapps.resona.mysql.member.entity.Sex;
import com.synapps.resona.mysql.member.exception.MemberException;
import com.synapps.resona.mysql.member.repository.MemberRepository;
import com.synapps.resona.oauth.entity.ProviderType;
import com.synapps.resona.oauth.entity.RoleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberService memberService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getMember_Success() {
        // Given
        String email = "test@example.com";
        Member mockMember = createMockMember(email);
        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(mockMember));

        // Mock SecurityContext
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(new User(email, "password", java.util.Collections.emptyList()));

        // When
        Member result = memberService.getMember();

        // Then
        assertNotNull(result);
        assertEquals(email, result.getEmail());
    }

    @Test
    void signUp_Success() throws Exception {
        // Given
        SignupRequest request = createSignupRequest();
        when(memberRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(memberRepository.save(any(Member.class))).thenReturn(createMockMember(request.getEmail()));

        // When
        MemberDto result = memberService.signUp(request);

        // Then
        assertNotNull(result);
        assertEquals(request.getEmail(), result.getEmail());
        verify(memberRepository).save(any(Member.class));
    }

    @Test
    void signUp_DuplicateEmail() {
        // Given
        SignupRequest request = createSignupRequest();
        when(memberRepository.existsByEmail(request.getEmail())).thenReturn(true);

        // When & Then
        assertThrows(MemberException.class, () -> memberService.signUp(request));
    }

    @Test
    void checkDuplicateId_Exists() throws Exception {
        // Given
        DuplicateIdRequest request = new DuplicateIdRequest();
        request.setId("1");
        when(memberRepository.existsById(1L)).thenReturn(true);

        // When
        boolean result = memberService.checkDuplicateId(request);

        // Then
        assertTrue(result);
    }

    @Test
    void deleteUser_Success() {
        // Given
        String email = "test@example.com";
        Member mockMember = createMockMember(email);
        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(mockMember));

        // Mock SecurityContext
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(new User(email, "password", java.util.Collections.emptyList()));

        // When
        String result = memberService.deleteUser();

        // Then
        assertEquals("delete successful", result);
        verify(memberRepository).delete(mockMember);
    }

    private Member createMockMember(String email) {
        return Member.of(
                "nickname",
                "1234567890",
                0,
                LocalDateTime.now(),
                "comment",
                Sex.MAN,
                false,
                email,
                "password",
                "location",
                ProviderType.LOCAL,
                RoleType.USER,
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    private SignupRequest createSignupRequest() {
        SignupRequest request = new SignupRequest();
        request.setEmail("test@example.com");
        request.setPassword("password");
        request.setNickname("nickname");
        request.setPhoneNumber("1234567890");
        request.setTimezone(0);
        request.setAge(25);
        request.setBirth("1999-08-25");
        request.setComment("comment");
        request.setSex("MALE");
        request.setLocation("location");
        request.setProviderType("LOCAL");
        request.setCode("code");
        return request;
    }
}