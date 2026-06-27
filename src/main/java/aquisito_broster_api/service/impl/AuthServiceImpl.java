package aquisito_broster_api.service.impl;

import aquisito_broster_api.dto.auth.AuthResponse;
import aquisito_broster_api.dto.auth.LoginRequest;
import aquisito_broster_api.entity.Usuario;
import aquisito_broster_api.exception.BadRequestException;
import aquisito_broster_api.exception.ResourceNotFoundException;
import aquisito_broster_api.repository.UsuarioRepository;
import aquisito_broster_api.security.JwtService;
import aquisito_broster_api.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthServiceImpl(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (!usuario.isActivo()) {
            throw new BadRequestException("Usuario inactivo");
        }

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new BadRequestException("Credenciales inválidas");
        }

        return new AuthResponse(
            jwtService.generateToken(usuario.getUsername(), usuario.getNombre(), usuario.getRol()),
            usuario.getUsername(),
            usuario.getNombre(),
            usuario.getRol().name()
        );
    }
}