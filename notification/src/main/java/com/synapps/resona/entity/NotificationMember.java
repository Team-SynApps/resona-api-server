package com.synapps.resona.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationMember {

    @Id
    private Long id;

    private NotificationMember(Long id) {
        this.id = id;
    }

    public static NotificationMember of(Long id) {
        return new NotificationMember(id);
    }
}
