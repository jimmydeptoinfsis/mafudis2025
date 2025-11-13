package com.app.thym.ddejim.mafudis.controller;

import com.app.thym.ddejim.mafudis.Service.CargoService;
import com.app.thym.ddejim.mafudis.Service.OtrosConocimientosService;
import com.app.thym.ddejim.mafudis.dto.Breadcrumb;
import com.app.thym.ddejim.mafudis.model.Cargo;
import com.app.thym.ddejim.mafudis.model.OtrosConocimientos;
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
@RequestMapping("/otros-conocimientos")
public class OtrosConocimientosController {

    @Autowired
    private OtrosConocimientosService otrosConocimientosService;

    @Autowired
    private CargoService cargoService;
    // --- INICIO: MÉTODO AUXILIAR PARA BREADCRUMBS ---
    private void addOtrosConocimientosBreadcrumbs(Long cargoId, String currentPageLabel, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));

        List<Breadcrumb> breadcrumbs = new ArrayList<>();
        breadcrumbs.add(new Breadcrumb("Inicio", "/dashboard")); // Asumiendo que esta es la URL de la lista de cargos
        breadcrumbs.add(new Breadcrumb("Organigrama de Cargos", "/organigrama12")); // La página actual no necesita URL
        breadcrumbs.add(new Breadcrumb("Detalle del cargo", "/cargos/adm/details/" + cargoId));

        if (currentPageLabel != null && !currentPageLabel.isEmpty()) {
            breadcrumbs.add(new Breadcrumb("Otros Conocimientos", "/otros-conocimientos/adm/cargo/" + cargoId));
            breadcrumbs.add(new Breadcrumb(currentPageLabel, null));
        } else {
            breadcrumbs.add(new Breadcrumb("Otros Conocimientos", null));
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
    public String listOtrosConocimientosPorCargo(@PathVariable Long cargoId, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));
        List<OtrosConocimientos> otrosConocimientos = otrosConocimientosService.findByCargoId(cargoId);

        model.addAttribute("cargo", cargo);
        model.addAttribute("otrosConocimientos", otrosConocimientos);
        return "otros_conocimientos/list";
    }

    @GetMapping("/cargo/{cargoId}/new")
    public String showNewOtrosConocimientosForm(@PathVariable Long cargoId, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));
        OtrosConocimientos otrosConocimientos = new OtrosConocimientos();
        otrosConocimientos.setCargo(cargo);
        model.addAttribute("cargo", cargo);
        model.addAttribute("otrosConocimientos", otrosConocimientos);
        model.addAttribute("formAction", "/otros-conocimientos/cargo/" + cargoId);
        return "otros_conocimientos/form";
    }

    @PostMapping("/cargo/{cargoId}")
    public String saveOtrosConocimientos(@PathVariable Long cargoId,
                                         @Valid @ModelAttribute("otrosConocimientos") OtrosConocimientos otrosConocimientos,
                                         BindingResult result, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));
        otrosConocimientos.setCargo(cargo);

        if (result.hasErrors()) {
            model.addAttribute("cargo", cargo);
            return "otros_conocimientos/form";
        }

        otrosConocimientosService.save(otrosConocimientos);
        return "redirect:/otros-conocimientos/cargo/" + cargoId;
    }

    @GetMapping("/cargo/{cargoId}/edit/{id}")
    public String showEditOtrosConocimientosForm(@PathVariable Long cargoId, @PathVariable Long id, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));
        OtrosConocimientos otrosConocimientos = otrosConocimientosService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Conocimiento no encontrado: " + id));
        model.addAttribute("cargo", cargo);
        model.addAttribute("otrosConocimientos", otrosConocimientos);
        model.addAttribute("formAction", "/otros-conocimientos/cargo/" + cargoId + "/edit/" + id);
        return "otros_conocimientos/form";
    }

    @PostMapping("/cargo/{cargoId}/edit/{id}")
    public String updateOtrosConocimientos(@PathVariable Long cargoId, @PathVariable Long id,
                                           @Valid @ModelAttribute("otrosConocimientos") OtrosConocimientos otrosConocimientos,
                                           BindingResult result, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));
        otrosConocimientos.setCargo(cargo);
        otrosConocimientos.setId(id);

        if (result.hasErrors()) {
            model.addAttribute("cargo", cargo);
            return "otros_conocimientos/form";
        }

        otrosConocimientosService.save(otrosConocimientos);
        return "redirect:/otros-conocimientos/cargo/" + cargoId;
    }

    @GetMapping("/cargo/{cargoId}/delete/{id}")
    public String deleteOtrosConocimientos(@PathVariable Long cargoId, @PathVariable Long id) {
        otrosConocimientosService.deleteById(id);
        return "redirect:/otros-conocimientos/cargo/" + cargoId;
    }

    @PostMapping("/cargo/{cargoId}/reorder")
    @ResponseBody
    public ResponseEntity<?> reorderOtrosConocimientos(@PathVariable Long cargoId, @RequestBody List<Long> orderedIds) {
        try {
            otrosConocimientosService.updateOrder(cargoId, orderedIds);
            return ResponseEntity.ok("Orden actualizado correctamente.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar el orden: " + e.getMessage());
        }
    }

    @GetMapping("/adm/cargo/{cargoId}")
    public String listOtrosConocimientosPorCargoadm(@PathVariable Long cargoId, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));
        List<OtrosConocimientos> otrosConocimientos = otrosConocimientosService.findByCargoId(cargoId);

        model.addAttribute("cargo", cargo);
        model.addAttribute("otrosConocimientos", otrosConocimientos);
        addOtrosConocimientosBreadcrumbs(cargoId, null, model);
        return "adm-cargo-edit-otros-conocimientos-list";
    }

    @GetMapping("/adm/cargo/{cargoId}/new")
    public String showNewOtrosConocimientosFormadm(@PathVariable Long cargoId, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));
        OtrosConocimientos otrosConocimientos = new OtrosConocimientos();
        otrosConocimientos.setCargo(cargo);
        model.addAttribute("cargo", cargo);
        model.addAttribute("otrosConocimientos", otrosConocimientos);
        model.addAttribute("formAction", "/otros-conocimientos/adm/cargo/" + cargoId);
        addOtrosConocimientosBreadcrumbs(cargoId, "Nuevo", model);
        return "adm-cargo-edit-otros-conocimientos-form";
    }

    @PostMapping("/adm/cargo/{cargoId}")
    public String saveOtrosConocimientosadm(@PathVariable Long cargoId,
                                         @Valid @ModelAttribute("otrosConocimientos") OtrosConocimientos otrosConocimientos,
                                         BindingResult result, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));
        otrosConocimientos.setCargo(cargo);

        if (result.hasErrors()) {
            model.addAttribute("cargo", cargo);
            addOtrosConocimientosBreadcrumbs(cargoId, otrosConocimientos.getId() == null ? "Nuevo" : "Editar", model);
            return "adm-cargo-edit-otros-conocimientos-form";
        }

        otrosConocimientosService.save(otrosConocimientos);
        return "redirect:/otros-conocimientos/adm/cargo/" + cargoId;
    }

    @GetMapping("/adm/cargo/{cargoId}/edit/{id}")
    public String showEditOtrosConocimientosFormadm(@PathVariable Long cargoId, @PathVariable Long id, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));
        OtrosConocimientos otrosConocimientos = otrosConocimientosService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Conocimiento no encontrado: " + id));
        model.addAttribute("cargo", cargo);
        model.addAttribute("otrosConocimientos", otrosConocimientos);
        model.addAttribute("formAction", "/otros-conocimientos/adm/cargo/" + cargoId + "/edit/" + id);
        addOtrosConocimientosBreadcrumbs(cargoId, "Editar", model);
        return "adm-cargo-edit-otros-conocimientos-form";
    }

    @PostMapping("/adm/cargo/{cargoId}/edit/{id}")
    public String updateOtrosConocimientosadm(@PathVariable Long cargoId, @PathVariable Long id,
                                              @Valid @ModelAttribute("otrosConocimientos") OtrosConocimientos otrosConocimientos,
                                              BindingResult result, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));

        OtrosConocimientos existingConocimiento = otrosConocimientosService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Conocimiento no encontrado: " + id));

        // IMPORTANTE: Preservar el orderIndex original
        Integer originalOrderIndex = existingConocimiento.getOrderIndex();

        existingConocimiento.setDescripcion(otrosConocimientos.getDescripcion());
        existingConocimiento.setCargo(cargo);
        existingConocimiento.setOrderIndex(originalOrderIndex); // ← MANTENER EL ORDEN ORIGINAL

        if (result.hasErrors()) {
            model.addAttribute("cargo", cargo);
            addOtrosConocimientosBreadcrumbs(cargoId, "Editar", model);
            return "adm-cargo-edit-otros-conocimientos-form";
        }

        otrosConocimientosService.save(existingConocimiento);
        return "redirect:/otros-conocimientos/adm/cargo/" + cargoId;
    }

    @GetMapping("/adm/cargo/{cargoId}/delete/{id}")
    public String deleteOtrosConocimientosadm(@PathVariable Long cargoId, @PathVariable Long id) {
        otrosConocimientosService.deleteById(id);
        return "redirect:/otros-conocimientos/adm/cargo/" + cargoId;
    }

    @PostMapping("/adm/cargo/{cargoId}/reorder")
    @ResponseBody
    public ResponseEntity<?> reorderOtrosConocimientosadm(@PathVariable Long cargoId, @RequestBody List<Long> orderedIds) {
        try {
            otrosConocimientosService.updateOrder(cargoId, orderedIds);
            return ResponseEntity.ok("Orden actualizado correctamente.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar el orden: " + e.getMessage());
        }
    }
}