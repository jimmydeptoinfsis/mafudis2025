package com.app.thym.ddejim.mafudis.controller;

import com.app.thym.ddejim.mafudis.Service.ObjetivoUnidadService;
import com.app.thym.ddejim.mafudis.Service.UnidadService;
import com.app.thym.ddejim.mafudis.dto.Breadcrumb;
import com.app.thym.ddejim.mafudis.model.ObjetivoUnidad;
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
@RequestMapping("/objetivos-unidad")
public class ObjetivoUnidadController {

    private static final Logger logger = LoggerFactory.getLogger(ObjetivoUnidadController.class);

    @Autowired
    private ObjetivoUnidadService objetivoUnidadService;

    @Autowired
    private UnidadService unidadService;

    private void addObjetivoUnidadBreadcrumbs(Long unidadId, Model model, String... pathSegments) {
        List<Breadcrumb> breadcrumbs = new ArrayList<>();
        breadcrumbs.add(new Breadcrumb("Inicio", "/dashboard"));
        breadcrumbs.add(new Breadcrumb("Organigrama de Unidades", "/organigrama13"));
        breadcrumbs.add(new Breadcrumb("Detalle de Unidad", "/adm/unidades/details/" + unidadId));

        // Recorre los segmentos de la ruta
        for (int i = 0; i < pathSegments.length; i++) {
            String label = pathSegments[i];
            String url = null; // Por defecto, el último eslabón no tiene URL

            // Si no es el último eslabón, debe tener una URL para poder volver
            if (i < pathSegments.length - 1) {
                // Asigna la URL correspondiente a la página de "Objetivos de Unidad"
                if (label.equals("Objetivos de Unidad")) {
                    url = "/objetivos-unidad/adm/unidad/" + unidadId;
                }
            }
            breadcrumbs.add(new Breadcrumb(label, url));
        }
        model.addAttribute("breadcrumbs", breadcrumbs);
    }
    @GetMapping("/unidad/{unidadId}")
    public String listObjetivosPorUnidad(@PathVariable Long unidadId, Model model) {
        logger.info("Listing objetivos for unidadId: {}", unidadId);
        try {
            Unidad unidad = unidadService.findById(unidadId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unidad no encontrada: " + unidadId));
            List<ObjetivoUnidad> objetivos = objetivoUnidadService.findByUnidadId(unidadId);
            objetivos.sort((o1, o2) -> Integer.compare(o1.getOrderIndex(), o2.getOrderIndex()));

            model.addAttribute("unidad", unidad);
            model.addAttribute("objetivos", objetivos);
            return "objetivos-unidad/list";
        } catch (Exception e) {
            logger.error("Error listing objetivos for unidadId {}: {}", unidadId, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al listar los objetivos", e);
        }
    }

    @GetMapping("/unidad/{unidadId}/new")
    public String showNewObjetivoForm(@PathVariable Long unidadId, Model model) {
        logger.info("Showing new objetivo form for unidadId: {}", unidadId);
        try {
            Unidad unidad = unidadService.findById(unidadId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unidad no encontrada: " + unidadId));
            ObjetivoUnidad objetivo = new ObjetivoUnidad();
            objetivo.setUnidad(unidad);
            model.addAttribute("unidad", unidad);
            model.addAttribute("objetivo", objetivo);
            return "objetivos-unidad/form";
        } catch (Exception e) {
            logger.error("Error showing new objetivo form for unidadId {}: {}", unidadId, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al mostrar el formulario", e);
        }
    }

    @PostMapping("/unidad/{unidadId}")
    public String saveObjetivo(@PathVariable Long unidadId,
                               @Valid @ModelAttribute("objetivo") ObjetivoUnidad objetivo,
                               BindingResult result, Model model) {
        logger.info("Saving new objetivo for unidadId: {}", unidadId);
        try {
            Unidad unidad = unidadService.findById(unidadId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unidad no encontrada: " + unidadId));
            objetivo.setUnidad(unidad);

            if (result.hasErrors()) {
                model.addAttribute("unidad", unidad);
                return "objetivos-unidad/form";
            }

            objetivoUnidadService.save(objetivo);
            return "redirect:/objetivos-unidad/unidad/" + unidadId;
        } catch (Exception e) {
            logger.error("Error saving objetivo for unidadId {}: {}", unidadId, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al guardar el objetivo", e);
        }
    }

    @GetMapping("/unidad/{unidadId}/edit/{id}")
    public String showEditObjetivoForm(@PathVariable Long unidadId, @PathVariable Long id, Model model) {
        logger.info("Showing edit form for objetivoId: {} in unidadId: {}", id, unidadId);
        try {
            Unidad unidad = unidadService.findById(unidadId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unidad no encontrada: " + unidadId));
            ObjetivoUnidad objetivo = objetivoUnidadService.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Objetivo no encontrado: " + id));
            model.addAttribute("unidad", unidad);
            model.addAttribute("objetivo", objetivo);
            return "objetivos-unidad/form";
        } catch (Exception e) {
            logger.error("Error showing edit form for objetivoId {} in unidadId {}: {}", id, unidadId, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al mostrar el formulario de edición", e);
        }
    }

    @PostMapping("/unidad/{unidadId}/edit/{id}")
    public String updateObjetivo(@PathVariable Long unidadId, @PathVariable Long id,
                                 @Valid @ModelAttribute("objetivo") ObjetivoUnidad objetivo,
                                 BindingResult result, Model model) {
        logger.info("Updating objetivoId: {} for unidadId: {}. Objetivo: {}", id, unidadId, objetivo);
        try {
            Unidad unidad = unidadService.findById(unidadId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unidad no encontrada: " + unidadId));

            ObjetivoUnidad existingObjetivo = objetivoUnidadService.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Objetivo no encontrado: " + id));
            existingObjetivo.setDescripcion(objetivo.getDescripcion());
            existingObjetivo.setUnidad(unidad);

            if (result.hasErrors()) {
                logger.error("Validation errors: {}", result.getAllErrors());
                model.addAttribute("unidad", unidad);
                return "objetivos-unidad/form";
            }

            logger.debug("Saving updated objetivo: {}", existingObjetivo);
            ObjetivoUnidad savedObjetivo = objetivoUnidadService.save(existingObjetivo);
            logger.debug("Saved objetivo: {}", savedObjetivo);
            return "redirect:/objetivos-unidad/unidad/" + unidadId;
        } catch (Exception e) {
            logger.error("Error updating objetivoId {} for unidadId {}: {}", id, unidadId, e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al actualizar el objetivo", e);
        }
    }

    @GetMapping("/unidad/{unidadId}/delete/{id}")
    public String deleteObjetivo(@PathVariable Long unidadId, @PathVariable Long id) {
        logger.info("Deleting objetivoId: {} from unidadId: {}", id, unidadId);
        try {
            objetivoUnidadService.deleteById(id);
            return "redirect:/objetivos-unidad/unidad/" + unidadId;
        } catch (Exception e) {
            logger.error("Error deleting objetivoId {} from unidadId {}: {}", id, unidadId, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al eliminar el objetivo", e);
        }
    }

    @PostMapping("/unidad/{unidadId}/reorder")
    @ResponseBody
    public ResponseEntity<?> reorderObjetivos(@PathVariable Long unidadId, @RequestBody List<Long> orderedIds) {
        logger.info("Reordering objetivos for unidadId: {}. Ordered IDs: {}", unidadId, orderedIds);
        try {
            objetivoUnidadService.updateOrder(unidadId, orderedIds);
            return ResponseEntity.ok("Orden actualizado correctamente.");
        } catch (Exception e) {
            logger.error("Error reordering objetivos for unidadId {}: {}", unidadId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar el orden: " + e.getMessage());
        }
    }

    @GetMapping("/adm/unidad/{unidadId}")
    public String listObjetivosadm(@PathVariable Long unidadId, Model model) {
        Unidad unidad = unidadService.findById(unidadId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unidad no encontrada"));

        // CORRECCIÓN: Usa el método que garantiza el orden desde el principio
        List<ObjetivoUnidad> objetivos = objetivoUnidadService.findByUnidadIdOrdered(unidadId);

        model.addAttribute("unidad", unidad);
        model.addAttribute("objetivos", objetivos);
        addObjetivoUnidadBreadcrumbs(unidadId, model, "Objetivos de Unidad");
        model.addAttribute("backUrl", "/adm/unidades/details/" + unidadId);
        return "adm-unidad-edit-objetivos-list";
    }

    @GetMapping("/adm/unidad/{unidadId}/new")
    public String showNewObjetivoFormadm(@PathVariable Long unidadId, Model model) {
        logger.info("Showing new objetivo form for unidadId: {}", unidadId);
        try {
            Unidad unidad = unidadService.findById(unidadId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unidad no encontrada: " + unidadId));
            ObjetivoUnidad objetivo = new ObjetivoUnidad();
            objetivo.setUnidad(unidad);
            model.addAttribute("unidad", unidad);
            model.addAttribute("objetivo", objetivo);
            addObjetivoUnidadBreadcrumbs(unidadId, model, "Objetivos de Unidad", "Nuevo");

            return "adm-unidad-edit-objetivos-form";
        } catch (Exception e) {
            logger.error("Error showing new objetivo form for unidadId {}: {}", unidadId, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al mostrar el formulario", e);
        }
    }

    @PostMapping("/adm/unidad/{unidadId}")
    public String saveObjetivoadm(@PathVariable Long unidadId,
                               @Valid @ModelAttribute("objetivo") ObjetivoUnidad objetivo,
                               BindingResult result, Model model) {
        logger.info("Saving new objetivo for unidadId: {}", unidadId);
        try {
            Unidad unidad = unidadService.findById(unidadId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unidad no encontrada: " + unidadId));
            objetivo.setUnidad(unidad);

            if (result.hasErrors()) {
                model.addAttribute("unidad", unidad);
                String currentPage = objetivo.getId() == null ? "Nuevo Objetivo" : "Editar Objetivo";
                addObjetivoUnidadBreadcrumbs(unidadId, model, "Objetivos de Unidad", currentPage);

                return "adm-unidad-edit-objetivos-form";
            }

            objetivoUnidadService.save(objetivo);
            return "redirect:/objetivos-unidad/adm/unidad/" + unidadId;
        } catch (Exception e) {
            logger.error("Error saving objetivo for unidadId {}: {}", unidadId, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al guardar el objetivo", e);
        }
    }

    @GetMapping("/adm/unidad/{unidadId}/edit/{id}")
    public String showEditObjetivoFormadm(@PathVariable Long unidadId, @PathVariable Long id, Model model) {
        logger.info("Showing edit form for objetivoId: {} in unidadId: {}", id, unidadId);
        try {
            Unidad unidad = unidadService.findById(unidadId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unidad no encontrada: " + unidadId));
            ObjetivoUnidad objetivo = objetivoUnidadService.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Objetivo no encontrado: " + id));
            model.addAttribute("unidad", unidad);
            model.addAttribute("objetivo", objetivo);
            addObjetivoUnidadBreadcrumbs(unidadId, model, "Objetivos de Unidad", "Editar");

            return "adm-unidad-edit-objetivos-form";
        } catch (Exception e) {
            logger.error("Error showing edit form for objetivoId {} in unidadId {}: {}", id, unidadId, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al mostrar el formulario de edición", e);
        }
    }

    @PostMapping("/adm/unidad/{unidadId}/edit/{id}")
    public String updateObjetivoadm(@PathVariable Long unidadId, @PathVariable Long id,
                                 @Valid @ModelAttribute("objetivo") ObjetivoUnidad objetivo,
                                 BindingResult result, Model model) {
        logger.info("Updating objetivoId: {} for unidadId: {}. Objetivo: {}", id, unidadId, objetivo);
        try {
            Unidad unidad = unidadService.findById(unidadId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unidad no encontrada: " + unidadId));

            ObjetivoUnidad existingObjetivo = objetivoUnidadService.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Objetivo no encontrado: " + id));
            existingObjetivo.setDescripcion(objetivo.getDescripcion());
            existingObjetivo.setUnidad(unidad);

            if (result.hasErrors()) {
                logger.error("Validation errors: {}", result.getAllErrors());
                model.addAttribute("unidad", unidad);
                return "adm-unidad-edit-objetivos-form";
            }

            logger.debug("Saving updated objetivo: {}", existingObjetivo);
            ObjetivoUnidad savedObjetivo = objetivoUnidadService.save(existingObjetivo);
            logger.debug("Saved objetivo: {}", savedObjetivo);
            return "redirect:/objetivos-unidad/adm/unidad/" + unidadId;
        } catch (Exception e) {
            logger.error("Error updating objetivoId {} for unidadId {}: {}", id, unidadId, e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al actualizar el objetivo", e);
        }
    }

    @GetMapping("/adm/unidad/{unidadId}/delete/{id}")
    public String deleteObjetivoadm(@PathVariable Long unidadId, @PathVariable Long id) {
        logger.info("Deleting objetivoId: {} from unidadId: {}", id, unidadId);
        try {
            objetivoUnidadService.deleteById(id);
            return "redirect:/objetivos-unidad/adm/unidad/" + unidadId;
        } catch (Exception e) {
            logger.error("Error deleting objetivoId {} from unidadId {}: {}", id, unidadId, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al eliminar el objetivo", e);
        }
    }

    @PostMapping("/adm/unidad/{unidadId}/reorder")
    @ResponseBody
    public ResponseEntity<?> reorderObjetivosadm(@PathVariable Long unidadId, @RequestBody List<Long> orderedIds) {
        logger.info("Reordering objetivos for unidadId: {}. Ordered IDs: {}", unidadId, orderedIds);
        try {
            objetivoUnidadService.updateOrder(unidadId, orderedIds);
            return ResponseEntity.ok("Orden actualizado correctamente.");
        } catch (Exception e) {
            logger.error("Error reordering objetivos for unidadId {}: {}", unidadId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar el orden: " + e.getMessage());
        }
    }
}