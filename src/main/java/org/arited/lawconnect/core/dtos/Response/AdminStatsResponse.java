package org.arited.lawconnect.core.dtos.Response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminStatsResponse {
    private long totalAvocats;
    private long avocatsValides;
    private long avocatsEnAttente;
    private double tauxConversion; // avocatsValides / totalAvocats * 100, arrondi 1 décimale
}
