package com.synapps.resona.listener;

import com.synapps.resona.entity.MemberNotificationSetting;
import com.synapps.resona.entity.NotificationMember;
import com.synapps.resona.event.MemberRegisteredEvent;
import com.synapps.resona.repository.MemberNotificationSettingRepository;
import com.synapps.resona.repository.NotificationMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class MemberNotificationEventListener {

    private final NotificationMemberRepository notificationMemberRepository;
    private final MemberNotificationSettingRepository notificationSettingRepository;

    @EventListener
    @Transactional
    public void handleMemberRegisteredEvent(MemberRegisteredEvent event) {
        NotificationMember member = NotificationMember.of(event.memberId());
        notificationMemberRepository.save(member);

        MemberNotificationSetting newSetting = MemberNotificationSetting.of(member, true, true, true, true);
        notificationSettingRepository.save(newSetting);
    }
}
