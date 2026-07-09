package org.arited.lawconnect.core.entities;

import org.arited.lawconnect.core.enums.AccesEnum;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "admins")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@PrimaryKeyJoinColumn(name = "user_id")
public class Admin extends User {

    @Column(name = "user_id", unique = true, nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccesEnum niveauAcces = AccesEnum.MODERATEUR;
}