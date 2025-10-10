package com.synapps.resona.report.query.repository;

import com.synapps.resona.annotation.DatabaseRepositories.MongoDBRepository;
import com.synapps.resona.report.common.entity.ReportCategory;
import com.synapps.resona.report.common.entity.ReportStatus;
import com.synapps.resona.report.query.entity.ReportDocument;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

@MongoDBRepository
public interface ReportDocumentRepository extends MongoRepository<ReportDocument, String> {

  Optional<ReportDocument> findByReportId(Long reportId);

  /**
   * 특정 처리 상태(예: PENDING)에 있는 신고들을 최신순으로 페이지네이션하여 조회
   * @param status 조회할 신고 처리 상태
   * @param pageable 페이지 정보
   * @return 신고 문서 페이지
   */
  Page<ReportDocument> findByStatusOrderByCreatedAtDesc(ReportStatus status, Pageable pageable);

  /**
   * 특정 카테고리와 처리 상태에 맞는 신고들을 최신순으로 페이지네이션하여 조회
   * @param category 조회할 신고 사유
   * @param status 조회할 신고 처리 상태
   * @param pageable 페이지 정보
   * @return 신고 문서 페이지
   */
  Page<ReportDocument> findByCategoryAndStatusOrderByCreatedAtDesc(ReportCategory category, ReportStatus status, Pageable pageable);

}