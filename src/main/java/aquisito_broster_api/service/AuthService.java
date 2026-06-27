package aquisito_broster_api.service;

import aquisito_broster_api.dto.auth.AuthResponse;
import aquisito_broster_api.dto.auth.LoginRequest;

public interface AuthService {

    AuthResponse login(LoginRequest request);
}