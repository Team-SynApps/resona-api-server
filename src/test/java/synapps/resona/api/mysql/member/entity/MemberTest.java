package synapps.resona.api.mysql.member.entity;

import synapps.resona.api.oauth.entity.ProviderType;
import synapps.resona.api.oauth.entity.RoleType;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class MemberTest {

    @Test
    void testMemberCreation() {
        LocalDateTime now = LocalDateTime.now();
        Member member = Member.of(
                "nickname",
                "1234567890",
                0,
                now,
                "comment",
                Sex.MAN,
                false,
                "test@example.com",
                "password",
                "location",
                ProviderType.LOCAL,
                RoleType.USER,
                now,
                now,
                now
        );

        assertNotNull(member);
        assertEquals("nickname", member.getNickname());
        assertEquals("1234567890", member.getPhoneNumber());
        assertEquals(0, member.getTimezone());
        assertEquals(now, member.getBirth());
        assertEquals("comment", member.getComment());
        assertEquals(Sex.MAN, member.getSex());
        assertFalse(member.getIsOnline());
        assertEquals("test@example.com", member.getEmail());
        assertEquals("password", member.getPassword());
        assertEquals("location", member.getLocation());
        assertEquals(ProviderType.LOCAL, member.getProviderType());
        assertEquals(RoleType.USER, member.getRoleType());
        assertEquals(now, member.getCreatedAt());
        assertEquals(now, member.getModifiedAt());
        assertEquals(now, member.getLastAccessedAt());
    }

    @Test
    void testSetUserNickname() {
        Member member = createTestMember();
        member.setUserNickname("newNickname");
        assertEquals("newNickname", member.getNickname());
    }

    @Test
    void testSetProfileImageUrl() {
        Member member = createTestMember();
        member.setProfileImageUrl("http://example.com/image.jpg");
        assertEquals("http://example.com/image.jpg", member.getProfileImageUrl());
    }

    @Test
    void testEncodePassword() {
        Member member = createTestMember();
        String rawPassword = "password123";
        member.encodePassword(rawPassword);

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        assertTrue(encoder.matches(rawPassword, member.getPassword()));
    }

    @Test
    void testBirthToAge() {
        LocalDateTime birthDate = LocalDateTime.now().minusYears(25);
        Member member = createTestMember();
        member = Member.of(
                member.getNickname(),
                member.getPhoneNumber(),
                member.getTimezone(),
                birthDate,
                member.getComment(),
                member.getSex(),
                member.getIsOnline(),
                member.getEmail(),
                member.getPassword(),
                member.getLocation(),
                member.getProviderType(),
                member.getRoleType(),
                member.getCreatedAt(),
                member.getModifiedAt(),
                member.getLastAccessedAt()
        );

        assertEquals(25, member.getAge());
    }

    private Member createTestMember() {
        return Member.of(
                "nickname",
                "1234567890",
                0,
                LocalDateTime.now(),
                "comment",
                Sex.MAN,
                false,
                "test@example.com",
                "password",
                "location",
                ProviderType.LOCAL,
                RoleType.USER,
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }
}