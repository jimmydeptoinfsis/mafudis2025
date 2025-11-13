package com.app.thym.ddejim.mafudis.controller;

import com.app.thym.ddejim.mafudis.Service.CargoService;
import com.app.thym.ddejim.mafudis.Service.HabilidadesDestrezasService;
import com.app.thym.ddejim.mafudis.dto.Breadcrumb;
import com.app.thym.ddejim.mafudis.model.Cargo;
import com.app.thym.ddejim.mafudis.model.HabilidadesDestrezas;
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
@RequestMapping("/habilidades-destrezas")
public class HabilidadesDestrezasController {

    @Autowired
    private HabilidadesDestrezasService habilidadesDestrezasService;

    @Autowired
    private CargoService cargoService;


    // --- INICIO: MÉTODO AUXILIAR PARA BREADCRUMBS ---
    private void addHabilidadesDestrezasBreadcrumbs(Long cargoId, String currentPageLabel, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));

        List<Breadcrumb> breadcrumbs = new ArrayList<>();
        breadcrumbs.add(new Breadcrumb("Inicio", "/dashboard")); // Asumiendo que esta es la URL de la lista de cargos
        breadcrumbs.add(new Breadcrumb("Organigrama de Cargos", "/organigrama12")); // La página actual no necesita URL
        breadcrumbs.add(new Breadcrumb("Detalle del cargo", "/cargos/adm/details/" + cargoId));

        if (currentPageLabel != null && !currentPageLabel.isEmpty()) {
            breadcrumbs.add(new Breadcrumb("Habilidades y Destrezas", "/habilidades-destrezas/adm/cargo/" + cargoId));
            breadcrumbs.add(new Breadcrumb(currentPageLabel, null));
        } else {
            breadcrumbs.add(new Breadcrumb("Habilidades y Destrezas", null));
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
    public String listHabilidadesDestrezasPorCargo(@PathVariable Long cargoId, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));
        List<HabilidadesDestrezas> habilidadesDestrezas = habilidadesDestrezasService.findByCargoId(cargoId);

        model.addAttribute("cargo", cargo);
        model.addAttribute("habilidadesDestrezas", habilidadesDestrezas);
        return "habilidades_destrezas/list";
    }

    @GetMapping("/cargo/{cargoId}/new")
    public String showNewHabilidadesDestrezasForm(@PathVariable Long cargoId, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));
        HabilidadesDestrezas habilidadesDestrezas = new HabilidadesDestrezas();
        habilidadesDestrezas.setCargo(cargo);
        model.addAttribute("cargo", cargo);
        model.addAttribute("habilidadesDestrezas", habilidadesDestrezas);
        model.addAttribute("formAction", "/habilidades-destrezas/cargo/" + cargoId);
        return "habilidades_destrezas/form";
    }

    @PostMapping("/cargo/{cargoId}")
    public String saveHabilidadesDestrezas(@PathVariable Long cargoId,
                                           @Valid @ModelAttribute("habilidadesDestrezas") HabilidadesDestrezas habilidadesDestrezas,
                                           BindingResult result, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));
        habilidadesDestrezas.setCargo(cargo);

        if (result.hasErrors()) {
            model.addAttribute("cargo", cargo);
            return "habilidades_destrezas/form";
        }

        habilidadesDestrezasService.save(habilidadesDestrezas);
        return "redirect:/habilidades-destrezas/cargo/" + cargoId;
    }

    @GetMapping("/cargo/{cargoId}/edit/{id}")
    public String showEditHabilidadesDestrezasForm(@PathVariable Long cargoId, @PathVariable Long id, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));
        HabilidadesDestrezas habilidadesDestrezas = habilidadesDestrezasService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Habilidad no encontrada: " + id));
        model.addAttribute("cargo", cargo);
        model.addAttribute("habilidadesDestrezas", habilidadesDestrezas);
        model.addAttribute("formAction", "/habilidades-destrezas/cargo/" + cargoId + "/edit/" + id);
        return "habilidades_destrezas/form";
    }

    @PostMapping("/cargo/{cargoId}/edit/{id}")
    public String updateHabilidadesDestrezas(@PathVariable Long cargoId, @PathVariable Long id,
                                             @Valid @ModelAttribute("habilidadesDestrezas") HabilidadesDestrezas habilidadesDestrezas,
                                             BindingResult result, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));
        habilidadesDestrezas.setCargo(cargo);
        habilidadesDestrezas.setId(id);

        if (result.hasErrors()) {
            model.addAttribute("cargo", cargo);
            return "habilidades_destrezas/form";
        }

        habilidadesDestrezasService.save(habilidadesDestrezas);
        return "redirect:/habilidades-destrezas/cargo/" + cargoId;
    }

    @GetMapping("/cargo/{cargoId}/delete/{id}")
    public String deleteHabilidadesDestrezas(@PathVariable Long cargoId, @PathVariable Long id) {
        habilidadesDestrezasService.deleteById(id);
        return "redirect:/habilidades-destrezas/cargo/" + cargoId;
    }

    @PostMapping("/cargo/{cargoId}/reorder")
    @ResponseBody
    public ResponseEntity<?> reorderHabilidadesDestrezas(@PathVariable Long cargoId, @RequestBody List<Long> orderedIds) {
        try {
            habilidadesDestrezasService.updateOrder(cargoId, orderedIds);
            return ResponseEntity.ok("Orden actualizado correctamente.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar el orden: " + e.getMessage());
        }
    }

    @GetMapping("/adm/cargo/{cargoId}")
    public String listHabilidadesDestrezasPorCargoadm(@PathVariable Long cargoId, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));
        List<HabilidadesDestrezas> habilidadesDestrezas = habilidadesDestrezasService.findByCargoId(cargoId);

        model.addAttribute("cargo", cargo);
        model.addAttribute("habilidadesDestrezas", habilidadesDestrezas);
        addHabilidadesDestrezasBreadcrumbs(cargoId, null, model);
        return "adm-cargo-edit-habilidades-destrezas-list";
    }

    @GetMapping("/adm/cargo/{cargoId}/new")
    public String showNewHabilidadesDestrezasFormadm(@PathVariable Long cargoId, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));
        HabilidadesDestrezas habilidadesDestrezas = new HabilidadesDestrezas();
        habilidadesDestrezas.setCargo(cargo);
        model.addAttribute("cargo", cargo);
        model.addAttribute("habilidadesDestrezas", habilidadesDestrezas);
        model.addAttribute("formAction", "/habilidades-destrezas/adm/cargo/" + cargoId);
        addHabilidadesDestrezasBreadcrumbs(cargoId, "Nueva", model);
        return "adm-cargo-edit-habilidades-destrezas-form";
    }

    @PostMapping("/adm/cargo/{cargoId}")
    public String saveHabilidadesDestrezasadm(@PathVariable Long cargoId,
                                           @Valid @ModelAttribute("habilidadesDestrezas") HabilidadesDestrezas habilidadesDestrezas,
                                           BindingResult result, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));
        habilidadesDestrezas.setCargo(cargo);

        if (result.hasErrors()) {
            model.addAttribute("cargo", cargo);
            addHabilidadesDestrezasBreadcrumbs(cargoId, habilidadesDestrezas.getId() == null ? "Nueva" : "Editar", model);
            return "adm-cargo-edit-habilidades-destrezas-form";
        }

        habilidadesDestrezasService.save(habilidadesDestrezas);
        return "redirect:/habilidades-destrezas/adm/cargo/" + cargoId;
    }

    @GetMapping("/adm/cargo/{cargoId}/edit/{id}")
    public String showEditHabilidadesDestrezasFormadm(@PathVariable Long cargoId, @PathVariable Long id, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));
        HabilidadesDestrezas habilidadesDestrezas = habilidadesDestrezasService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Habilidad no encontrada: " + id));
        model.addAttribute("cargo", cargo);
        model.addAttribute("habilidadesDestrezas", habilidadesDestrezas);
        model.addAttribute("formAction", "/habilidades-destrezas/adm/cargo/" + cargoId + "/edit/" + id);
        addHabilidadesDestrezasBreadcrumbs(cargoId, "Editar", model);
        return "adm-cargo-edit-habilidades-destrezas-form";
    }

    @PostMapping("/adm/cargo/{cargoId}/edit/{id}")
    public String updateHabilidadesDestrezasadm(@PathVariable Long cargoId, @PathVariable Long id,
                                                @Valid @ModelAttribute("habilidadesDestrezas") HabilidadesDestrezas habilidadesDestrezas,
                                                BindingResult result, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));

        HabilidadesDestrezas existingHabilidad = habilidadesDestrezasService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Habilidad no encontrada: " + id));

        // IMPORTANTE: Preservar el orderIndex original
        Integer originalOrderIndex = existingHabilidad.getOrderIndex();

        existingHabilidad.setDescripcion(habilidadesDestrezas.getDescripcion());
        existingHabilidad.setCargo(cargo);
        existingHabilidad.setOrderIndex(originalOrderIndex); // ← MANTENER EL ORDEN ORIGINAL

        if (result.hasErrors()) {
            model.addAttribute("cargo", cargo);
            addHabilidadesDestrezasBreadcrumbs(cargoId, "Editar", model);
            return "adm-cargo-edit-habilidades-destrezas-form";
        }

        habilidadesDestrezasService.save(existingHabilidad);
        return "redirect:/habilidades-destrezas/adm/cargo/" + cargoId;
    }

    @GetMapping("/adm/cargo/{cargoId}/delete/{id}")
    public String deleteHabilidadesDestrezasadm(@PathVariable Long cargoId, @PathVariable Long id) {
        habilidadesDestrezasService.deleteById(id);
        return "redirect:/habilidades-destrezas/adm/cargo/" + cargoId;
    }

    @PostMapping("/adm/cargo/{cargoId}/reorder")
    @ResponseBody
    public ResponseEntity<?> reorderHabilidadesDestrezasadm(@PathVariable Long cargoId, @RequestBody List<Long> orderedIds) {
        try {
            habilidadesDestrezasService.updateOrder(cargoId, orderedIds);
            return ResponseEntity.ok("Orden actualizado correctamente.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar el orden: " + e.getMessage());
        }
    }
}