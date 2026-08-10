package org.arited.lawconnect.core.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "et_notification_preference")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    @Builder.Default
    private boolean email = true;

    @Column(nullable = false)
    @Builder.Default
    private boolean sms = false;

    @Column(nullable = false)
    @Builder.Default
    private boolean push = false;

    @Column(name = "lettre_information", nullable = false)
    @Builder.Default
    private boolean lettreInformation = true;
}
