package org.arited.lawconnect.core.repositories;

import org.arited.lawconnect.core.entities.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByDestinataireUserIdOrderByDateCreationDesc(Long destinataireId, Pageable pageable);

    Page<Notification> findByDestinataireUserIdAndLuFalseOrderByDateCreationDesc(Long destinataireId, Pageable pageable);

    long countByDestinataireUserIdAndLuFalse(Long destinataireId);

    @Modifying
    @Query("UPDATE Notification n SET n.lu = true WHERE n.destinataire.userId = :userId AND n.lu = false")
    int markAllAsRead(@Param("userId") Long userId);
}