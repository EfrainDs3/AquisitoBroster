package aquisito_broster_api.service.impl;

import aquisito_broster_api.dto.request.UsuarioRequest;
import aquisito_broster_api.dto.response.UsuarioResponse;
import aquisito_broster_api.entity.Usuario;
import aquisito_broster_api.exception.BadRequestException;
import aquisito_broster_api.exception.ResourceNotFoundException;
import aquisito_broster_api.repository.UsuarioRepository;
import aquisito_broster_api.service.UsuarioService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<UsuarioResponse> listar() {
        return usuarioRepository.findAll().stream()
            .map(usuario -> new UsuarioResponse(usuario.getId(), usuario.getUsername(), usuario.getNombre(), usuario.getRol().name()))
            .toList();
    }

    @Override
    public UsuarioResponse crear(UsuarioRequest request) {
        usuarioRepository.findByUsername(request.getUsername()).ifPresent(user -> {
            throw new BadRequestException("Ya existe un usuario con ese username");
        });

        Usuario usuario = new Usuario();
        usuario.setUsername(request.getUsername());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setNombre(request.getNombre());
        usuario.setRol(request.getRol());
        usuario.setActivo(request.isActivo());
        Usuario saved = usuarioRepository.save(usuario);
        return new UsuarioResponse(saved.getId(), saved.getUsername(), saved.getNombre(), saved.getRol().name());
    }

    @Override
    public UsuarioResponse cambiarActivo(Long id, boolean activo) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        usuario.setActivo(activo);
        Usuario saved = usuarioRepository.save(usuario);
        return new UsuarioResponse(saved.getId(), saved.getUsername(), saved.getNombre(), saved.getRol().name());
    }
}