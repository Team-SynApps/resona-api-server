package com.synapps.resona.comment.command.repository;

import com.synapps.resona.annotation.DatabaseRepositories.MySQLRepository;
import com.synapps.resona.comment.command.entity.comment.CommentTranslation;
import org.springframework.data.jpa.repository.JpaRepository;

@MySQLRepository
public interface CommentTranslationRepository extends JpaRepository<CommentTranslation, Long> {
}
