package com.app.thym.ddejim.mafudis.controller;

import com.app.thym.ddejim.mafudis.Service.CargoService;
import com.app.thym.ddejim.mafudis.Service.ResponsabilidadService;
import com.app.thym.ddejim.mafudis.dto.Breadcrumb;
import com.app.thym.ddejim.mafudis.model.Cargo;
import com.app.thym.ddejim.mafudis.model.Responsabilidad;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult; // Necesario para BindingResult
import jakarta.validation.Valid; // Necesario para @Valid

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/responsabilidades") // Define la ruta base para este controlador
public class ResponsabilidadController {

    @Autowired
    private ResponsabilidadService responsabilidadService;

    @Autowired
    private CargoService cargoService;

    // *** MÉTODO FALTANTE ***
    // Este método manejará la petición GET a /responsabilidades/cargo/{cargoId}
    @GetMapping("/cargo/{cargoId}")
    public String listResponsabilidadesPorCargo(@PathVariable Long cargoId, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));
        List<Responsabilidad> responsabilidades = responsabilidadService.findByCargoId(cargoId);

        model.addAttribute("cargo", cargo);
        model.addAttribute("responsabilidades", responsabilidades);
        // Asegúrate de que 'responsabilidades/list.html' exista
        return "responsabilidades/list";
    }

    // --- Otros métodos que probablemente necesitarás ---

    // Mostrar formulario para crear nueva responsabilidad
    @GetMapping("/cargo/{cargoId}/new")
    public String showNewResponsabilidadForm(@PathVariable Long cargoId, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));
        Responsabilidad responsabilidad = new Responsabilidad();
        responsabilidad.setCargo(cargo); // Asociar con el cargo
        model.addAttribute("cargo", cargo); // Añadir cargo al modelo
        model.addAttribute("responsabilidad", responsabilidad);
        // Asegúrate de tener una plantilla llamada 'responsabilidades/form.html'
        return "responsabilidades/form";
    }

    // Guardar nueva responsabilidad
    @PostMapping("/cargo/{cargoId}")
    public String saveResponsabilidad(@PathVariable Long cargoId,
                                      @Valid @ModelAttribute("responsabilidad") Responsabilidad responsabilidad,
                                      BindingResult result, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));
        responsabilidad.setCargo(cargo); // Asegurarse de que el cargo esté asociado

        if (result.hasErrors()) {
            model.addAttribute("cargo", cargo); // Añadir cargo de nuevo si hay errores
            return "responsabilidades/form"; // Volver al formulario si hay errores
        }

        responsabilidadService.save(responsabilidad);
        return "redirect:/responsabilidades/cargo/" + cargoId; // Redirigir a la lista
    }


    // Mostrar formulario para editar una responsabilidad
    @GetMapping("/cargo/{cargoId}/edit/{id}")
    public String showEditResponsabilidadForm(@PathVariable Long cargoId, @PathVariable Long id, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));
        Responsabilidad responsabilidad = responsabilidadService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Responsabilidad no encontrada: " + id));
        model.addAttribute("cargo", cargo);
        model.addAttribute("responsabilidad", responsabilidad);
        return "responsabilidades/form"; // Reutiliza el mismo formulario
    }

    // Actualizar una responsabilidad existente
    @PostMapping("/cargo/{cargoId}/edit/{id}")
    public String updateResponsabilidad(@PathVariable Long cargoId, @PathVariable Long id,
                                        @Valid @ModelAttribute("responsabilidad") Responsabilidad responsabilidad,
                                        BindingResult result, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));
        responsabilidad.setCargo(cargo); // Asegurarse de que el cargo esté asociado
        responsabilidad.setId(id); // Asegurarse de que el ID esté seteado para la actualización

        if (result.hasErrors()) {
            model.addAttribute("cargo", cargo); // Añadir cargo de nuevo si hay errores
            return "responsabilidades/form"; // Volver al formulario si hay errores
        }
        responsabilidadService.save(responsabilidad);
        return "redirect:/responsabilidades/cargo/" + cargoId;
    }

    // Eliminar una responsabilidad
    @GetMapping("/cargo/{cargoId}/delete/{id}")
    public String deleteResponsabilidad(@PathVariable Long cargoId, @PathVariable Long id) {
        responsabilidadService.deleteById(id);
        return "redirect:/responsabilidades/cargo/" + cargoId;
    }

    // Reordenar responsabilidades (si usas drag & drop)
    @PostMapping("/cargo/{cargoId}/reorder")
    @ResponseBody // Importante para devolver una respuesta directa (no una vista)
    public ResponseEntity<?> reorderResponsabilidades(@PathVariable Long cargoId, @RequestBody List<Long> orderedIds) {
        try {
            responsabilidadService.updateOrder(cargoId, orderedIds);
            return ResponseEntity.ok("Orden actualizado correctamente.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar el orden: " + e.getMessage());
        }
    }
    private void addResponsabilidadBreadcrumbs(Long cargoId, String currentPageLabel, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));

        List<Breadcrumb> breadcrumbs = new ArrayList<>();
        breadcrumbs.add(new Breadcrumb("Inicio", "/dashboard")); // Asumiendo que esta es la URL de la lista de cargos
        breadcrumbs.add(new Breadcrumb("Organigrama de Cargos", "/organigrama12")); // La página actual no necesita URL
        breadcrumbs.add(new Breadcrumb("Detalle del cargo", "/cargos/adm/details/" + cargoId));

        if (currentPageLabel != null && !currentPageLabel.isEmpty()) {
            breadcrumbs.add(new Breadcrumb("Responsabilidades", "/responsabilidades/adm/cargo/" + cargoId));
            breadcrumbs.add(new Breadcrumb(currentPageLabel, null));
        } else {
            breadcrumbs.add(new Breadcrumb("Responsabilidades", null));
        }
        model.addAttribute("breadcrumbs", breadcrumbs);
        // --- INICIO: LÓGICA PARA OBTENER LA URL DE "VOLVER" ---
        String backUrl = null;
        // Nos aseguramos de que haya al menos 2 eslabones para tener un penúltimo.
        if (breadcrumbs.size() > 1) {
            // El penúltimo eslabón está en la posición (tamaño - 2)
            Breadcrumb penultimateCrumb = breadcrumbs.get(breadcrumbs.size() - 2);
            if (penultimateCrumb != null) {
                backUrl = penultimateCrumb.getUrl();
            }
        }
        // Si no se encuentra una URL penúltima, podemos poner una por defecto (opcional)
        if (backUrl == null) {
            backUrl = "/dashboard"; // URL de fallback
        }
        model.addAttribute("backUrl", backUrl);
        // --- FIN: LÓGICA PARA OBTENER LA URL DE "VOLVER" ---

    }

    @GetMapping("/adm/cargo/{cargoId}")
    public String listResponsabilidadesPorCargoadm(@PathVariable Long cargoId, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));
        List<Responsabilidad> responsabilidades = responsabilidadService.findByCargoId(cargoId);

        model.addAttribute("cargo", cargo);
        model.addAttribute("responsabilidades", responsabilidades);
        addResponsabilidadBreadcrumbs(cargoId, null, model);
        // Asegúrate de que 'responsabilidades/list.html' exista
        return "adm-cargo-edit-responsabilidades-list";
    }

    // --- Otros métodos que probablemente necesitarás ---

    // Mostrar formulario para crear nueva responsabilidad
    @GetMapping("/adm/cargo/{cargoId}/new")
    public String showNewResponsabilidadFormadm(@PathVariable Long cargoId, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));
        Responsabilidad responsabilidad = new Responsabilidad();
        responsabilidad.setCargo(cargo); // Asociar con el cargo
        model.addAttribute("cargo", cargo); // Añadir cargo al modelo
        model.addAttribute("responsabilidad", responsabilidad);
        // Asegúrate de tener una plantilla llamada 'responsabilidades/form.html'
        addResponsabilidadBreadcrumbs(cargoId, "Nuevo", model);
        return "adm-cargo-edit-responsabilidades-form";
    }

    // Guardar nueva responsabilidad
    @PostMapping("/adm/cargo/{cargoId}")
    public String saveResponsabilidadadm(@PathVariable Long cargoId,
                                      @Valid @ModelAttribute("responsabilidad") Responsabilidad responsabilidad,
                                      BindingResult result, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));
        responsabilidad.setCargo(cargo); // Asegurarse de que el cargo esté asociado

        if (result.hasErrors()) {
            model.addAttribute("cargo", cargo); // Añadir cargo de nuevo si hay errores
            return "adm-cargo-edit-responsabilidades-form"; // Volver al formulario si hay errores
        }

        responsabilidadService.save(responsabilidad);
        return "redirect:/responsabilidades/adm/cargo/" + cargoId; // Redirigir a la lista
    }


    // Mostrar formulario para editar una responsabilidad
    @GetMapping("/adm/cargo/{cargoId}/edit/{id}")
    public String showEditResponsabilidadFormadm(@PathVariable Long cargoId, @PathVariable Long id, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));
        Responsabilidad responsabilidad = responsabilidadService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Responsabilidad no encontrada: " + id));
        model.addAttribute("cargo", cargo);
        model.addAttribute("responsabilidad", responsabilidad);
        addResponsabilidadBreadcrumbs(cargoId, "Editar", model);
        return "adm-cargo-edit-responsabilidades-form"; // Reutiliza el mismo formulario
    }

    // Actualizar una responsabilidad existente
    @PostMapping("/adm/cargo/{cargoId}/edit/{id}")
    public String updateResponsabilidadadm(@PathVariable Long cargoId, @PathVariable Long id,
                                        @Valid @ModelAttribute("responsabilidad") Responsabilidad responsabilidad,
                                        BindingResult result, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));
        responsabilidad.setCargo(cargo); // Asegurarse de que el cargo esté asociado
        responsabilidad.setId(id); // Asegurarse de que el ID esté seteado para la actualización

        if (result.hasErrors()) {
            model.addAttribute("cargo", cargo); // Añadir cargo de nuevo si hay errores
            return "adm-cargo-edit-responsabilidades-form"; // Volver al formulario si hay errores
        }
        responsabilidadService.save(responsabilidad);
        addResponsabilidadBreadcrumbs(cargoId, "Nuevo", model);
        return "redirect:/responsabilidades/adm/cargo/" + cargoId;
    }

    // Eliminar una responsabilidad
    @GetMapping("/adm/cargo/{cargoId}/delete/{id}")
    public String deleteResponsabilidadadm(@PathVariable Long cargoId, @PathVariable Long id) {
        responsabilidadService.deleteById(id);
        return "redirect:/responsabilidades/adm/cargo/" + cargoId;
    }

    // Reordenar responsabilidades (si usas drag & drop)
    @PostMapping("/adm/cargo/{cargoId}/reorder")
    @ResponseBody // Importante para devolver una respuesta directa (no una vista)
    public ResponseEntity<?> reorderResponsabilidadesadm(@PathVariable Long cargoId, @RequestBody List<Long> orderedIds) {
        try {
            responsabilidadService.updateOrder(cargoId, orderedIds);
            return ResponseEntity.ok("Orden actualizado correctamente.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar el orden: " + e.getMessage());
        }
    }

}