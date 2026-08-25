package org.arited.lawconnect.core.dtos.Response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RessourceJuridiqueResponse {
    private Long id;
    private String titre;
    private String auteur;
    private String description;
    private String specialiteNom;
    private Long specialiteId;
    private String cheminFichier;
}
