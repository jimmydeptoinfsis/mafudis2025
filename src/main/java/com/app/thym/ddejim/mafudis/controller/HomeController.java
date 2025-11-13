package com.app.thym.ddejim.mafudis.controller;

import com.app.thym.ddejim.mafudis.model.Cargo;
import com.app.thym.ddejim.mafudis.model.ConfiguracionHome;
import com.app.thym.ddejim.mafudis.model.Unidad;
import com.app.thym.ddejim.mafudis.repository.CargoRepository;
import com.app.thym.ddejim.mafudis.repository.ConfiguracionHomeRepository;
import com.app.thym.ddejim.mafudis.repository.UnidadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@Controller
public class HomeController {

    /*@Autowired
    private ContactoRepository contactoRepository;*/

    @Autowired
    private ConfiguracionHomeRepository configuracionHomeRepository;
    @Autowired
    private ConfiguracionHomeRepository repo;
    @Autowired
    private UnidadRepository unidadRepository;
    @Autowired
    private CargoRepository cargoRepository;
    @GetMapping("/")
    public String landingPage(Model model) {



        ConfiguracionHome config = configuracionHomeRepository.findById(1L).orElse(new ConfiguracionHome());

        model.addAttribute("pageTitle", "MAFUDIS - UMSS");
        model.addAttribute("tituloPrincipal", config.getTituloPrincipal());
        model.addAttribute("subtitulo", config.getSubtitulo());
        model.addAttribute("descripcionIntro", config.getDescripcionIntro());
        model.addAttribute("mision", config.getMision());
        model.addAttribute("vision", config.getVision());
        model.addAttribute("anioActual", LocalDate.now().getYear());

        try {
            List<Unidad> unidades = unidadRepository.findAll();
            model.addAttribute("unidades", unidades);
        } catch (Exception e) {
            // Manejar el error si es necesario, por ahora se pasa una lista vacía
            model.addAttribute("unidades", List.of());
        }
        try {
            List<Cargo> cargos = cargoRepository.findAll();
            model.addAttribute("cargos", cargos);
        } catch (Exception e) {
            model.addAttribute("cargos", List.of());
        }

        return "landingoct";
    }



}