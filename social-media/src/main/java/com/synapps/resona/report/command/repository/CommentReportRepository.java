package com.synapps.resona.report.command.repository;

import com.synapps.resona.annotation.DatabaseRepositories.MySQLRepository;
import com.synapps.resona.comment.command.entity.comment.Comment;
import com.synapps.resona.command.entity.member.Member;
import com.synapps.resona.report.command.entity.CommentReport;
import org.springframework.data.jpa.repository.JpaRepository;

@MySQLRepository
public interface CommentReportRepository extends JpaRepository<CommentReport, Long> {
    boolean existsByReporterAndComment(Member reporter, Comment comment);
}
