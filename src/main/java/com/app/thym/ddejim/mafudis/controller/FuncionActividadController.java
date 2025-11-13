package com.app.thym.ddejim.mafudis.controller;

import com.app.thym.ddejim.mafudis.Service.CargoService;
import com.app.thym.ddejim.mafudis.Service.FuncionActividadService;
import com.app.thym.ddejim.mafudis.dto.Breadcrumb;
import com.app.thym.ddejim.mafudis.model.Cargo;
import com.app.thym.ddejim.mafudis.model.FuncionActividad;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/funciones-actividades") // Changed to hyphen
public class FuncionActividadController {

    @Autowired
    private FuncionActividadService funcionActividadService;

    @Autowired
    private CargoService cargoService;

    @GetMapping("/cargo/{cargoId}")
    public String listFuncionesActividadesPorCargo(@PathVariable Long cargoId, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));
        List<FuncionActividad> funcionesActividades = funcionActividadService.findByCargoId(cargoId);

        model.addAttribute("cargo", cargo);
        model.addAttribute("funcionesActividades", funcionesActividades);
        return "funciones_actividades/list"; // Changed to "list" to use list.html
    }

    @GetMapping("/cargo/{cargoId}/new")
    public String showNewFuncionActividadForm(@PathVariable Long cargoId, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));
        FuncionActividad funcionActividad = new FuncionActividad();
        funcionActividad.setCargo(cargo);
        model.addAttribute("cargo", cargo);
        model.addAttribute("funcionActividad", funcionActividad);
        model.addAttribute("formAction", "/funciones-actividades/cargo/" + cargoId); // Set form action
        return "funciones_actividades/form";
    }

    @PostMapping("/cargo/{cargoId}")
    public String saveFuncionActividad(@PathVariable Long cargoId,
                                       @Valid @ModelAttribute("funcionActividad") FuncionActividad funcionActividad,
                                       BindingResult result, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));
        funcionActividad.setCargo(cargo);

        if (result.hasErrors()) {
            model.addAttribute("cargo", cargo);
            addFuncionesBreadcrumbs(cargoId, funcionActividad.getId() == null ? "Nueva" : "Editar", model);
            return "funciones_actividades/form";
        }

        funcionActividadService.save(funcionActividad);

        return "redirect:/funciones-actividades/cargo/" + cargoId;
    }

    @GetMapping("/cargo/{cargoId}/edit/{id}")
    public String showEditFuncionActividadForm(@PathVariable Long cargoId, @PathVariable Long id, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));
        FuncionActividad funcionActividad = funcionActividadService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Función o actividad no encontrada: " + id));
        model.addAttribute("cargo", cargo);
        model.addAttribute("funcionActividad", funcionActividad);
        model.addAttribute("formAction", "/funciones-actividades/cargo/" + cargoId + "/edit/" + id); // Set form action
        return "funciones_actividades/form";
    }

    @PostMapping("/cargo/{cargoId}/edit/{id}")
    public String updateFuncionActividad(@PathVariable Long cargoId, @PathVariable Long id,
                                         @Valid @ModelAttribute("funcionActividad") FuncionActividad funcionActividad,
                                         BindingResult result, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));
        funcionActividad.setCargo(cargo);
        funcionActividad.setId(id);

        if (result.hasErrors()) {
            model.addAttribute("cargo", cargo);
            return "funciones_actividades/form";
        }

        funcionActividadService.save(funcionActividad);
        return "redirect:/funciones-actividades/cargo/" + cargoId;
    }

    @GetMapping("/cargo/{cargoId}/delete/{id}")
    public String deleteFuncionActividad(@PathVariable Long cargoId, @PathVariable Long id) {
        funcionActividadService.deleteById(id);
        return "redirect:/funciones-actividades/cargo/" + cargoId;
    }

    @PostMapping("/cargo/{cargoId}/reorder")
    @ResponseBody
    public ResponseEntity<?> reorderFuncionesActividades(@PathVariable Long cargoId, @RequestBody List<Long> orderedIds) {
        try {
            funcionActividadService.updateOrder(cargoId, orderedIds);
            return ResponseEntity.ok("Orden actualizado correctamente.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar el orden: " + e.getMessage());
        }
    }
    private void addFuncionesBreadcrumbs(Long cargoId, String currentPageLabel, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));

        List<Breadcrumb> breadcrumbs = new ArrayList<>();
        breadcrumbs.add(new Breadcrumb("Inicio", "/dashboard")); // Asumiendo que esta es la URL de la lista de cargos
        breadcrumbs.add(new Breadcrumb("Organigrama de Cargos", "/organigrama12")); // La página actual no necesita URL
        breadcrumbs.add(new Breadcrumb("Detalle del cargo", "/cargos/adm/details/" + cargoId));

        // Construye el resto de la ruta
        if (currentPageLabel != null && !currentPageLabel.isEmpty()) {
            breadcrumbs.add(new Breadcrumb("Funciones y Actividades", "/funciones-actividades/adm/cargo/" + cargoId));
            breadcrumbs.add(new Breadcrumb(currentPageLabel, null)); // Página actual
        } else {
            breadcrumbs.add(new Breadcrumb("Funciones y Actividades", null)); // Página actual
        }
        model.addAttribute("breadcrumbs", breadcrumbs);

        // Lógica para el botón "Volver"
        String backUrl = "/cargos/adm"; // URL por defecto
        if (breadcrumbs.size() > 1) {
            backUrl = breadcrumbs.get(breadcrumbs.size() - 2).getUrl();
        }
        model.addAttribute("backUrl", backUrl);
    }
    @GetMapping("/adm/cargo/{cargoId}")
    public String listFuncionesActividadesPorCargoadm(@PathVariable Long cargoId, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));
        List<FuncionActividad> funcionesActividades = funcionActividadService.findByCargoId(cargoId);

        model.addAttribute("cargo", cargo);
        model.addAttribute("funcionesActividades", funcionesActividades);
        addFuncionesBreadcrumbs(cargoId, null, model);
        return "adm-cargo-edit-funciones-actividades-list"; // Changed to "list" to use list.html
    }



    // --- Mapeos para las vistas ---


    @GetMapping("/adm/cargo/{cargoId}/new")
    public String showNewFuncionActividadFormadm(@PathVariable Long cargoId, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));
        FuncionActividad funcionActividad = new FuncionActividad();
        funcionActividad.setCargo(cargo);
        model.addAttribute("cargo", cargo);
        model.addAttribute("funcionActividad", funcionActividad);
        model.addAttribute("formAction", "/funciones-actividades/adm/cargo/" + cargoId); // Set form action
        addFuncionesBreadcrumbs(cargoId, "Nueva", model);
        return "adm-cargo-edit-funciones-actividades-form";
    }

    @PostMapping("/adm/cargo/{cargoId}")
    public String saveFuncionActividadadm(@PathVariable Long cargoId,
                                       @Valid @ModelAttribute("funcionActividad") FuncionActividad funcionActividad,
                                       BindingResult result, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));
        funcionActividad.setCargo(cargo);

        if (result.hasErrors()) {
            model.addAttribute("cargo", cargo);
            return "adm-cargo-edit-funciones-actividades-form";
        }

        funcionActividadService.save(funcionActividad);
        return "redirect:/funciones-actividades/adm/cargo/" + cargoId;
    }

    @GetMapping("/adm/cargo/{cargoId}/edit/{id}")
    public String showEditFuncionActividadFormadm(@PathVariable Long cargoId, @PathVariable Long id, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));
        FuncionActividad funcionActividad = funcionActividadService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Función o actividad no encontrada: " + id));
        model.addAttribute("cargo", cargo);
        model.addAttribute("funcionActividad", funcionActividad);
        model.addAttribute("formAction", "/funciones-actividades/adm/cargo/" + cargoId + "/edit/" + id); // Set form action
        addFuncionesBreadcrumbs(cargoId, "Editar", model);
        return "adm-cargo-edit-funciones-actividades-form";
    }

    @PostMapping("/adm/cargo/{cargoId}/edit/{id}")
    public String updateFuncionActividadadm(@PathVariable Long cargoId, @PathVariable Long id,
                                            @Valid @ModelAttribute("funcionActividad") FuncionActividad funcionActividad,
                                            BindingResult result, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));

        FuncionActividad existingFuncion = funcionActividadService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Función actividad no encontrada: " + id));

        // IMPORTANTE: Preservar el orderIndex original
        Integer originalOrderIndex = existingFuncion.getOrderIndex();

        existingFuncion.setDescripcion(funcionActividad.getDescripcion());
        existingFuncion.setCargo(cargo);
        existingFuncion.setOrderIndex(originalOrderIndex); // ← MANTENER EL ORDEN ORIGINAL

        if (result.hasErrors()) {
            model.addAttribute("cargo", cargo);
            addFuncionesBreadcrumbs(cargoId, "Editar", model);
            return "adm-cargo-edit-funciones-actividades-form";
        }

        funcionActividadService.save(existingFuncion);
        return "redirect:/funciones-actividades/adm/cargo/" + cargoId;
    }

    @GetMapping("/adm/cargo/{cargoId}/delete/{id}")
    public String deleteFuncionActividadadm(@PathVariable Long cargoId, @PathVariable Long id) {
        funcionActividadService.deleteById(id);
        return "redirect:/funciones-actividades/adm/cargo/" + cargoId;
    }

    @PostMapping("/adm/cargo/{cargoId}/reorder")
    @ResponseBody
    public ResponseEntity<?> reorderFuncionesActividadesadm(@PathVariable Long cargoId, @RequestBody List<Long> orderedIds) {
        try {
            funcionActividadService.updateOrder(cargoId, orderedIds);
            return ResponseEntity.ok("Orden actualizado correctamente.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar el orden: " + e.getMessage());
        }
    }
}