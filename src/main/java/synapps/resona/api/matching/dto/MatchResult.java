package synapps.resona.api.matching.dto;

import java.util.List;

public record MatchResult(
    boolean isSuccess,
    List<Long> matchedMemberIds
) {
  public static MatchResult success(List<Long> memberIds) {
    return new MatchResult(true, memberIds);
  }

  public static MatchResult failure() {
    return new MatchResult(false, List.of());
  }
}