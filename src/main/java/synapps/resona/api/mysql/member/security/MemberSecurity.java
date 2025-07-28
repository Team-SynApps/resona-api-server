package synapps.resona.api.mysql.member.security;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import synapps.resona.api.oauth.entity.UserPrincipal; // UserPrincipal import

@Component("memberSecurity")
public class MemberSecurity {

  private static final Logger log = LogManager.getLogger(MemberSecurity.class);

  // AuthTokenProvider 의존성 제거
  public MemberSecurity() {
    log.debug("MemberSecurity bean created.");
  }

  /**
   * 현재 인증된 사용자가 주어진 memberId의 소유자인지 확인합니다.
   *
   * @param memberId 확인할 사용자의 ID
   * @return 소유자가 맞으면 true, 아니면 false
   */
  public boolean isOwner(Long memberId) {
    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

      // 인증 정보가 없거나, 인증되지 않았거나, Principal이 비정상적인 경우 실패 처리
      if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof UserPrincipal)) {
        log.warn("Cannot check owner: Authentication is invalid or principal is not UserPrincipal.");
        return false;
      }

      UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

      boolean result = userPrincipal.getMemberId().equals(memberId);
      log.debug("Ownership check for memberId '{}'. Current user is '{}'. Result: {}",
          memberId, userPrincipal.getMemberId(), result);

      return result;

    } catch (Exception e) {
      log.error("Error during ownership check for memberId: {}", memberId, e);
      return false;
    }
  }
}