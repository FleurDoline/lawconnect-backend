package org.arited.lawconnect.core.services;

import org.arited.lawconnect.core.dtos.Pagination.PageResponse;
import org.arited.lawconnect.core.dtos.Request.AdminCreateRequest;
import org.arited.lawconnect.core.dtos.Request.AdminUpdateRequest;
import org.arited.lawconnect.core.dtos.Response.AdminResponse;
import org.arited.lawconnect.core.enums.AccesEnum;

import java.util.List;

public interface AdminService {
    AdminResponse createAdmin(AdminCreateRequest request);
    AdminResponse getAdminById(Long id);
    AdminResponse getAdminByUserId(Long userId);
    PageResponse<AdminResponse> getAllAdmins(int page, int size);
    List<AdminResponse> getAdminsByNiveauAcces(AccesEnum niveauAcces);
    AdminResponse updateAdmin(Long id, AdminUpdateRequest request);
    void deleteAdmin(Long id);
}
