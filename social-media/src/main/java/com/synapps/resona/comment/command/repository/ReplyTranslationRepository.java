package com.synapps.resona.comment.command.repository;

import com.synapps.resona.annotation.DatabaseRepositories.MySQLRepository;
import com.synapps.resona.comment.command.entity.reply.ReplyTranslation;
import org.springframework.data.jpa.repository.JpaRepository;

@MySQLRepository
public interface ReplyTranslationRepository extends JpaRepository<ReplyTranslation, Long> {
}
