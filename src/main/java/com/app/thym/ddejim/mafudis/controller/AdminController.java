package com.app.thym.ddejim.mafudis.controller;

import com.app.thym.ddejim.mafudis.Service.UsuarioService;
import com.app.thym.ddejim.mafudis.model.Cargo;
import com.app.thym.ddejim.mafudis.model.Usuario;
import com.app.thym.ddejim.mafudis.repository.CargoRepository;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Controller
public class AdminController {
    @Autowired
    private CargoRepository cargoRepository;

    @Autowired
    private UsuarioService usuarioService;

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    @GetMapping("/dashboard")
    public String showDashboard(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            Usuario currentUser = usuarioService.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado en la base de datos"));
            // 8. Añadir el objeto Usuario completo al modelo
            model.addAttribute("currentUser", currentUser);
        }
        model.addAttribute("pageTitle", "Dashboard");
        model.addAttribute("currentPage", "dashboard");
        List<Cargo> cargos = cargoRepository.findAll();
        model.addAttribute("cargos", cargos);
        return "dashboard";
    }

    @GetMapping("/dashboardp")
    public String showDashboardp(Model model) {
        try {
            model.addAttribute("pageTitle", "Organigrama");
            List<Cargo> cargos = cargoRepository.findAll();
            logger.info("Cargos encontrados: {}", cargos.size());
            cargos.forEach(cargo -> logger.debug("Cargo: {}", cargo));
            model.addAttribute("cargos", cargos);
            return "dashboardp";
        } catch (Exception e) {
            logger.error("Error en showDashboardp: ", e);
            model.addAttribute("error", "Error al cargar los datos del organigrama");
            return "error";
        }
    }
}