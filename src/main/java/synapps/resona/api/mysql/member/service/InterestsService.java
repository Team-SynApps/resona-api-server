package synapps.resona.api.mysql.member.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import synapps.resona.api.mysql.member.dto.request.interests.InterestsRequest;
import synapps.resona.api.mysql.member.dto.response.MemberDto;
import synapps.resona.api.mysql.member.entity.interests.Interests;
import synapps.resona.api.mysql.member.entity.member.Member;
import synapps.resona.api.mysql.member.repository.InterestsRepository;
import synapps.resona.api.mysql.member.repository.MemberRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class InterestsService {
    private final InterestsRepository interestsRepository;
    private final MemberService memberService;
    private final MemberRepository memberRepository;

    /**
     * Register Interests
     */
    @Transactional
    public Interests registerInterests(InterestsRequest request) {
        MemberDto memberDto = memberService.getMember();
        Member member = memberRepository.findById(memberDto.getId()).orElseThrow();

        Interests interests = Interests.of(member, request.getInterestedLanguages(), request.getHobbies(), LocalDateTime.now(), LocalDateTime.now());
        interestsRepository.save(interests);

        return interests;
    }

    /**
     * Get Interests
     */
    public Interests getInterests() {
        MemberDto memberDto = memberService.getMember();
        Member member = memberRepository.findById(memberDto.getId()).orElseThrow();

        return interestsRepository.findByMember(member).orElseThrow(() -> new RuntimeException("Interests not found"));
    }

    /**
     * Edit Interests
     */
    @Transactional
    public Interests editInterests(InterestsRequest request) {
        MemberDto memberDto = memberService.getMember();
        Member member = memberRepository.findById(memberDto.getId()).orElseThrow();

        Interests interests = interestsRepository.findByMember(member).orElseThrow(() -> new RuntimeException("Interests not found"));
        interests.modifyInterests(request.getInterestedLanguages(), request.getHobbies());
        return interests;
    }

    /**
     * Delete Interests
     */
    @Transactional
    public Interests deleteInterests() {
        MemberDto memberDto = memberService.getMember();
        Member member = memberRepository.findById(memberDto.getId()).orElseThrow();

        Interests interests = interestsRepository.findByMember(member)
                .orElseThrow(() -> new RuntimeException("Interests not found"));

        interests.softDelete();
        return interests;
    }
}
