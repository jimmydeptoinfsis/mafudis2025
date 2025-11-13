package com.app.thym.ddejim.mafudis.config;

import com.app.thym.ddejim.mafudis.Service.UsuarioService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class CustomLoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Autowired
    @Lazy
    private UsuarioService usuarioService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        String username = request.getParameter("username");
        String errorMessage = "Usuario o contraseña incorrectos.";

        // Verificar si la excepción es por cuenta bloqueada
        if (exception instanceof LockedException) {
            errorMessage = "Tu cuenta ha sido bloqueada por 1 hora debido a múltiples intentos fallidos.";
        } else if (username != null && !username.isEmpty()) {
            // Solo incrementar intentos fallidos si NO es una cuenta bloqueada
            // y si el usuario existe
            try {
                if (!usuarioService.isAccountLocked(username)) {
                    usuarioService.handleFailedLoginAttempt(username);

                    // Verificar si después de este intento la cuenta quedó bloqueada
                    if (usuarioService.isAccountLocked(username)) {
                        errorMessage = "Tu cuenta ha sido bloqueada por 1 hora debido a múltiples intentos fallidos.";
                    }
                } else {
                    errorMessage = "Tu cuenta está bloqueada temporalmente. Inténtalo más tarde.";
                }
            } catch (Exception e) {
                // Si el usuario no existe, mostrar el mensaje genérico
                errorMessage = "Usuario o contraseña incorrectos.";
            }
        }

        // Codificar el mensaje y redirigir
        String encodedMessage = URLEncoder.encode(errorMessage, StandardCharsets.UTF_8);
        setDefaultFailureUrl("/login?error=true&message=" + encodedMessage);
        super.onAuthenticationFailure(request, response, exception);
    }
}