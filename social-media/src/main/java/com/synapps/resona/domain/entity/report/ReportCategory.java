package com.synapps.resona.domain.entity.report;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReportCategory {

  // --- 영구 제재 (-1) ---
  IMPERSONATION("유출 / 사칭 / 사기", -1),
  COMMERCIAL_AD("상업적 광고 및 판매", -1),
  ILLEGAL_CONTENT("불법촬영물 등의 유통", -1),
  OBSCENE_CONTENT("음란물 / 불건전한 만남 및 대화", -1),

  // --- 기간제 제재 (일 단위) ---
  SPAM("낚시/놀람/도배", 7),
  ABUSE("욕설 / 비하", 7),
  POLITICAL_CONTENT("정당 / 정치인 비하 및 선거운동", 7),

  // --- 관리자 확인 필요 (0) ---
  ETC("기타", 0);

  private final String description;
  private final int sanctionDays;
}
