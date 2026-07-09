package org.arited.lawconnect.core.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "clients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@PrimaryKeyJoinColumn(name = "user_id")
public class Client extends User {

    // ID from auth-service (microservice link)
    @Column(name = "user_id", unique = true, nullable = false)
    private Long userId;
}