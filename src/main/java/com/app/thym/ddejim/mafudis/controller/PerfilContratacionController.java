package com.app.thym.ddejim.mafudis.controller;

import com.app.thym.ddejim.mafudis.Service.CargoService;
import com.app.thym.ddejim.mafudis.Service.PerfilContratacionService;
import com.app.thym.ddejim.mafudis.dto.Breadcrumb;
import com.app.thym.ddejim.mafudis.model.Cargo;
import com.app.thym.ddejim.mafudis.model.PerfilContratacion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/perfil-contratacion")
public class PerfilContratacionController {

    @Autowired
    private PerfilContratacionService perfilContratacionService;

    @Autowired
    private CargoService cargoService;

    // --- INICIO: MÉTODO AUXILIAR PARA BREADCRUMBS ---
    private void addPerfilContratacionBreadcrumbs(Long cargoId, String currentPageLabel, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));

        List<Breadcrumb> breadcrumbs = new ArrayList<>();
        breadcrumbs.add(new Breadcrumb("Inicio", "/dashboard")); // Asumiendo que esta es la URL de la lista de cargos
        breadcrumbs.add(new Breadcrumb("Organigrama de Cargos", "/organigrama12")); // La página actual no necesita URL
        breadcrumbs.add(new Breadcrumb("Detalle del cargo", "/cargos/adm/details/" + cargoId));

        if (currentPageLabel != null && !currentPageLabel.isEmpty()) {
            breadcrumbs.add(new Breadcrumb("Perfil de Contratación", "/perfil-contratacion/adm/cargo/" + cargoId));
            breadcrumbs.add(new Breadcrumb(currentPageLabel, null));
        } else {
            breadcrumbs.add(new Breadcrumb("Perfil de Contratación", null));
        }
        model.addAttribute("breadcrumbs", breadcrumbs);

        // Lógica para el botón "Volver"
        String backUrl = "/cargos/adm"; // URL por defecto
        if (breadcrumbs.size() > 1) {
            backUrl = breadcrumbs.get(breadcrumbs.size() - 2).getUrl();
        }
        model.addAttribute("backUrl", backUrl);
    }
    // --- FIN: MÉTODO AUXILIAR PARA BREADCRUMBS ---



    @GetMapping("/cargo/{cargoId}")
    public String listPerfilesContratacionPorCargo(@PathVariable Long cargoId, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));
        List<PerfilContratacion> perfilesContratacion = perfilContratacionService.findByCargoId(cargoId);

        model.addAttribute("cargo", cargo);
        model.addAttribute("perfilesContratacion", perfilesContratacion);

        return "perfil_contratacion/list";
    }

    @GetMapping("/cargo/{cargoId}/new")
    public String showNewPerfilContratacionForm(@PathVariable Long cargoId, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));
        PerfilContratacion perfilContratacion = new PerfilContratacion();
        perfilContratacion.setCargo(cargo);
        model.addAttribute("cargo", cargo);
        model.addAttribute("perfilContratacion", perfilContratacion);
        model.addAttribute("formAction", "/perfil-contratacion/cargo/" + cargoId);

        return "perfil_contratacion/form";
    }

    @PostMapping("/cargo/{cargoId}")
    public String savePerfilContratacion(@PathVariable Long cargoId,
                                         @Valid @ModelAttribute("perfilContratacion") PerfilContratacion perfilContratacion,
                                         BindingResult result, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));
        perfilContratacion.setCargo(cargo);

        if (result.hasErrors()) {
            model.addAttribute("cargo", cargo);

            return "perfil_contratacion/form";
        }

        perfilContratacionService.save(perfilContratacion);
        return "redirect:/perfil-contratacion/cargo/" + cargoId;
    }

    @GetMapping("/cargo/{cargoId}/edit/{id}")
    public String showEditPerfilContratacionForm(@PathVariable Long cargoId, @PathVariable Long id, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));
        PerfilContratacion perfilContratacion = perfilContratacionService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Perfil no encontrado: " + id));
        model.addAttribute("cargo", cargo);
        model.addAttribute("perfilContratacion", perfilContratacion);
        model.addAttribute("formAction", "/perfil-contratacion/cargo/" + cargoId + "/edit/" + id);

        return "perfil_contratacion/form";
    }

    @PostMapping("/cargo/{cargoId}/edit/{id}")
    public String updatePerfilContratacion(@PathVariable Long cargoId, @PathVariable Long id,
                                           @Valid @ModelAttribute("perfilContratacion") PerfilContratacion perfilContratacion,
                                           BindingResult result, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));
        perfilContratacion.setCargo(cargo);
        perfilContratacion.setId(id);

        if (result.hasErrors()) {
            model.addAttribute("cargo", cargo);
            return "perfil_contratacion/form";
        }

        perfilContratacionService.save(perfilContratacion);
        return "redirect:/perfil-contratacion/cargo/" + cargoId;
    }

    @GetMapping("/cargo/{cargoId}/delete/{id}")
    public String deletePerfilContratacion(@PathVariable Long cargoId, @PathVariable Long id) {
        perfilContratacionService.deleteById(id);
        return "redirect:/perfil-contratacion/cargo/" + cargoId;
    }

    @PostMapping("/cargo/{cargoId}/reorder")
    @ResponseBody
    public ResponseEntity<?> reorderPerfilesContratacion(@PathVariable Long cargoId, @RequestBody List<Long> orderedIds) {
        try {
            perfilContratacionService.updateOrder(cargoId, orderedIds);
            return ResponseEntity.ok("Orden actualizado correctamente.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar el orden: " + e.getMessage());
        }
    }


    @GetMapping("/adm/cargo/{cargoId}")
    public String listPerfilesContratacionPorCargoadm(@PathVariable Long cargoId, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));
        List<PerfilContratacion> perfilesContratacion = perfilContratacionService.findByCargoId(cargoId);

        model.addAttribute("cargo", cargo);
        model.addAttribute("perfilesContratacion", perfilesContratacion);
        addPerfilContratacionBreadcrumbs(cargoId, null, model);
        return "adm-cargo-edit-perfil-contratacion-list";
    }

    @GetMapping("/adm/cargo/{cargoId}/new")
    public String showNewPerfilContratacionFormadm(@PathVariable Long cargoId, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));
        PerfilContratacion perfilContratacion = new PerfilContratacion();
        perfilContratacion.setCargo(cargo);
        model.addAttribute("cargo", cargo);
        model.addAttribute("perfilContratacion", perfilContratacion);
        model.addAttribute("formAction", "/perfil-contratacion/adm/cargo/" + cargoId);
        addPerfilContratacionBreadcrumbs(cargoId, "Nuevo", model);
        return "adm-cargo-edit-perfil-contratacion-form";
    }

    @PostMapping("/adm/cargo/{cargoId}")
    public String savePerfilContratacionadm(@PathVariable Long cargoId,
                                         @Valid @ModelAttribute("perfilContratacion") PerfilContratacion perfilContratacion,
                                         BindingResult result, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));
        perfilContratacion.setCargo(cargo);

        if (result.hasErrors()) {
            model.addAttribute("cargo", cargo);
            addPerfilContratacionBreadcrumbs(cargoId, perfilContratacion.getId() == null ? "Nuevo" : "Editar", model);
            return "adm-cargo-edit-perfil-contratacion-form";
        }

        perfilContratacionService.save(perfilContratacion);
        return "redirect:/perfil-contratacion/adm/cargo/" + cargoId;
    }

    @GetMapping("/adm/cargo/{cargoId}/edit/{id}")
    public String showEditPerfilContratacionFormadm(@PathVariable Long cargoId, @PathVariable Long id, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));
        PerfilContratacion perfilContratacion = perfilContratacionService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Perfil no encontrado: " + id));
        model.addAttribute("cargo", cargo);
        model.addAttribute("perfilContratacion", perfilContratacion);
        model.addAttribute("formAction", "/perfil-contratacion/adm/cargo/" + cargoId + "/edit/" + id);
        addPerfilContratacionBreadcrumbs(cargoId, "Editar", model);
        return "adm-cargo-edit-perfil-contratacion-form";
    }

    @PostMapping("/adm/cargo/{cargoId}/edit/{id}")
    public String updatePerfilContratacionadm(@PathVariable Long cargoId, @PathVariable Long id,
                                              @Valid @ModelAttribute("perfilContratacion") PerfilContratacion perfilContratacion,
                                              BindingResult result, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));

        PerfilContratacion existingPerfil = perfilContratacionService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Perfil no encontrado: " + id));

        // IMPORTANTE: Preservar el orderIndex original
        Integer originalOrderIndex = existingPerfil.getOrderIndex();

        existingPerfil.setDescripcion(perfilContratacion.getDescripcion());
        existingPerfil.setCargo(cargo);
        existingPerfil.setOrderIndex(originalOrderIndex); // ← MANTENER EL ORDEN ORIGINAL

        if (result.hasErrors()) {
            model.addAttribute("cargo", cargo);
            addPerfilContratacionBreadcrumbs(cargoId, "Editar", model);
            return "adm-cargo-edit-perfil-contratacion-form";
        }

        perfilContratacionService.save(existingPerfil);
        return "redirect:/perfil-contratacion/adm/cargo/" + cargoId;
    }

    @GetMapping("/adm/cargo/{cargoId}/delete/{id}")
    public String deletePerfilContratacionadm(@PathVariable Long cargoId, @PathVariable Long id) {
        perfilContratacionService.deleteById(id);
        return "redirect:/perfil-contratacion/adm/cargo/" + cargoId;
    }

    @PostMapping("/adm/cargo/{cargoId}/reorder")
    @ResponseBody
    public ResponseEntity<?> reorderPerfilesContratacionadm(@PathVariable Long cargoId, @RequestBody List<Long> orderedIds) {
        try {
            perfilContratacionService.updateOrder(cargoId, orderedIds);
            return ResponseEntity.ok("Orden actualizado correctamente.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar el orden: " + e.getMessage());
        }
    }



}
