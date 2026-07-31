package org.arited.lawconnect.core.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Table(
    name = "disponibilites",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_disponibilite_avocat_jour",
        columnNames = {"avocat_id", "jour"}
    )
)
@Getter
@Setter
public class Disponibilite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "disponibilite_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "avocat_id", nullable = false)
    private Avocat avocat;

    @Enumerated(EnumType.STRING)
    @Column(name = "jour", nullable = false)
    private DayOfWeek jour;

    @Column(name = "heure_debut", nullable = false)
    private LocalTime heureDebut;

    @Column(name = "heure_fin", nullable = false)
    private LocalTime heureFin;
}