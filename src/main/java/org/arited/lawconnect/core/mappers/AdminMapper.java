package org.arited.lawconnect.core.mappers;

import org.arited.lawconnect.core.dtos.Request.AdminCreateRequest;
import org.arited.lawconnect.core.dtos.Request.AdminUpdateRequest;
import org.arited.lawconnect.core.dtos.Response.AdminResponse;
import org.arited.lawconnect.core.entities.Admin;
import org.arited.lawconnect.core.enums.AccesEnum;
import org.springframework.stereotype.Component;

@Component
public class AdminMapper {

    public Admin toEntity(AdminCreateRequest request) {
        Admin admin = new Admin();
        admin.setEmail(request.getEmail());
        admin.setFullName(request.getFullName());
        admin.setNiveauAcces(
            request.getNiveauAcces() != null ? request.getNiveauAcces() : AccesEnum.MODERATEUR
        );
        return admin;
    }

    public void updateEntity(AdminUpdateRequest request, Admin admin) {
        if (request.getFullName() != null) {
            admin.setFullName(request.getFullName());
        }
        if (request.getNiveauAcces() != null) {
            admin.setNiveauAcces(request.getNiveauAcces());
        }
    }

    public AdminResponse toResponse(Admin admin) {
        return AdminResponse.builder()
            .userId(admin.getUserId())
            .email(admin.getEmail())
            .fullName(admin.getFullName())
            .role(admin.getRole() != null ? admin.getRole().name() : null)
            .niveauAcces(admin.getNiveauAcces())
            .createdAt(admin.getCreatedAt())
            .updatedAt(admin.getUpdatedAt())
            .active(admin.isActive())
            .build();
    }
}