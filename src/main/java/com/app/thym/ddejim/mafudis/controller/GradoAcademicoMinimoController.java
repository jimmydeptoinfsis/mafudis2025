package com.app.thym.ddejim.mafudis.controller;

import com.app.thym.ddejim.mafudis.Service.CargoService;
import com.app.thym.ddejim.mafudis.Service.GradoAcademicoMinimoService;
import com.app.thym.ddejim.mafudis.dto.Breadcrumb;
import com.app.thym.ddejim.mafudis.model.Cargo;
import com.app.thym.ddejim.mafudis.model.GradoAcademicoMinimo;
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
@RequestMapping("/grado-academico-minimo")
public class GradoAcademicoMinimoController {

    @Autowired
    private GradoAcademicoMinimoService gradoAcademicoMinimoService;

    @Autowired
    private CargoService cargoService;
    // --- INICIO: MÉTODO AUXILIAR PARA BREADCRUMBS ---
    private void addGradoAcademicoBreadcrumbs(Long cargoId, String currentPageLabel, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));

        List<Breadcrumb> breadcrumbs = new ArrayList<>();
        breadcrumbs.add(new Breadcrumb("Inicio", "/dashboard")); // Asumiendo que esta es la URL de la lista de cargos
        breadcrumbs.add(new Breadcrumb("Organigrama de Cargos", "/organigrama12")); // La página actual no necesita URL
        breadcrumbs.add(new Breadcrumb("Detalle del cargo", "/cargos/adm/details/" + cargoId));

        if (currentPageLabel != null && !currentPageLabel.isEmpty()) {
            breadcrumbs.add(new Breadcrumb("Grado Académico Mínimo", "/grado-academico-minimo/adm/cargo/" + cargoId));
            breadcrumbs.add(new Breadcrumb(currentPageLabel, null));
        } else {
            breadcrumbs.add(new Breadcrumb("Grado Académico Mínimo", null));
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
    public String listGradoAcademicoMinimoPorCargo(@PathVariable Long cargoId, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));
        List<GradoAcademicoMinimo> gradosAcademicos = gradoAcademicoMinimoService.findByCargoId(cargoId);

        model.addAttribute("cargo", cargo);
        model.addAttribute("gradosAcademicos", gradosAcademicos);
        addGradoAcademicoBreadcrumbs(cargoId, null, model);
        return "grado_academico_minimo/list";
    }

    @GetMapping("/cargo/{cargoId}/new")
    public String showNewGradoAcademicoMinimoForm(@PathVariable Long cargoId, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));
        GradoAcademicoMinimo gradoAcademicoMinimo = new GradoAcademicoMinimo();
        gradoAcademicoMinimo.setCargo(cargo);
        model.addAttribute("cargo", cargo);
        model.addAttribute("gradoAcademicoMinimo", gradoAcademicoMinimo);
        model.addAttribute("formAction", "/grado-academico-minimo/cargo/" + cargoId);

        return "grado_academico_minimo/form";
    }

    @PostMapping("/cargo/{cargoId}")
    public String saveGradoAcademicoMinimo(@PathVariable Long cargoId,
                                           @Valid @ModelAttribute("gradoAcademicoMinimo") GradoAcademicoMinimo gradoAcademicoMinimo,
                                           BindingResult result, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));
        gradoAcademicoMinimo.setCargo(cargo);

        if (result.hasErrors()) {
            model.addAttribute("cargo", cargo);
            return "grado_academico_minimo/form";
        }

        gradoAcademicoMinimoService.save(gradoAcademicoMinimo);
        return "redirect:/grado-academico-minimo/cargo/" + cargoId;
    }

    @GetMapping("/cargo/{cargoId}/edit/{id}")
    public String showEditGradoAcademicoMinimoForm(@PathVariable Long cargoId, @PathVariable Long id, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));
        GradoAcademicoMinimo gradoAcademicoMinimo = gradoAcademicoMinimoService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Grado académico no encontrado: " + id));
        model.addAttribute("cargo", cargo);
        model.addAttribute("gradoAcademicoMinimo", gradoAcademicoMinimo);
        model.addAttribute("formAction", "/grado-academico-minimo/cargo/" + cargoId + "/edit/" + id);
        return "grado_academico_minimo/form";
    }

    @PostMapping("/cargo/{cargoId}/edit/{id}")
    public String updateGradoAcademicoMinimo(@PathVariable Long cargoId, @PathVariable Long id,
                                             @Valid @ModelAttribute("gradoAcademicoMinimo") GradoAcademicoMinimo gradoAcademicoMinimo,
                                             BindingResult result, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));
        gradoAcademicoMinimo.setCargo(cargo);
        gradoAcademicoMinimo.setId(id);

        if (result.hasErrors()) {
            model.addAttribute("cargo", cargo);
            return "grado_academico_minimo/form";
        }

        gradoAcademicoMinimoService.save(gradoAcademicoMinimo);
        return "redirect:/grado-academico-minimo/cargo/" + cargoId;
    }

    @GetMapping("/cargo/{cargoId}/delete/{id}")
    public String deleteGradoAcademicoMinimo(@PathVariable Long cargoId, @PathVariable Long id) {
        gradoAcademicoMinimoService.deleteById(id);
        return "redirect:/grado-academico-minimo/cargo/" + cargoId;
    }

    @PostMapping("/cargo/{cargoId}/reorder")
    @ResponseBody
    public ResponseEntity<?> reorderGradoAcademicoMinimo(@PathVariable Long cargoId, @RequestBody List<Long> orderedIds) {
        try {
            gradoAcademicoMinimoService.updateOrder(cargoId, orderedIds);
            return ResponseEntity.ok("Orden actualizado correctamente.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar el orden: " + e.getMessage());
        }
    }

    @GetMapping("/adm/cargo/{cargoId}")
    public String listGradoAcademicoMinimoPorCargoadm(@PathVariable Long cargoId, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));
        List<GradoAcademicoMinimo> gradosAcademicos = gradoAcademicoMinimoService.findByCargoId(cargoId);

        model.addAttribute("cargo", cargo);
        model.addAttribute("gradosAcademicos", gradosAcademicos);
        addGradoAcademicoBreadcrumbs(cargoId, null, model);
        return "adm-cargo-edit-grado-academico-list";
    }

    @GetMapping("/adm/cargo/{cargoId}/new")
    public String showNewGradoAcademicoMinimoFormadm(@PathVariable Long cargoId, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));
        GradoAcademicoMinimo gradoAcademicoMinimo = new GradoAcademicoMinimo();
        gradoAcademicoMinimo.setCargo(cargo);
        model.addAttribute("cargo", cargo);
        model.addAttribute("gradoAcademicoMinimo", gradoAcademicoMinimo);
        model.addAttribute("formAction", "/grado-academico-minimo/adm/cargo/" + cargoId);
        addGradoAcademicoBreadcrumbs(cargoId, "Nuevo", model);
        return "adm-cargo-edit-grado-academico-form";
    }

    @PostMapping("/adm/cargo/{cargoId}")
    public String saveGradoAcademicoMinimoadm(@PathVariable Long cargoId,
                                           @Valid @ModelAttribute("gradoAcademicoMinimo") GradoAcademicoMinimo gradoAcademicoMinimo,
                                           BindingResult result, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));
        gradoAcademicoMinimo.setCargo(cargo);

        if (result.hasErrors()) {
            model.addAttribute("cargo", cargo);
            addGradoAcademicoBreadcrumbs(cargoId, gradoAcademicoMinimo.getId() == null ? "Nuevo" : "Editar", model);
            return "adm-cargo-edit-grado-academico-form";
        }

        gradoAcademicoMinimoService.save(gradoAcademicoMinimo);
        return "redirect:/grado-academico-minimo/adm/cargo/" + cargoId;
    }

    @GetMapping("/adm/cargo/{cargoId}/edit/{id}")
    public String showEditGradoAcademicoMinimoFormadm(@PathVariable Long cargoId, @PathVariable Long id, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));
        GradoAcademicoMinimo gradoAcademicoMinimo = gradoAcademicoMinimoService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Grado académico no encontrado: " + id));
        model.addAttribute("cargo", cargo);
        model.addAttribute("gradoAcademicoMinimo", gradoAcademicoMinimo);
        model.addAttribute("formAction", "/grado-academico-minimo/adm/cargo/" + cargoId + "/edit/" + id);
        addGradoAcademicoBreadcrumbs(cargoId, "Editar", model);
        return "adm-cargo-edit-grado-academico-form";
    }

    @PostMapping("/adm/cargo/{cargoId}/edit/{id}")
    public String updateGradoAcademicoMinimoadm(@PathVariable Long cargoId, @PathVariable Long id,
                                                @Valid @ModelAttribute("gradoAcademicoMinimo") GradoAcademicoMinimo gradoAcademicoMinimo,
                                                BindingResult result, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));

        GradoAcademicoMinimo existingGrado = gradoAcademicoMinimoService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Grado académico no encontrado: " + id));

        // IMPORTANTE: Preservar el orderIndex original
        Integer originalOrderIndex = existingGrado.getOrderIndex();

        existingGrado.setDescripcion(gradoAcademicoMinimo.getDescripcion());
        existingGrado.setCargo(cargo);
        existingGrado.setOrderIndex(originalOrderIndex); // ← MANTENER EL ORDEN ORIGINAL

        if (result.hasErrors()) {
            model.addAttribute("cargo", cargo);
            addGradoAcademicoBreadcrumbs(cargoId, "Editar", model);
            return "adm-cargo-edit-grado-academico-form";
        }

        gradoAcademicoMinimoService.save(existingGrado);
        return "redirect:/grado-academico-minimo/adm/cargo/" + cargoId;
    }

    @GetMapping("/adm/cargo/{cargoId}/delete/{id}")
    public String deleteGradoAcademicoMinimoadm(@PathVariable Long cargoId, @PathVariable Long id) {
        gradoAcademicoMinimoService.deleteById(id);
        return "redirect:/grado-academico-minimo/adm/cargo/" + cargoId;
    }

    @PostMapping("/adm/cargo/{cargoId}/reorder")
    @ResponseBody
    public ResponseEntity<?> reorderGradoAcademicoMinimoadm(@PathVariable Long cargoId, @RequestBody List<Long> orderedIds) {
        try {
            gradoAcademicoMinimoService.updateOrder(cargoId, orderedIds);
            return ResponseEntity.ok("Orden actualizado correctamente.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar el orden: " + e.getMessage());
        }
    }
}