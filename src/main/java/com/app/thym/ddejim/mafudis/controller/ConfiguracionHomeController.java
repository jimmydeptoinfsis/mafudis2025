package com.app.thym.ddejim.mafudis.controller;


import com.app.thym.ddejim.mafudis.model.ConfiguracionHome;
import com.app.thym.ddejim.mafudis.repository.ConfiguracionHomeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/configuracion")
public class ConfiguracionHomeController {

    @Autowired
    private ConfiguracionHomeRepository repo;

    @GetMapping
    public String editar(Model model) {
        ConfiguracionHome config = repo.findById(1L).orElse(new ConfiguracionHome());
        model.addAttribute("config", config);
        return "admin/configuracion_form";
    }

    @PostMapping
    public String guardar(@ModelAttribute ConfiguracionHome nuevaConfig) {
        ConfiguracionHome existente = repo.findById(1L).orElse(new ConfiguracionHome());

        existente.setTituloPrincipal(nuevaConfig.getTituloPrincipal());
        existente.setSubtitulo(nuevaConfig.getSubtitulo());
        existente.setDescripcionIntro(nuevaConfig.getDescripcionIntro());
        existente.setMision(nuevaConfig.getMision());
        existente.setVision(nuevaConfig.getVision());

        repo.save(existente);

        return "redirect:/admin/configuracion?success=true";
    }

}