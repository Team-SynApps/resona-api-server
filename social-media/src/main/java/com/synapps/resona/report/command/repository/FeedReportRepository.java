package com.synapps.resona.report.command.repository;

import com.synapps.resona.annotation.DatabaseRepositories.MySQLRepository;
import com.synapps.resona.entity.member.Member;
import com.synapps.resona.feed.command.entity.Feed;
import com.synapps.resona.report.command.entity.FeedReport;
import org.springframework.data.jpa.repository.JpaRepository;

@MySQLRepository
public interface FeedReportRepository extends JpaRepository<FeedReport, Long> {

  /**
   * 특정 사용자가 특정 피드를 이미 신고했는지 확인하여 중복 신고를 방지
   * @param reporter 신고자
   * @param feed 신고 대상 피드
   * @return 신고 내역 존재 여부
   */
  boolean existsByReporterAndFeed(Member reporter, Feed feed);

}