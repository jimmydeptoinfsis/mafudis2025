package com.app.thym.ddejim.mafudis.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
        // Obtenemos el código de estado del error
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());

            // 1. Verificamos si el error es un 404 (Not Found)
            if (statusCode == HttpStatus.NOT_FOUND.value()) {

                // 2. Verificamos si el usuario está autenticado
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                // Si hay una sesión activa y no es un usuario anónimo...
                if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName())) {
                    // 3. ...le mostramos nuestra página de error personalizada.
                    return "custom-404"; // Carga el archivo custom-404.html
                }
            }
        }

        // Para cualquier otro tipo de error (500, 403, etc.) o si el usuario no está logueado,
        // puedes mostrar una página de error genérica.
        // Spring Boot buscará un archivo llamado "error.html" por defecto.
        return "generic-error";
    }
}