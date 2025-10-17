package com.synapps.resona.report.command.repository;

import com.synapps.resona.annotation.DatabaseRepositories.MySQLRepository;
import com.synapps.resona.comment.command.entity.reply.Reply;
import com.synapps.resona.command.entity.member.Member;
import com.synapps.resona.report.command.entity.ReplyReport;
import org.springframework.data.jpa.repository.JpaRepository;

@MySQLRepository
public interface ReplyReportRepository extends JpaRepository<ReplyReport, Long> {
    boolean existsByReporterAndReply(Member reporter, Reply reply);
}
