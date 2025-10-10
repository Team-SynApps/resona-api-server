
package com.synapps.resona.report.query.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static org.mockito.Mockito.mock;
import com.synapps.resona.report.common.entity.ReportStatus;
import com.synapps.resona.report.dto.ReportDto;
import com.synapps.resona.report.query.entity.ReportDocument;
import com.synapps.resona.report.query.repository.ReportDocumentRepository;
import java.util.Collections;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class ReportQueryServiceTest {

    @InjectMocks
    private ReportQueryService reportQueryService;

    @Mock
    private ReportDocumentRepository reportDocumentRepository;

    @Test
    @DisplayName("상태별 신고 목록을 성공적으로 조회한다.")
    void getReportsByStatus_Success() {
        // given
        ReportStatus status = ReportStatus.PENDING;
        PageRequest pageable = PageRequest.of(0, 10);
        ReportDocument reportDocument = mock(ReportDocument.class);
        Page<ReportDocument> reportPage = new PageImpl<>(Collections.singletonList(reportDocument));

        when(reportDocumentRepository.findByStatusOrderByCreatedAtDesc(status, pageable)).thenReturn(reportPage);

        // when
        Page<ReportDto> result = reportQueryService.getReportsByStatus(status, pageable);

        // then
        assertThat(result).hasSize(1);
    }
}
