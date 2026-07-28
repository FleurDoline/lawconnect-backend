package org.arited.lawconnect.core.services;

import org.arited.lawconnect.core.dtos.Request.ChangePasswordRequest;

public interface UserService {
    void changePassword(Long userId, ChangePasswordRequest request);
}