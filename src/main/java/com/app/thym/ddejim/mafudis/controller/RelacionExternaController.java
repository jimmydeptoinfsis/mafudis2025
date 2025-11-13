package com.app.thym.ddejim.mafudis.controller;

import com.app.thym.ddejim.mafudis.Service.CargoService;
import com.app.thym.ddejim.mafudis.Service.RelacionExternaService;
import com.app.thym.ddejim.mafudis.dto.Breadcrumb;
import com.app.thym.ddejim.mafudis.model.Cargo;
import com.app.thym.ddejim.mafudis.model.RelacionExterna;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Controller
@RequestMapping("/relaciones-externas")
public class RelacionExternaController {

    private static final Logger LOGGER = Logger.getLogger(RelacionExternaController.class.getName());

    @Autowired
    private RelacionExternaService relacionExternaService;

    @Autowired
    private CargoService cargoService; // Inyecta CargoService


    // --- INICIO: MÉTODO AUXILIAR PARA BREADCRUMBS ---
    private void addRelacionesExternasBreadcrumbs(Long cargoId, String currentPageLabel, Model model) {
        // Si no hay un cargoId, no podemos generar los breadcrumbs anidados.
        // Podríamos tener una lógica para breadcrumbs más simples si fuera necesario.
        if (cargoId == null) return;

        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));

        List<Breadcrumb> breadcrumbs = new ArrayList<>();
        breadcrumbs.add(new Breadcrumb("Inicio", "/dashboard")); // Asumiendo que esta es la URL de la lista de cargos
        breadcrumbs.add(new Breadcrumb("Organigrama de Cargos", "/organigrama12")); // La página actual no necesita URL
        breadcrumbs.add(new Breadcrumb("Detalle del cargo", "/cargos/adm/details/" + cargoId));

        // El flujo aquí es un poco diferente:
        // 1. Details -> Asignar Relaciones (adm-cargo-edit-relaciones-externas.html)
        // 2. Asignar Relaciones -> Gestionar Catálogo (adm-cargo-edit-relaciones-externas-list.html)
        // 3. Gestionar Catálogo -> Formulario
        breadcrumbs.add(new Breadcrumb("Gestionar Relaciones Externas", "/cargos/adm/" + cargoId + "/relaciones-externas"));

        if ("list".equals(currentPageLabel)) {
            breadcrumbs.add(new Breadcrumb("Gestionar Catálogo", null));
        } else if (currentPageLabel != null && !currentPageLabel.isEmpty()) {
            breadcrumbs.add(new Breadcrumb("Gestionar Catálogo", "/relaciones-externas?fromCargoId=" + cargoId));
            breadcrumbs.add(new Breadcrumb(currentPageLabel, null));
        }

        model.addAttribute("breadcrumbs", breadcrumbs);

        // Lógica para el botón "Volver"
        String backUrl = "/cargos/adm/details/" + cargoId; // URL por defecto es volver a detalles del cargo
        if (breadcrumbs.size() > 1) {
            backUrl = breadcrumbs.get(breadcrumbs.size() - 2).getUrl();
        }
        model.addAttribute("backUrl", backUrl);
    }
    // --- FIN: MÉTODO AUXILIAR PARA BREADCRUMBS ---
    /*@GetMapping
    public String listRelaciones(Model model) {
        LOGGER.info("Listing all relaciones externas");
        model.addAttribute("relaciones", relacionExternaService.findAll());
        return "relaciones_externas/list";
    }*/

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        LOGGER.info("Showing create form for new RelacionExterna");
        model.addAttribute("relacionExterna", new RelacionExterna());
        model.addAttribute("isNew", true);
        return "relaciones_externas/form";
    }

   /* @PostMapping
    public String saveRelacion(@Valid @ModelAttribute("relacionExterna") RelacionExterna relacionExterna,
                               BindingResult result, Model model) {
        LOGGER.info("Saving new RelacionExterna: " + relacionExterna.getNombre());
        if (result.hasErrors()) {
            LOGGER.warning("Validation errors in saveRelacion: " + result.getAllErrors());
            model.addAttribute("isNew", true);
            return "relaciones_externas/form";
        }
        relacionExternaService.save(relacionExterna);
        return "redirect:/relaciones-externas";
    }*/

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        LOGGER.info("Showing edit form for RelacionExterna ID: " + id);
        RelacionExterna relacionExterna = relacionExternaService.findById(id)
                .orElseThrow(() -> {
                    LOGGER.severe("RelacionExterna not found: " + id);
                    return new IllegalArgumentException("Relación no encontrada: " + id);
                });
        model.addAttribute("relacionExterna", relacionExterna);
        model.addAttribute("isNew", false);
        return "relaciones_externas/form";
    }

    @PostMapping("/edit/{id}")
    public String updateRelacion(@PathVariable("id") Long id,
                                 @Valid @ModelAttribute("relacionExterna") RelacionExterna relacionExterna,
                                 BindingResult result, Model model) {
        LOGGER.info("Updating RelacionExterna ID: " + id);
        if (result.hasErrors()) {
            LOGGER.warning("Validation errors in updateRelacion: " + result.getAllErrors());
            model.addAttribute("isNew", false);
            return "relaciones_externas/form";
        }
        relacionExterna.setId(id);
        relacionExternaService.save(relacionExterna);
        return "redirect:/relaciones-externas";
    }

    @GetMapping("/delete/{id}")
    public String deleteRelacion(@PathVariable("id") Long id) {
        LOGGER.info("Deleting RelacionExterna ID: " + id);
        relacionExternaService.deleteById(id);
        return "redirect:/relaciones-externas";
    }

    @PostMapping("/reorder")
    @ResponseBody
    public ResponseEntity<?> reorderRelaciones(@RequestBody List<Long> orderedIds) {
        LOGGER.info("Received reorder request with IDs: " + orderedIds);
        try {
            relacionExternaService.updateOrder(orderedIds);
            LOGGER.info("Order updated successfully for IDs: " + orderedIds);
            return ResponseEntity.ok("Orden actualizado correctamente.");
        } catch (Exception e) {
            LOGGER.severe("Error updating order: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar el orden: " + e.getMessage());
        }
    }
    @GetMapping
    public String listAllRelacionesadm(Model model, @RequestParam(name = "fromCargoId", required = false) Long cargoId) {
        LOGGER.info("Listing all relaciones externas for CRUD management");
        model.addAttribute("relaciones", relacionExternaService.findAll());

        // Pasamos el ID del cargo a la vista para que el botón "Volver" sepa a dónde regresar.
        model.addAttribute("cargoId", cargoId);
        addRelacionesExternasBreadcrumbs(cargoId, "list", model);
        return "adm-cargo-edit-relaciones-externas-list";
    }

    /**
     * Muestra el formulario para crear una NUEVA relación externa.
     */
    @GetMapping("/adm/new")
    public String showNewFormadm(@RequestParam(value = "cargoId", required = false) Long cargoId, Model model) {
        model.addAttribute("relacionExterna", new RelacionExterna());
        model.addAttribute("isNew", true);
        model.addAttribute("cargoId", cargoId); // Pasar cargoId al modelo
        addRelacionesExternasBreadcrumbs(cargoId, "Nueva", model);
        return "adm-cargo-edit-relaciones-externas-form";
    }

    /**
     * Muestra el formulario para EDITAR una relación existente.
     */
    @GetMapping("/adm/edit/{id}")
    public String showEditFormadm(@PathVariable("id") Long id,
                                  @RequestParam(value = "cargoId", required = false) Long cargoId,
                                  Model model) {
        RelacionExterna relacion = relacionExternaService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Relación no encontrada"));
        model.addAttribute("relacionExterna", relacion);
        model.addAttribute("isNew", false);
        model.addAttribute("cargoId", cargoId); // Pasar cargoId al modelo
        addRelacionesExternasBreadcrumbs(cargoId, "Editar", model);
        return "adm-cargo-edit-relaciones-externas-form";
    }

    /**
     * Guarda una relación nueva o actualizada.
     */
    @PostMapping("/adm/save")
    public String saveOrUpdateRelacionadm(@Valid @ModelAttribute("relacionExterna") RelacionExterna relacionExterna,
                                          BindingResult result,
                                          @RequestParam(value = "cargoId", required = false) Long cargoId,
                                          Model model) {
        if (result.hasErrors()) {
            model.addAttribute("isNew", relacionExterna.getId() == null);
            model.addAttribute("cargoId", cargoId); // Asegurar que cargoId esté disponible en caso de error
            addRelacionesExternasBreadcrumbs(cargoId, relacionExterna.getId() == null ? "Nueva" : "Editar", model);
            return "adm-cargo-edit-relaciones-externas-form";
        }

        relacionExternaService.save(relacionExterna);

        if (cargoId != null) {
            // Redirige a la lista de relaciones externas con el cargoId
            return "redirect:/relaciones-externas?fromCargoId=" + cargoId;
        } else {
            // Si no hay cargoId, redirige a la lista general
            return "redirect:/relaciones-externas";
        }
    }

    /**
     * Elimina una relación externa.
     */
    @GetMapping("/adm/delete/{id}")
    public String deleteRelacionadm(@PathVariable("id") Long id) {
        relacionExternaService.deleteById(id);
        return "redirect:/relaciones-externas"; // Redirige a la lista principal
    }
}