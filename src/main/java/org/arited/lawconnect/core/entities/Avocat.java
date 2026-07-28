package org.arited.lawconnect.core.entities;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.arited.lawconnect.core.enums.StatutAvocatEnum;

@Entity
@Table(name = "avocats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Avocat extends User {

    private LocalDate validAt;
    private Long validBy;
    private String lienAgenda;

    private String carteProfessionnel;
    private String diplome;
    private String pieceIdentite;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(precision = 10, scale = 2)
    private BigDecimal tarif;

    private Integer experience;
    private String photo;
    
    @Column(name = "telephone")
    private String telephone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutAvocatEnum statut = StatutAvocatEnum.EN_ATTENTE;
    

    @Column(precision = 3, scale = 2)
    private BigDecimal noteMoyenne = BigDecimal.ZERO;

    private String adresseCabinet;
    private String ville;
    private Integer progression = 0;

    @ManyToMany
    @JoinTable(
        name = "avocat_specialite",
        joinColumns = @JoinColumn(name = "avocat_id"),
        inverseJoinColumns = @JoinColumn(name = "specialite_id")
    )
    private Set<SpecialiteDroit> specialites = new HashSet<>();

    @OneToMany(mappedBy = "avocat", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Abonnement> abonnements = new ArrayList<>();
}