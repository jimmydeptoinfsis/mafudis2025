package com.app.thym.ddejim.mafudis.controller;

import com.app.thym.ddejim.mafudis.Service.ClasificacionUnidadService;
import com.app.thym.ddejim.mafudis.Service.UnidadService;
import com.app.thym.ddejim.mafudis.dto.Breadcrumb;
import com.app.thym.ddejim.mafudis.model.ClasificacionUnidad;
import com.app.thym.ddejim.mafudis.model.Unidad;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/clasificaciones-unidad")
public class ClasificacionUnidadController {

    private static final Logger logger = LoggerFactory.getLogger(ClasificacionUnidadController.class);

    @Autowired
    private ClasificacionUnidadService clasificacionUnidadService;
    @Autowired
    private UnidadService unidadService;
    @GetMapping("/list")
    public String listClasificacionesUnidad(Model model) {
        logger.info("Listing all clasificaciones de unidad");
        try {
            model.addAttribute("clasificacionesUnidad", clasificacionUnidadService.findAll());
            return "clasificaciones-unidad/list";
        } catch (Exception e) {
            logger.error("Error listing clasificaciones de unidad: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al listar las clasificaciones de unidad", e);
        }
    }

    @GetMapping("/new")
    public String showNewClasificacionUnidadForm(Model model) {
        logger.info("Showing new clasificacion unidad form");
        model.addAttribute("clasificacionUnidad", new ClasificacionUnidad());
        return "clasificaciones-unidad/form";
    }

    @PostMapping("/save")
    public String saveClasificacionUnidad(@Valid @ModelAttribute("clasificacionUnidad") ClasificacionUnidad clasificacionUnidad,
                                          BindingResult result, Model model) {
        logger.info("Saving clasificacion unidad: {}", clasificacionUnidad);
        try {
            if (result.hasErrors()) {
                logger.error("Validation errors: {}", result.getAllErrors());
                return "clasificaciones-unidad/form";
            }
            clasificacionUnidadService.save(clasificacionUnidad);
            return "redirect:/clasificaciones-unidad/list";
        } catch (Exception e) {
            logger.error("Error saving clasificacion unidad: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al guardar la clasificacion de unidad", e);
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditClasificacionUnidadForm(@PathVariable Long id, Model model) {
        logger.info("Showing edit form for clasificacionUnidadId: {}", id);
        try {
            ClasificacionUnidad clasificacionUnidad = clasificacionUnidadService.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Clasificacion de unidad no encontrada: " + id));
            model.addAttribute("clasificacionUnidad", clasificacionUnidad);
            return "clasificaciones-unidad/form";
        } catch (Exception e) {
            logger.error("Error showing edit form for clasificacionUnidadId {}: {}", id, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al mostrar el formulario de edición", e);
        }
    }

    @PostMapping("/edit/{id}")
    public String updateClasificacionUnidad(@PathVariable Long id,
                                            @Valid @ModelAttribute("clasificacionUnidad") ClasificacionUnidad clasificacionUnidad,
                                            BindingResult result, Model model) {
        logger.info("Updating clasificacionUnidadId: {}. ClasificacionUnidad: {}", id, clasificacionUnidad);
        try {
            if (result.hasErrors()) {
                logger.error("Validation errors: {}", result.getAllErrors());
                return "clasificaciones-unidad/form";
            }

            ClasificacionUnidad existingClasificacionUnidad = clasificacionUnidadService.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Clasificacion de unidad no encontrada: " + id));
            existingClasificacionUnidad.setNombre(clasificacionUnidad.getNombre());
            clasificacionUnidadService.save(existingClasificacionUnidad);
            return "redirect:/clasificaciones-unidad/list";
        } catch (Exception e) {
            logger.error("Error updating clasificacionUnidadId {}: {}", id, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al actualizar la clasificacion de unidad", e);
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteClasificacionUnidad(@PathVariable Long id) {
        logger.info("Deleting clasificacionUnidadId: {}", id);
        try {
            clasificacionUnidadService.deleteById(id);
            return "redirect:/clasificaciones-unidad/list";
        } catch (Exception e) {
            logger.error("Error deleting clasificacionUnidadId {}: {}", id, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al eliminar la clasificacion de unidad", e);
        }
    }
    private void addClasificacionUnidadBreadcrumbs(Long unidadId, String currentPageLabel, Model model) {
        List<Breadcrumb> breadcrumbs = new ArrayList<>();
        breadcrumbs.add(new Breadcrumb("Inicio", "/dashboard"));

        if (unidadId != null) {
            Unidad unidad = unidadService.findById(unidadId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unidad no encontrada"));

            breadcrumbs.add(new Breadcrumb("Organigrama de unidades", "/organigrama13"));
            breadcrumbs.add(new Breadcrumb("Detalle de Unidad", "/adm/unidades/details/" + unidadId));
            breadcrumbs.add(new Breadcrumb("Editar Clasificación", "/adm/unidades/" + unidadId + "/edit-clasificacion"));

            if ("list".equals(currentPageLabel)) {
                breadcrumbs.add(new Breadcrumb("Gestionar Catálogo de Clasificación de Unidad", null));
            } else if (currentPageLabel != null) {
                breadcrumbs.add(new Breadcrumb("Gestionar Catálogo de Clasificación de Unidad", "/clasificaciones-unidad/adm/list?fromUnidadId=" + unidadId));
                breadcrumbs.add(new Breadcrumb(currentPageLabel, null));
            }
        } else {
            breadcrumbs.add(new Breadcrumb("Gestionar Clasificaciones", null));
        }

        model.addAttribute("breadcrumbs", breadcrumbs);

        String backUrl = "/dashboard";
        if (breadcrumbs.size() > 1) {
            backUrl = breadcrumbs.get(breadcrumbs.size() - 2).getUrl();
        }
        model.addAttribute("backUrl", backUrl);
    }

    @GetMapping("/adm/list")
    public String listClasificacionesUnidadadm(@RequestParam(name = "fromUnidadId", required = false) Long unidadId, Model model) {
        logger.info("Listing all clasificaciones de unidad");
        try {
            model.addAttribute("clasificacionesUnidad", clasificacionUnidadService.findAll());
            model.addAttribute("fromUnidadId", unidadId);
            addClasificacionUnidadBreadcrumbs(unidadId, "list", model);
            return "adm-unidad-edit-clasificacion-list";
        } catch (Exception e) {
            logger.error("Error listing clasificaciones de unidad: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al listar las clasificaciones de unidad", e);
        }
    }

    @GetMapping("/adm/new")
    public String showNewClasificacionUnidadFormadm(@RequestParam(name = "fromUnidadId", required = false) Long unidadId, Model model) {
        logger.info("Showing new clasificacion unidad form");
        model.addAttribute("clasificacionUnidad", new ClasificacionUnidad());
        model.addAttribute("fromUnidadId", unidadId);
        addClasificacionUnidadBreadcrumbs(unidadId, "Nueva", model);
        return "adm-unidad-edit-clasificacion-form";
    }

    @PostMapping("/adm/save")
    public String saveClasificacionUnidadadm(@Valid @ModelAttribute("clasificacionUnidad") ClasificacionUnidad clasificacionUnidad,
                                             BindingResult result,
                                             @RequestParam(name = "fromUnidadId", required = false) Long unidadId,
                                             Model model) {
        logger.info("Saving clasificacion unidad: {}", clasificacionUnidad);
        try {
            if (result.hasErrors()) {
                model.addAttribute("fromUnidadId", unidadId);
                addClasificacionUnidadBreadcrumbs(unidadId, clasificacionUnidad.getId() == null ? "Nueva" : "Editar", model);
                logger.error("Validation errors: {}", result.getAllErrors());
                return "adm-unidad-edit-clasificacion";
            }
            clasificacionUnidadService.save(clasificacionUnidad);
            return "redirect:/clasificaciones-unidad/adm/list";
        } catch (Exception e) {
            logger.error("Error saving clasificacion unidad: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al guardar la clasificacion de unidad", e);
        }
    }

    @GetMapping("/adm/edit/{id}")
    public String showEditClasificacionUnidadFormadm(@PathVariable Long id, @RequestParam(name = "fromUnidadId", required = false) Long unidadId, Model model) {
        logger.info("Showing edit form for clasificacionUnidadId: {}", id);
        try {
            ClasificacionUnidad clasificacionUnidad = clasificacionUnidadService.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Clasificacion de unidad no encontrada: " + id));
            model.addAttribute("clasificacionUnidad", clasificacionUnidad);
            model.addAttribute("fromUnidadId", unidadId);
            addClasificacionUnidadBreadcrumbs(unidadId, "Editar", model);
            return "adm-unidad-edit-clasificacion-form";
        } catch (Exception e) {
            logger.error("Error showing edit form for clasificacionUnidadId {}: {}", id, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al mostrar el formulario de edición", e);
        }
    }

    @PostMapping("/adm/edit/{id}")
    public String updateClasificacionUnidadadm(@PathVariable Long id,
                                            @Valid @ModelAttribute("clasificacionUnidad") ClasificacionUnidad clasificacionUnidad,
                                            BindingResult result, Model model) {
        logger.info("Updating clasificacionUnidadId: {}. ClasificacionUnidad: {}", id, clasificacionUnidad);
        try {
            if (result.hasErrors()) {
                logger.error("Validation errors: {}", result.getAllErrors());
                return "adm-unidad-edit-clasificacion-form";
            }

            ClasificacionUnidad existingClasificacionUnidad = clasificacionUnidadService.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Clasificacion de unidad no encontrada: " + id));
            existingClasificacionUnidad.setNombre(clasificacionUnidad.getNombre());
            clasificacionUnidadService.save(existingClasificacionUnidad);
            return "redirect:/clasificaciones-unidad/adm/list";
        } catch (Exception e) {
            logger.error("Error updating clasificacionUnidadId {}: {}", id, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al actualizar la clasificacion de unidad", e);
        }
    }

    @GetMapping("/adm/delete/{id}")
    public String deleteClasificacionUnidadAdm(@PathVariable Long id,
                                               @RequestParam(name = "fromUnidadId", required = false) Long unidadId,
                                               RedirectAttributes redirectAttributes) {
        try {
            clasificacionUnidadService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Clasificación eliminada correctamente.");
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("error", "No se puede eliminar: la clasificación está en uso.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ocurrió un error inesperado al eliminar.");
        }

        String redirectUrl = "/clasificaciones-unidad/adm/list";
        if (unidadId != null) {
            redirectUrl += "?fromUnidadId=" + unidadId;
        }
        return "redirect:" + redirectUrl;
    }
}
