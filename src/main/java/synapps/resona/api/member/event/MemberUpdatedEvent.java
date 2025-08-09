package synapps.resona.api.member.event;

import synapps.resona.api.member.entity.profile.Profile;


public record MemberUpdatedEvent(Long memberId, Profile profile) {
}
