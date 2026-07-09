package org.arited.lawconnect.core.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "specialite_droit")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpecialiteDroit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nom; // e.g. "Divorce", "Droit pénal", "Garde à vue"
}
