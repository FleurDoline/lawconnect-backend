package org.arited.lawconnect.core.entities;

import jakarta.persistence.*;
import lombok.*;
import org.arited.lawconnect.core.enums.StatutConsultationEnum;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "consultations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Consultation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "avocat_id", nullable = false)
    private Avocat avocat;

    @Column(nullable = false)
    private String flowType; // "message" ou "consultation"

    private String eligibilite;

    private String typePersonne;

    @Column(columnDefinition = "TEXT")
    private String mission;

    @ElementCollection
    @CollectionTable(name = "consultation_attentes", joinColumns = @JoinColumn(name = "consultation_id"))
    @Column(name = "attente")
    @Builder.Default
    private List<String> attentes = new ArrayList<>();

    private String urgent;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String situation;

    @Column(nullable = false)
    private String nomComplet;

    @Column(nullable = false)
    private String telephone;

    @Column(nullable = false)
    private String email;

    private String ville;

    private String contactPreference;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StatutConsultationEnum statut = StatutConsultationEnum.EN_ATTENTE;

    private LocalDateTime dateRendezVous;

    private String modeConsultation; // "visioconférence" ou "présentiel"

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}