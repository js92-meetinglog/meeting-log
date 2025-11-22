package org.meetinglog.auth.service;

import org.meetinglog.auth.dto.AuthRequestDTO.LoginRequest;
import org.meetinglog.auth.dto.AuthResponseDTO.TokenResponse;

public interface AuthService {

  TokenResponse authenticateNormalUser(LoginRequest loginRequest);
}
