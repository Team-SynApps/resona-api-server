package com.synapps.resona.query.service;

import com.synapps.resona.domain.entity.comment.Comment;
import com.synapps.resona.exception.CommentException;
import com.synapps.resona.domain.repository.comment.comment.CommentRepository;
import com.synapps.resona.entity.member.Member;
import com.synapps.resona.query.dto.comment.MentionResponse;
import com.synapps.resona.domain.entity.comment.Mention;
import com.synapps.resona.exception.MentionException;
import com.synapps.resona.domain.repository.comment.MentionRepository;
import com.synapps.resona.repository.member.MemberRepository;
import com.synapps.resona.service.MemberService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MentionService {

  private final MentionRepository mentionRepository;
  private final MemberService memberService;
  private final CommentRepository commentRepository;
  private final MemberRepository memberRepository;

  @Transactional
  public MentionResponse register(Long commentId) {
    Comment comment = commentRepository.findById(commentId)
        .orElseThrow(CommentException::commentNotFound);
    String email = memberService.getMemberEmail();
    Member member = memberRepository.findByEmail(email).orElseThrow();

    Mention mention = Mention.of(member, comment, LocalDateTime.now());
    mentionRepository.save(mention);

    return MentionResponse.from(mention);
  }

  public MentionResponse read(Long mentionId) {
    Mention mention = mentionRepository.findById(mentionId).orElseThrow(MentionException::mentionNotFound);
    return MentionResponse.from(mention);
  }

  @Transactional
  public void delete(Long mentionId) {
    Mention mention = mentionRepository.findById(mentionId)
        .orElseThrow(MentionException::mentionNotFound);
    mention.softDelete();
  }
}