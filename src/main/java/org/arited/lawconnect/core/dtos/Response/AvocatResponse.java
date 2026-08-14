package org.arited.lawconnect.core.dtos.Response;

import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.arited.lawconnect.core.enums.RoleEnum;
import org.arited.lawconnect.core.enums.StatutAvocatEnum;
import org.arited.lawconnect.core.enums.TypePieceIdentiteEnum;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvocatResponse {
    private Long id;
    private Long userId;
    private String fullName;
    private String prenom;
    private String nom;
    private String email;
    private String telephone;
    private RoleEnum role;
    private boolean isActive;
    private Instant createdAt;
    private Instant updatedAt;
    private LocalDate validAt;
    private Long validBy;
    private String lienAgenda;

    private List<String> specialites;

    private String carteProfessionnel;
    private String diplome;
    private String pieceIdentite;
    private String pieceIdentiteRecto;
    private String pieceIdentiteVerso;
    private TypePieceIdentiteEnum typePieceIdentite;    
    private String bio;
    private BigDecimal tarif;
    private Integer experience;
    private String photo;
    private StatutAvocatEnum statut;
    private BigDecimal noteMoyenne;
    private String adresseCabinet;
    private String ville;
    private Integer progression;
    private boolean gereDisponibilites;
}