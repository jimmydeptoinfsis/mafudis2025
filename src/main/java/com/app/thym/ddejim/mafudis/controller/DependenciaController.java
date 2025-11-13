package com.app.thym.ddejim.mafudis.controller;

import com.app.thym.ddejim.mafudis.Service.DependenciaService;
import com.app.thym.ddejim.mafudis.Service.UnidadService;
import com.app.thym.ddejim.mafudis.dto.Breadcrumb;
import com.app.thym.ddejim.mafudis.model.Dependencia;
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
@RequestMapping("/dependencias")
public class DependenciaController {

    private static final Logger logger = LoggerFactory.getLogger(DependenciaController.class);

    @Autowired
    private DependenciaService dependenciaService;
    @Autowired
    private UnidadService unidadService;

    private void addDependenciaBreadcrumbs(Long unidadId, String currentPageLabel, Model model) {
        List<Breadcrumb> breadcrumbs = new ArrayList<>();
        breadcrumbs.add(new Breadcrumb("Inicio", "/dashboard"));

        if (unidadId != null) {
            Unidad unidad = unidadService.findById(unidadId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unidad no encontrada"));

            breadcrumbs.add(new Breadcrumb("Organigrama de unidades", "/organigrama13"));
            breadcrumbs.add(new Breadcrumb("Detalle de Unidad", "/adm/unidades/details/" + unidadId));
            breadcrumbs.add(new Breadcrumb("Editar Dependencia", "/adm/unidades/" + unidadId + "/edit-dependencia"));

            if ("list".equals(currentPageLabel)) {
                breadcrumbs.add(new Breadcrumb("Gestionar Catálogo de Dependencias de Unidad", null));
            } else if (currentPageLabel != null) {
                breadcrumbs.add(new Breadcrumb("Gestionar Catálogo de Dependencias de Unidad", "/dependencias/adm/list?fromUnidadId=" + unidadId));
                breadcrumbs.add(new Breadcrumb(currentPageLabel, null));
            }
        } else {
            breadcrumbs.add(new Breadcrumb("Gestionar Dependencias", null));
        }

        model.addAttribute("breadcrumbs", breadcrumbs);

        String backUrl = "/dashboard";
        if (breadcrumbs.size() > 1) {
            backUrl = breadcrumbs.get(breadcrumbs.size() - 2).getUrl();
        }
        model.addAttribute("backUrl", backUrl);
    }
    @GetMapping("/list")
    public String listDependencias(Model model) {
        logger.info("Listing all dependencias");
        try {
            model.addAttribute("dependencias", dependenciaService.findAll());
            return "dependencias/list";
        } catch (Exception e) {
            logger.error("Error listing dependencias: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al listar las dependencias", e);
        }
    }

    @GetMapping("/new")
    public String showNewDependenciaForm(Model model) {
        logger.info("Showing new dependencia form");
        model.addAttribute("dependencia", new Dependencia());
        return "dependencias/form";
    }

    @PostMapping("/save")
    public String saveDependencia(@Valid @ModelAttribute("dependencia") Dependencia dependencia,
                                  BindingResult result, Model model) {
        logger.info("Saving dependencia: {}", dependencia);
        try {
            if (result.hasErrors()) {
                logger.error("Validation errors: {}", result.getAllErrors());
                return "dependencias/form";
            }
            dependenciaService.save(dependencia);
            return "redirect:/dependencias/list";
        } catch (Exception e) {
            logger.error("Error saving dependencia: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al guardar la dependencia", e);
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditDependenciaForm(@PathVariable Long id, Model model) {
        logger.info("Showing edit form for dependenciaId: {}", id);
        try {
            Dependencia dependencia = dependenciaService.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dependencia no encontrada: " + id));
            model.addAttribute("dependencia", dependencia);
            return "dependencias/form";
        } catch (Exception e) {
            logger.error("Error showing edit form for dependenciaId {}: {}", id, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al mostrar el formulario de edición", e);
        }
    }

    @PostMapping("/edit/{id}")
    public String updateDependencia(@PathVariable Long id,
                                    @Valid @ModelAttribute("dependencia") Dependencia dependencia,
                                    BindingResult result, Model model) {
        logger.info("Updating dependenciaId: {}. Dependencia: {}", id, dependencia);
        try {
            if (result.hasErrors()) {
                logger.error("Validation errors: {}", result.getAllErrors());
                return "dependencias/form";
            }

            Dependencia existingDependencia = dependenciaService.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dependencia no encontrada: " + id));
            existingDependencia.setNombre(dependencia.getNombre());
            dependenciaService.save(existingDependencia);
            return "redirect:/dependencias/list";
        } catch (Exception e) {
            logger.error("Error updating dependenciaId {}: {}", id, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al actualizar la dependencia", e);
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteDependencia(@PathVariable Long id) {
        logger.info("Deleting dependenciaId: {}", id);
        try {
            dependenciaService.deleteById(id);
            return "redirect:/dependencias/list";
        } catch (Exception e) {
            logger.error("Error deleting dependenciaId {}: {}", id, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al eliminar la dependencia", e);
        }
    }

    @GetMapping("/adm/list")
    public String listDependenciasadm(@RequestParam(name = "fromUnidadId", required = false) Long unidadId, Model model) {
        logger.info("Listing all dependencias");
        try {
            model.addAttribute("dependencias", dependenciaService.findAll());
            model.addAttribute("fromUnidadId", unidadId);
            addDependenciaBreadcrumbs(unidadId, "list", model);
            return "adm-unidad-edit-dependencia-list";
        } catch (Exception e) {
            logger.error("Error listing dependencias: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al listar las dependencias", e);
        }
    }

    @GetMapping("/adm/new")
    public String showNewDependenciaFormAdm(@RequestParam(name = "fromUnidadId", required = false) Long unidadId, Model model) {
        logger.info("Showing new dependencia form");
        model.addAttribute("dependencia", new Dependencia());
        model.addAttribute("fromUnidadId", unidadId);
        addDependenciaBreadcrumbs(unidadId, "Nueva", model);
        return "adm-unidad-edit-dependencia-form";
    }

    @PostMapping("/adm/save")
    public String saveDependenciaAdm(@Valid @ModelAttribute("dependencia") Dependencia dependencia,
                                     BindingResult result,
                                     @RequestParam(name = "fromUnidadId", required = false) Long unidadId,
                                     Model model) {
        logger.info("Saving dependencia: {}", dependencia);
        try {
            if (result.hasErrors()) {
                logger.error("Validation errors: {}", result.getAllErrors());
                model.addAttribute("fromUnidadId", unidadId);
                addDependenciaBreadcrumbs(unidadId, dependencia.getId() == null ? "Nueva" : "Editar", model);
                return "adm-unidad-edit-dependencia";
            }
            dependenciaService.save(dependencia);
            return "redirect:/dependencias/adm/list";
        } catch (Exception e) {
            logger.error("Error saving dependencia: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al guardar la dependencia", e);
        }
    }

    @GetMapping("/adm/edit/{id}")
    public String showEditDependenciaFormAdm(@PathVariable Long id, @RequestParam(name = "fromUnidadId", required = false) Long unidadId, Model model) {
        logger.info("Showing edit form for dependenciaId: {}", id);
        try {
            Dependencia dependencia = dependenciaService.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dependencia no encontrada: " + id));
            model.addAttribute("dependencia", dependencia);
            model.addAttribute("fromUnidadId", unidadId);
            addDependenciaBreadcrumbs(unidadId, "Editar", model);
            return "adm-unidad-edit-dependencia-form";
        } catch (Exception e) {
            logger.error("Error showing edit form for dependenciaId {}: {}", id, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al mostrar el formulario de edición", e);
        }
    }

    @PostMapping("/adm/edit/{id}")
    public String updateDependenciaadm(@PathVariable Long id,
                                    @Valid @ModelAttribute("dependencia") Dependencia dependencia,
                                    BindingResult result, Model model) {
        logger.info("Updating dependenciaId: {}. Dependencia: {}", id, dependencia);
        try {
            if (result.hasErrors()) {
                logger.error("Validation errors: {}", result.getAllErrors());
                return "adm-unidad-edit-dependencia-form";
            }

            Dependencia existingDependencia = dependenciaService.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dependencia no encontrada: " + id));
            existingDependencia.setNombre(dependencia.getNombre());
            dependenciaService.save(existingDependencia);
            return "redirect:/dependencias/adm/list";
        } catch (Exception e) {
            logger.error("Error updating dependenciaId {}: {}", id, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al actualizar la dependencia", e);
        }
    }

    @GetMapping("/adm/delete/{id}")
    public String deleteDependenciaAdm(@PathVariable Long id,
                                       @RequestParam(name = "fromUnidadId", required = false) Long unidadId,
                                       RedirectAttributes redirectAttributes) {
        try {
            dependenciaService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Dependencia eliminada correctamente.");
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("error", "No se puede eliminar: la dependencia está en uso.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ocurrió un error inesperado al eliminar.");
        }

        String redirectUrl = "/dependencias/adm/list";
        if (unidadId != null) {
            redirectUrl += "?fromUnidadId=" + unidadId;
        }
        return "redirect:" + redirectUrl;
    }
}
