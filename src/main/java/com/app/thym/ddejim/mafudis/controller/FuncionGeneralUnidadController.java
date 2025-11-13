package com.app.thym.ddejim.mafudis.controller;

import com.app.thym.ddejim.mafudis.Service.FuncionGeneralUnidadService;
import com.app.thym.ddejim.mafudis.Service.UnidadService;
import com.app.thym.ddejim.mafudis.dto.Breadcrumb;
import com.app.thym.ddejim.mafudis.model.FuncionGeneralUnidad;
import com.app.thym.ddejim.mafudis.model.Unidad;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/funciones-generales-unidad")
public class FuncionGeneralUnidadController {

    private static final Logger logger = LoggerFactory.getLogger(FuncionGeneralUnidadController.class);

    @Autowired
    private FuncionGeneralUnidadService funcionGeneralUnidadService;

    @Autowired
    private UnidadService unidadService;

    private void addFuncionGeneralUnidadBreadcrumbs(Long unidadId, Model model, String... pathSegments) {
        List<Breadcrumb> breadcrumbs = new ArrayList<>();
        breadcrumbs.add(new Breadcrumb("Inicio", "/dashboard"));
        breadcrumbs.add(new Breadcrumb("Organigrama de Unidades", "/organigrama13"));
        breadcrumbs.add(new Breadcrumb("Detalle de Unidad", "/adm/unidades/details/" + unidadId));

        for (int i = 0; i < pathSegments.length; i++) {
            String label = pathSegments[i];
            String url = null;

            if (i < pathSegments.length - 1 && label.equals("Funciones de Unidad")) {
                url = "/funciones-generales-unidad/adm/unidad/" + unidadId;
            }
            breadcrumbs.add(new Breadcrumb(label, url));
        }
        model.addAttribute("breadcrumbs", breadcrumbs);
    }
    @GetMapping("/unidad/{unidadId}")
    public String listFuncionesPorUnidad(@PathVariable Long unidadId, Model model) {
        logger.info("Listing funciones for unidadId: {}", unidadId);
        try {
            Unidad unidad = unidadService.findById(unidadId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unidad no encontrada: " + unidadId));
            List<FuncionGeneralUnidad> funciones = funcionGeneralUnidadService.findByUnidadId(unidadId);
            funciones.sort((f1, f2) -> Integer.compare(f1.getOrderIndex(), f2.getOrderIndex()));

            model.addAttribute("unidad", unidad);
            model.addAttribute("funciones", funciones);
            return "funciones-generales-unidad/list";
        } catch (Exception e) {
            logger.error("Error listing funciones for unidadId {}: {}", unidadId, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al listar las funciones", e);
        }
    }

    @GetMapping("/unidad/{unidadId}/new")
    public String showNewFuncionForm(@PathVariable Long unidadId, Model model) {
        logger.info("Showing new funcion form for unidadId: {}", unidadId);
        try {
            Unidad unidad = unidadService.findById(unidadId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unidad no encontrada: " + unidadId));
            FuncionGeneralUnidad funcion = new FuncionGeneralUnidad();
            funcion.setUnidad(unidad);
            model.addAttribute("unidad", unidad);
            model.addAttribute("funcion", funcion);
            return "funciones-generales-unidad/form";
        } catch (Exception e) {
            logger.error("Error showing new funcion form for unidadId {}: {}", unidadId, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al mostrar el formulario", e);
        }
    }

    @PostMapping("/unidad/{unidadId}")
    public String saveFuncion(@PathVariable Long unidadId,
                              @Valid @ModelAttribute("funcion") FuncionGeneralUnidad funcion,
                              BindingResult result, Model model) {
        logger.info("Saving new funcion for unidadId: {}", unidadId);
        try {
            Unidad unidad = unidadService.findById(unidadId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unidad no encontrada: " + unidadId));
            funcion.setUnidad(unidad);

            if (result.hasErrors()) {
                model.addAttribute("unidad", unidad);
                return "funciones-generales-unidad/form";
            }

            funcionGeneralUnidadService.save(funcion);
            return "redirect:/funciones-generales-unidad/unidad/" + unidadId;
        } catch (Exception e) {
            logger.error("Error saving funcion for unidadId {}: {}", unidadId, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al guardar la funcion", e);
        }
    }

    @GetMapping("/unidad/{unidadId}/edit/{id}")
    public String showEditFuncionForm(@PathVariable Long unidadId, @PathVariable Long id, Model model) {
        logger.info("Showing edit form for funcionId: {} in unidadId: {}", id, unidadId);
        try {
            Unidad unidad = unidadService.findById(unidadId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unidad no encontrada: " + unidadId));
            FuncionGeneralUnidad funcion = funcionGeneralUnidadService.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Funcion no encontrada: " + id));
            model.addAttribute("unidad", unidad);
            model.addAttribute("funcion", funcion);
            return "funciones-generales-unidad/form";
        } catch (Exception e) {
            logger.error("Error showing edit form for funcionId {} in unidadId {}: {}", id, unidadId, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al mostrar el formulario de edición", e);
        }
    }

    @PostMapping("/unidad/{unidadId}/edit/{id}")
    public String updateFuncion(@PathVariable Long unidadId, @PathVariable Long id,
                                @Valid @ModelAttribute("funcion") FuncionGeneralUnidad funcion,
                                BindingResult result, Model model) {
        logger.info("Updating funcionId: {} for unidadId: {}. Funcion: {}", id, unidadId, funcion);
        try {
            Unidad unidad = unidadService.findById(unidadId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unidad no encontrada: " + unidadId));

            FuncionGeneralUnidad existingFuncion = funcionGeneralUnidadService.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Funcion no encontrada: " + id));
            existingFuncion.setDescripcion(funcion.getDescripcion());
            existingFuncion.setUnidad(unidad);

            if (result.hasErrors()) {
                logger.error("Validation errors: {}", result.getAllErrors());
                model.addAttribute("unidad", unidad);
                return "funciones-generales-unidad/form";
            }

            logger.debug("Saving updated funcion: {}", existingFuncion);
            FuncionGeneralUnidad savedFuncion = funcionGeneralUnidadService.save(existingFuncion);
            logger.debug("Saved funcion: {}", savedFuncion);
            return "redirect:/funciones-generales-unidad/unidad/" + unidadId;
        } catch (Exception e) {
            logger.error("Error updating funcionId {} for unidadId {}: {}", id, unidadId, e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al actualizar la funcion", e);
        }
    }

    @GetMapping("/unidad/{unidadId}/delete/{id}")
    public String deleteFuncion(@PathVariable Long unidadId, @PathVariable Long id) {
        logger.info("Deleting funcionId: {} from unidadId: {}", id, unidadId);
        try {
            funcionGeneralUnidadService.deleteById(id);
            return "redirect:/funciones-generales-unidad/unidad/" + unidadId;
        } catch (Exception e) {
            logger.error("Error deleting funcionId {} from unidadId {}: {}", id, unidadId, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al eliminar la funcion", e);
        }
    }

    @PostMapping("/unidad/{unidadId}/reorder")
    @ResponseBody
    public ResponseEntity<?> reorderFunciones(@PathVariable Long unidadId, @RequestBody List<Long> orderedIds) {
        logger.info("Reordering funciones for unidadId: {}. Ordered IDs: {}", unidadId, orderedIds);
        try {
            funcionGeneralUnidadService.updateOrder(unidadId, orderedIds);
            return ResponseEntity.ok("Orden actualizado correctamente.");
        } catch (Exception e) {
            logger.error("Error reordering funciones for unidadId {}: {}", unidadId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar el orden: " + e.getMessage());
        }
    }


    @GetMapping("/adm/unidad/{unidadId}")
    public String listFuncionesPorUnidadadm(@PathVariable Long unidadId, Model model) {
        logger.info("Listing funciones for unidadId: {}", unidadId);
        try {
            Unidad unidad = unidadService.findById(unidadId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unidad no encontrada: " + unidadId));
            List<FuncionGeneralUnidad> funciones = funcionGeneralUnidadService.findByUnidadId(unidadId);
            funciones.sort((f1, f2) -> Integer.compare(f1.getOrderIndex(), f2.getOrderIndex()));

            model.addAttribute("unidad", unidad);
            model.addAttribute("funciones", funciones);
            model.addAttribute("backUrl", "/adm/unidades/details/" + unidadId);

            addFuncionGeneralUnidadBreadcrumbs(unidadId, model, "Funciones de Unidad");
            return "adm-unidad-edit-funciones-list";
        } catch (Exception e) {
            logger.error("Error listing funciones for unidadId {}: {}", unidadId, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al listar las funciones", e);
        }
    }

    @GetMapping("/adm/unidad/{unidadId}/new")
    public String showNewFuncionFormadm(@PathVariable Long unidadId, Model model) {
        logger.info("Showing new funcion form for unidadId: {}", unidadId);
        try {
            Unidad unidad = unidadService.findById(unidadId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unidad no encontrada: " + unidadId));
            FuncionGeneralUnidad funcion = new FuncionGeneralUnidad();
            funcion.setUnidad(unidad);
            model.addAttribute("unidad", unidad);
            model.addAttribute("funcion", funcion);
            addFuncionGeneralUnidadBreadcrumbs(unidadId, model, "Funciones de Unidad", "Nueva");
            return "adm-unidad-edit-funciones-form";
        } catch (Exception e) {
            logger.error("Error showing new funcion form for unidadId {}: {}", unidadId, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al mostrar el formulario", e);
        }
    }

    @PostMapping("/adm/unidad/{unidadId}")
    public String saveFuncionadm(@PathVariable Long unidadId,
                              @Valid @ModelAttribute("funcion") FuncionGeneralUnidad funcion,
                              BindingResult result, Model model) {
        logger.info("Saving new funcion for unidadId: {}", unidadId);
        try {
            Unidad unidad = unidadService.findById(unidadId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unidad no encontrada: " + unidadId));
            funcion.setUnidad(unidad);

            if (result.hasErrors()) {
                model.addAttribute("unidad", unidad);
                String currentPage = funcion.getId() == null ? "Nueva Función" : "Editar Función";
                addFuncionGeneralUnidadBreadcrumbs(unidadId, model, "Funciones de Unidad", currentPage);
                return "adm-unidad-edit-funciones-form";
            }

            funcionGeneralUnidadService.save(funcion);
            return "redirect:/funciones-generales-unidad/adm/unidad/" + unidadId;
        } catch (Exception e) {
            logger.error("Error saving funcion for unidadId {}: {}", unidadId, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al guardar la funcion", e);
        }
    }

    @GetMapping("/adm/unidad/{unidadId}/edit/{id}")
    public String showEditFuncionFormadm(@PathVariable Long unidadId, @PathVariable Long id, Model model) {
        logger.info("Showing edit form for funcionId: {} in unidadId: {}", id, unidadId);
        try {
            Unidad unidad = unidadService.findById(unidadId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unidad no encontrada: " + unidadId));
            FuncionGeneralUnidad funcion = funcionGeneralUnidadService.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Funcion no encontrada: " + id));
            model.addAttribute("unidad", unidad);
            model.addAttribute("funcion", funcion);
            addFuncionGeneralUnidadBreadcrumbs(unidadId, model, "Funciones de Unidad", "Editar");
            return "adm-unidad-edit-funciones-form";
        } catch (Exception e) {
            logger.error("Error showing edit form for funcionId {} in unidadId {}: {}", id, unidadId, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al mostrar el formulario de edición", e);
        }
    }

    @PostMapping("/adm/unidad/{unidadId}/edit/{id}")
    public String updateFuncionadm(@PathVariable Long unidadId, @PathVariable Long id,
                                @Valid @ModelAttribute("funcion") FuncionGeneralUnidad funcion,
                                BindingResult result, Model model) {
        logger.info("Updating funcionId: {} for unidadId: {}. Funcion: {}", id, unidadId, funcion);
        try {
            Unidad unidad = unidadService.findById(unidadId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unidad no encontrada: " + unidadId));

            FuncionGeneralUnidad existingFuncion = funcionGeneralUnidadService.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Funcion no encontrada: " + id));
            existingFuncion.setDescripcion(funcion.getDescripcion());
            existingFuncion.setUnidad(unidad);

            if (result.hasErrors()) {
                logger.error("Validation errors: {}", result.getAllErrors());
                model.addAttribute("unidad", unidad);
                return "adm-unidad-edit-funciones-form";
            }

            logger.debug("Saving updated funcion: {}", existingFuncion);
            FuncionGeneralUnidad savedFuncion = funcionGeneralUnidadService.save(existingFuncion);
            logger.debug("Saved funcion: {}", savedFuncion);
            return "redirect:/funciones-generales-unidad/adm/unidad/" + unidadId;
        } catch (Exception e) {
            logger.error("Error updating funcionId {} for unidadId {}: {}", id, unidadId, e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al actualizar la funcion", e);
        }
    }

    @GetMapping("/adm/unidad/{unidadId}/delete/{id}")
    public String deleteFuncionadm(@PathVariable Long unidadId, @PathVariable Long id) {
        logger.info("Deleting funcionId: {} from unidadId: {}", id, unidadId);
        try {
            funcionGeneralUnidadService.deleteById(id);
            return "redirect:/funciones-generales-unidad/adm/unidad/" + unidadId;
        } catch (Exception e) {
            logger.error("Error deleting funcionId {} from unidadId {}: {}", id, unidadId, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al eliminar la funcion", e);
        }
    }

    @PostMapping("/adm/unidad/{unidadId}/reorder")
    @ResponseBody
    public ResponseEntity<?> reorderFuncionesadm(@PathVariable Long unidadId, @RequestBody List<Long> orderedIds) {
        logger.info("Reordering funciones for unidadId: {}. Ordered IDs: {}", unidadId, orderedIds);
        try {
            funcionGeneralUnidadService.updateOrder(unidadId, orderedIds);
            return ResponseEntity.ok("Orden actualizado correctamente.");
        } catch (Exception e) {
            logger.error("Error reordering funciones for unidadId {}: {}", unidadId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar el orden: " + e.getMessage());
        }
    }


}