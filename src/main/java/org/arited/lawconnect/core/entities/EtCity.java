package org.arited.lawconnect.core.entities;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "et_city")
public class EtCity {
    @Id @GeneratedValue
    private Long id;
    private String cityName;
    private String cityRegion;
    private Boolean active;
    // getters/setters


}