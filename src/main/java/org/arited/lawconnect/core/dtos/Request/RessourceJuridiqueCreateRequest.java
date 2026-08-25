package org.arited.lawconnect.core.dtos.Request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RessourceJuridiqueCreateRequest {
    private String titre;
    private String auteur;
    private String description;
    private Long specialiteId;
}
