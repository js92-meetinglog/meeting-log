package org.meetinglog.auth.service;

import jakarta.validation.Valid;
import org.meetinglog.auth.dto.AuthRequestDTO.LoginRequest;
import org.meetinglog.auth.dto.AuthRequestDTO.SignupRequest;
import org.meetinglog.auth.dto.AuthResponseDTO.TokenResponse;
import org.meetinglog.jpa.entity.UserMst;

public interface AuthService {

  TokenResponse authenticateNormalUser(LoginRequest loginRequest);

  UserMst signupNormalUser(@Valid SignupRequest signupRequest);
}
