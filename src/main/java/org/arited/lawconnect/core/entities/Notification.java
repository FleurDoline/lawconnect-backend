package org.arited.lawconnect.core.entities;

import org.arited.lawconnect.core.enums.NotificationType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "et_notification")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "destinataire_id", nullable = false)
    private User destinataire;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private NotificationType type;

    @Column(nullable = false, length = 150)
    private String titre;

    @Column(nullable = false, length = 500)
    private String message;

    /** Frontend route to navigate to when the notification is clicked, e.g. /client/consultations/42 */
    @Column(length = 255)
    private String lien;

    @Column(nullable = false)
    @Builder.Default
    private boolean lu = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @PrePersist
    protected void onCreate() {
        this.dateCreation = LocalDateTime.now();
    }
}