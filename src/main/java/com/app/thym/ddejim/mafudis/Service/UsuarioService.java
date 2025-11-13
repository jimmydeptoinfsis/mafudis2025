package com.app.thym.ddejim.mafudis.Service;

import com.app.thym.ddejim.mafudis.model.Role;
import com.app.thym.ddejim.mafudis.model.Usuario;
import com.app.thym.ddejim.mafudis.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsuarioService implements UserDetailsService {
    public static final int MAX_FAILED_ATTEMPTS = 3;
    private static final long LOCK_TIME_DURATION = 1; // Duración del bloqueo en horas
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("No se encontró el usuario: " + username));

        // Verificar si la cuenta está bloqueada y si ya pasó el tiempo de bloqueo
        if (usuario.getLockTime() != null) {
            if (usuario.getLockTime().isAfter(LocalDateTime.now())) {
                // Cuenta todavía bloqueada
                throw new LockedException("La cuenta está bloqueada temporalmente. Inténtalo más tarde.");
            } else {
                // El tiempo de bloqueo ya pasó, desbloquear automáticamente
                unlockUser(usuario);
            }
        }

        return new User(usuario.getUsername(), usuario.getPassword(), getAuthorities(usuario.getRoles()));
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Collection<Role> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }

    // ================= MÉTODOS CRUD BÁSICOS ===================

    @Transactional(readOnly = true)
    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> findByUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    @Transactional
    public void deleteById(Long id) {
        usuarioRepository.deleteById(id);
    }

    // ================= LÓGICA DE GUARDADO Y ACTUALIZACIÓN ===================

    @Transactional
    public Usuario save(Usuario usuario) throws PasswordValidationException {
        if (usuario.getId() == null) {
            if (usuario.getPassword() == null || usuario.getPassword().isEmpty()) {
                throw new PasswordValidationException("La contraseña es obligatoria para nuevos usuarios.");
            }
            if (!usuario.getPassword().equals(usuario.getConfirmPassword())) {
                throw new PasswordValidationException("Las contraseñas no coinciden.");
            }
        } else {
            if (usuario.getPassword() != null && !usuario.getPassword().isEmpty()) {
                if (!usuario.getPassword().equals(usuario.getConfirmPassword())) {
                    throw new PasswordValidationException("Las contraseñas no coinciden.");
                }
            }
        }

        if (usuario.getPassword() != null && !usuario.getPassword().isEmpty()) {
            String encodedPassword = passwordEncoder.encode(usuario.getPassword());
            usuario.setPassword(encodedPassword);
        } else if (usuario.getId() != null) {
            usuarioRepository.findById(usuario.getId()).ifPresent(existingUser -> {
                usuario.setPassword(existingUser.getPassword());
            });
        }

        return usuarioRepository.save(usuario);
    }

    // ================= VALIDACIONES DE UNICIDAD ===================

    public boolean existsByUsername(String username) {
        return usuarioRepository.findByUsername(username).isPresent();
    }

    public boolean existsByUsernameAndNotId(String username, Long id) {
        return usuarioRepository.findByUsernameAndNotId(username, id).isPresent();
    }

    public boolean existsByEmail(String email) {
        return usuarioRepository.findByEmail(email).isPresent();
    }

    public boolean existsByEmailAndNotId(String email, Long id) {
        return usuarioRepository.findByEmailAndNotId(email, id).isPresent();
    }

    // ================= EXCEPCIONES PERSONALIZADAS ===================

    public static class UsernameAlreadyExistsException extends Exception {
        public UsernameAlreadyExistsException(String message) {
            super(message);
        }
    }

    public static class EmailAlreadyExistsException extends Exception {
        public EmailAlreadyExistsException(String message) {
            super(message);
        }
    }

    public static class PasswordValidationException extends Exception {
        public PasswordValidationException(String message) {
            super(message);
        }
    }

    // ================= RESETEO DE CONTRASEÑA ===================

    @Transactional
    public void createPasswordResetTokenForUser(String email, String token) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findByEmail(email);
        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();
            usuario.setResetPasswordToken(token);
            usuario.setResetPasswordTokenExpiryDate(LocalDateTime.now().plusHours(1));
            usuarioRepository.save(usuario);
        } else {
            System.out.println("Intento de reseteo para email no registrado: " + email);
        }
    }

    public boolean validatePasswordResetToken(String token) {
        return usuarioRepository.findByResetPasswordToken(token)
                .map(usuario -> !usuario.getResetPasswordTokenExpiryDate().isBefore(LocalDateTime.now()))
                .orElse(false);
    }

    @Transactional
    public void updatePassword(String token, String newPassword) {
        usuarioRepository.findByResetPasswordToken(token).ifPresent(usuario -> {
            usuario.setPassword(passwordEncoder.encode(newPassword));
            usuario.setResetPasswordToken(null);
            usuario.setResetPasswordTokenExpiryDate(null);
            usuarioRepository.save(usuario);
        });
    }

    // ================= MANEJO DE INTENTOS FALLIDOS Y BLOQUEO ===================

    @Transactional
    public void handleFailedLoginAttempt(String username) {
        usuarioRepository.findByUsername(username).ifPresent(usuario -> {
            // Verificar si la cuenta ya está desbloqueada por tiempo
            if (usuario.getLockTime() != null && usuario.getLockTime().isBefore(LocalDateTime.now())) {
                unlockUser(usuario);
                usuario.setFailedAttemptCount(1); // Reiniciar contador
            } else {
                usuario.setFailedAttemptCount(usuario.getFailedAttemptCount() + 1);
            }

            if (usuario.getFailedAttemptCount() >= MAX_FAILED_ATTEMPTS) {
                usuario.setLockTime(LocalDateTime.now().plusHours(LOCK_TIME_DURATION));
            }
            usuarioRepository.save(usuario);
        });
    }

    @Transactional
    public void handleSuccessfulLogin(String username) {
        usuarioRepository.findByUsername(username).ifPresent(this::unlockUser);
    }

    private void unlockUser(Usuario usuario) {
        usuario.setLockTime(null);
        usuario.setFailedAttemptCount(0);
        usuarioRepository.save(usuario);
    }

    /**
     * Verifica si un usuario está bloqueado actualmente
     */
    public boolean isAccountLocked(String username) {
        return usuarioRepository.findByUsername(username)
                .map(usuario -> usuario.getLockTime() != null &&
                        usuario.getLockTime().isAfter(LocalDateTime.now()))
                .orElse(false);
    }
}