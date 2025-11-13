package com.app.thym.ddejim.mafudis.controller;

import com.app.thym.ddejim.mafudis.Service.UsuarioService;
import com.app.thym.ddejim.mafudis.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
public class PasswordResetController {

    @Autowired
    private UsuarioService userService; // Necesitarás inyectar tu servicio de usuario

    @Autowired
    private EmailService emailService;

    // Muestra el formulario para pedir el reseteo
    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot-password";
    }

    // Procesa la solicitud de reseteo
    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String userEmail, RedirectAttributes redirectAttributes) {
        try {
            String token = UUID.randomUUID().toString();
            userService.createPasswordResetTokenForUser(userEmail, token);
            emailService.sendPasswordResetEmail(userEmail, token);
            redirectAttributes.addFlashAttribute("message", "Si tu correo está registrado, recibirás un enlace para restablecer tu contraseña.");
        } catch (Exception ex) {
            // Incluso si falla, muestra un mensaje genérico para no revelar si un email existe o no.
            redirectAttributes.addFlashAttribute("message", "Si tu correo está registrado, recibirás un enlace para restablecer tu contraseña.");
        }
        return "redirect:/forgot-password";
    }

    // Muestra el formulario para poner la nueva contraseña
    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        if (userService.validatePasswordResetToken(token)) {
            model.addAttribute("token", token);
            return "reset-password";
        } else {
            model.addAttribute("error", "El enlace es inválido o ha expirado.");
            return "redirect:/login?invalidToken";
        }
    }

    // Procesa el cambio de contraseña
    @PostMapping("/reset-password")
    public String handlePasswordReset(@RequestParam("token") String token,
                                      @RequestParam("password") String password,
                                      @RequestParam("confirmPassword") String confirmPassword,
                                      RedirectAttributes redirectAttributes) {

        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Las contraseñas no coinciden.");
            return "redirect:/reset-password?token=" + token;
        }

        if (userService.validatePasswordResetToken(token)) {
            userService.updatePassword(token, password);
            redirectAttributes.addFlashAttribute("message", "Tu contraseña ha sido actualizada exitosamente.");
            return "redirect:/login";
        } else {
            redirectAttributes.addFlashAttribute("error", "El enlace es inválido o ha expirado.");
            return "redirect:/login";
        }
    }
}
