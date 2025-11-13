package com.app.thym.ddejim.mafudis.controller;

import com.app.thym.ddejim.mafudis.Service.UsuarioService;
import com.app.thym.ddejim.mafudis.model.Role;
import com.app.thym.ddejim.mafudis.model.Usuario;
import com.app.thym.ddejim.mafudis.repository.RoleRepository;
import jakarta.persistence.Column;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private RoleRepository roleRepository;

    @GetMapping("/list")
    public String listUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioService.findAll());
        return "adm-edit-usuario";
    }

    /**
     * Muestra el formulario para crear un nuevo usuario.
     */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        List<Role> listRoles = roleRepository.findAll();
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("listRoles", listRoles);
        return "adm-edit-usuario-form";
    }

    /**
     * Guarda un usuario nuevo o actualiza uno existente.
     */
    @PostMapping("/save")
    public String saveUsuario(@ModelAttribute("usuario") Usuario usuario, Model model, RedirectAttributes redirectAttributes) {
        try {
            // Validación para Username
            if (usuario.getId() == null && usuarioService.existsByUsername(usuario.getUsername())) {
                throw new UsuarioService.UsernameAlreadyExistsException("El nombre de usuario '" + usuario.getUsername() + "' ya está en uso.");
            }
            if (usuario.getId() != null && usuarioService.existsByUsernameAndNotId(usuario.getUsername(), usuario.getId())) {
                throw new UsuarioService.UsernameAlreadyExistsException("El nombre de usuario '" + usuario.getUsername() + "' ya está en uso por otro usuario.");
            }

            // Validación para Email
            if (usuario.getId() == null && usuarioService.existsByEmail(usuario.getEmail())) {
                throw new UsuarioService.EmailAlreadyExistsException("El email '" + usuario.getEmail() + "' ya está registrado.");
            }
            if (usuario.getId() != null && usuarioService.existsByEmailAndNotId(usuario.getEmail(), usuario.getId())) {
                throw new UsuarioService.EmailAlreadyExistsException("El email '" + usuario.getEmail() + "' ya está registrado por otro usuario.");
            }

            // La validación de contraseña y el cifrado ahora ocurren dentro de save()
            usuarioService.save(usuario);

            redirectAttributes.addFlashAttribute("successMessage", "Usuario guardado exitosamente.");
            return "redirect:/usuarios/list";

        } catch (UsuarioService.PasswordValidationException | UsuarioService.EmailAlreadyExistsException | UsuarioService.UsernameAlreadyExistsException e) {
            // ... (el bloque catch sigue igual)
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("usuario", usuario);

            model.addAttribute("listRoles", roleRepository.findAll());
            return "form-usuario";

        } catch (Exception e) {
            // ... (el bloque catch sigue igual)
            model.addAttribute("errorMessage", "Error al guardar el usuario: " + e.getMessage());
            model.addAttribute("usuario", usuario);
            model.addAttribute("listRoles", roleRepository.findAll());
            return "form-usuario";
        }
    }

    /**
     * Muestra el formulario para editar un usuario existente.
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Usuario usuario = usuarioService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID de Usuario no válido:" + id));
        List<Role> listRoles = roleRepository.findAll();

        model.addAttribute("usuario", usuario);
        model.addAttribute("listRoles", listRoles);
        return "adm-edit-usuario-form";
    }

    @GetMapping("/delete/{id}")
    public String deleteUsuario(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Usuario eliminado exitosamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar el usuario: " + e.getMessage());
        }
        return "redirect:/usuarios/list";
    }
}