package com.synapps.resona.comment.command.repository;

import com.synapps.resona.annotation.DatabaseRepositories.MySQLRepository;
import com.synapps.resona.comment.command.entity.Mention;
import com.synapps.resona.comment.command.entity.MentionableContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@MySQLRepository
public interface MentionRepository extends JpaRepository<Mention, Long> {

    @Modifying
    @Query("UPDATE Mention m SET m.deleted = true WHERE m.target = :target")
    void softDeleteAllByTarget(@Param("target") MentionableContent target);
}
