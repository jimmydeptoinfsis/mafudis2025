package com.app.thym.ddejim.mafudis.controller;

import com.app.thym.ddejim.mafudis.Service.NivelJerarquicoService;
import com.app.thym.ddejim.mafudis.Service.UnidadService;
import com.app.thym.ddejim.mafudis.dto.Breadcrumb;
import com.app.thym.ddejim.mafudis.model.NivelJerarquico;
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
@RequestMapping("/niveles-jerarquicos")
public class NivelJerarquicoController {

    private static final Logger logger = LoggerFactory.getLogger(NivelJerarquicoController.class);

    @Autowired
    private NivelJerarquicoService nivelJerarquicoService;
    @Autowired
    private UnidadService unidadService;

    private void addNivelJerarquicoBreadcrumbs(Long unidadId, String currentPageLabel, Model model) {
        List<Breadcrumb> breadcrumbs = new ArrayList<>();
        breadcrumbs.add(new Breadcrumb("Inicio", "/dashboard"));

        if (unidadId != null) {
            Unidad unidad = unidadService.findById(unidadId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unidad no encontrada"));

            breadcrumbs.add(new Breadcrumb("Organigrama de unidades", "/organigrama13"));
            breadcrumbs.add(new Breadcrumb("Detalle de Unidad", "/adm/unidades/details/" + unidadId));
            breadcrumbs.add(new Breadcrumb("Editar Nivel Jerárquico", "/adm/unidades/" + unidadId + "/edit-nivel-jerarquico"));

            if ("list".equals(currentPageLabel)) {
                breadcrumbs.add(new Breadcrumb("Gestionar Catálogo de Nivel Jerárquico de Unidad", null));
            } else if (currentPageLabel != null) {
                breadcrumbs.add(new Breadcrumb("Gestionar Catálogo de Nivel Jerárquico de Unidad", "/niveles-jerarquicos/adm/list?fromUnidadId=" + unidadId));
                breadcrumbs.add(new Breadcrumb(currentPageLabel, null));
            }
        } else {
            breadcrumbs.add(new Breadcrumb("Gestionar Niveles Jerárquicos", null));
        }

        model.addAttribute("breadcrumbs", breadcrumbs);

        String backUrl = "/dashboard";
        if (breadcrumbs.size() > 1) {
            backUrl = breadcrumbs.get(breadcrumbs.size() - 2).getUrl();
        }
        model.addAttribute("backUrl", backUrl);
    }
    @GetMapping("/list")
    public String listNivelesJerarquicos(Model model) {
        logger.info("Listing all niveles jerarquicos");
        try {
            model.addAttribute("nivelesJerarquicos", nivelJerarquicoService.findAll());
            return "niveles-jerarquicos/list";
        } catch (Exception e) {
            logger.error("Error listing niveles jerarquicos: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al listar los niveles jerarquicos", e);
        }
    }

    @GetMapping("/new")
    public String showNewNivelJerarquicoForm(Model model) {
        logger.info("Showing new nivel jerarquico form");
        model.addAttribute("nivelJerarquico", new NivelJerarquico());
        return "niveles-jerarquicos/form";
    }

    @PostMapping("/save")
    public String saveNivelJerarquico(@Valid @ModelAttribute("nivelJerarquico") NivelJerarquico nivelJerarquico,
                                      BindingResult result, Model model) {
        logger.info("Saving nivel jerarquico: {}", nivelJerarquico);
        try {
            if (result.hasErrors()) {
                logger.error("Validation errors: {}", result.getAllErrors());
                return "niveles-jerarquicos/form";
            }
            nivelJerarquicoService.save(nivelJerarquico);
            return "redirect:/niveles-jerarquicos/list";
        } catch (Exception e) {
            logger.error("Error saving nivel jerarquico: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al guardar el nivel jerarquico", e);
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditNivelJerarquicoForm(@PathVariable Long id, Model model) {
        logger.info("Showing edit form for nivelJerarquicoId: {}", id);
        try {
            NivelJerarquico nivelJerarquico = nivelJerarquicoService.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nivel jerarquico no encontrado: " + id));
            model.addAttribute("nivelJerarquico", nivelJerarquico);
            return "niveles-jerarquicos/form";
        } catch (Exception e) {
            logger.error("Error showing edit form for nivelJerarquicoId {}: {}", id, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al mostrar el formulario de edición", e);
        }
    }

    @PostMapping("/edit/{id}")
    public String updateNivelJerarquico(@PathVariable Long id,
                                        @Valid @ModelAttribute("nivelJerarquico") NivelJerarquico nivelJerarquico,
                                        BindingResult result, Model model) {
        logger.info("Updating nivelJerarquicoId: {}. NivelJerarquico: {}", id, nivelJerarquico);
        try {
            if (result.hasErrors()) {
                logger.error("Validation errors: {}", result.getAllErrors());
                return "niveles-jerarquicos/form";
            }

            NivelJerarquico existingNivelJerarquico = nivelJerarquicoService.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nivel jerarquico no encontrado: " + id));
            existingNivelJerarquico.setNombre(nivelJerarquico.getNombre());
            nivelJerarquicoService.save(existingNivelJerarquico);
            return "redirect:/niveles-jerarquicos/list";
        } catch (Exception e) {
            logger.error("Error updating nivelJerarquicoId {}: {}", id, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al actualizar el nivel jerarquico", e);
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteNivelJerarquico(@PathVariable Long id) {
        logger.info("Deleting nivelJerarquicoId: {}", id);
        try {
            nivelJerarquicoService.deleteById(id);
            return "redirect:/niveles-jerarquicos/list";
        } catch (Exception e) {
            logger.error("Error deleting nivelJerarquicoId {}: {}", id, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al eliminar el nivel jerarquico", e);
        }
    }
    @GetMapping("/adm/list")
    public String listNivelesJerarquicosadm(@RequestParam(name = "fromUnidadId", required = false) Long unidadId, Model model) {
        logger.info("Listing all niveles jerarquicos");
        try {
            model.addAttribute("nivelesJerarquicos", nivelJerarquicoService.findAll());
            model.addAttribute("fromUnidadId", unidadId);
            addNivelJerarquicoBreadcrumbs(unidadId, "list", model);
            return "adm-unidad-edit-nivel-jerarquico-list";
        } catch (Exception e) {
            logger.error("Error listing niveles jerarquicos: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al listar los niveles jerarquicos", e);
        }
    }
    @GetMapping("/adm/new")
    public String showNewNivelJerarquicoFormAdm(@RequestParam(name = "fromUnidadId", required = false) Long unidadId, Model model) {
        logger.info("Showing new nivel jerarquico form");
        model.addAttribute("nivelJerarquico", new NivelJerarquico());
        model.addAttribute("fromUnidadId", unidadId);
        addNivelJerarquicoBreadcrumbs(unidadId, "Nuevo", model);
        return "adm-unidad-edit-nivel-jerarquico-form";
    }

    @PostMapping("/adm/save")
    public String saveNivelJerarquicoadm(@Valid @ModelAttribute("nivelJerarquico") NivelJerarquico nivelJerarquico,
                                         BindingResult result,
                                         @RequestParam(name = "fromUnidadId", required = false) Long unidadId,
                                         Model model) {
        logger.info("Saving nivel jerarquico: {}", nivelJerarquico);
        try {
            if (result.hasErrors()) {
                logger.error("Validation errors: {}", result.getAllErrors());
                model.addAttribute("fromUnidadId", unidadId);
                addNivelJerarquicoBreadcrumbs(unidadId, nivelJerarquico.getId() == null ? "Nuevo" : "Editar", model);
                return "adm-unidad-edit-nivel-jerarquico";
            }
            nivelJerarquicoService.save(nivelJerarquico);
            return "redirect:/niveles-jerarquicos/adm/list";
        } catch (Exception e) {
            logger.error("Error saving nivel jerarquico: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al guardar el nivel jerarquico", e);
        }
    }

    @GetMapping("/adm/edit/{id}")
    public String showEditNivelJerarquicoFormAdm(@PathVariable Long id, @RequestParam(name = "fromUnidadId", required = false) Long unidadId, Model model) {
        logger.info("Showing edit form for nivelJerarquicoId: {}", id);
        try {
            NivelJerarquico nivelJerarquico = nivelJerarquicoService.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nivel jerarquico no encontrado: " + id));
            model.addAttribute("nivelJerarquico", nivelJerarquico);
            model.addAttribute("fromUnidadId", unidadId);
            addNivelJerarquicoBreadcrumbs(unidadId, "Editar", model);
            return "adm-unidad-edit-nivel-jerarquico-form";
        } catch (Exception e) {
            logger.error("Error showing edit form for nivelJerarquicoId {}: {}", id, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al mostrar el formulario de edición", e);
        }
    }

    @PostMapping("/adm/edit/{id}")
    public String updateNivelJerarquicoadm(@PathVariable Long id,
                                        @Valid @ModelAttribute("nivelJerarquico") NivelJerarquico nivelJerarquico,
                                        BindingResult result, Model model) {
        logger.info("Updating nivelJerarquicoId: {}. NivelJerarquico: {}", id, nivelJerarquico);
        try {
            if (result.hasErrors()) {
                logger.error("Validation errors: {}", result.getAllErrors());
                return "adm-unidad-edit-nivel-jerarquico-form";
            }

            NivelJerarquico existingNivelJerarquico = nivelJerarquicoService.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nivel jerarquico no encontrado: " + id));
            existingNivelJerarquico.setNombre(nivelJerarquico.getNombre());
            nivelJerarquicoService.save(existingNivelJerarquico);
            return "redirect:/niveles-jerarquicos/adm/list";
        } catch (Exception e) {
            logger.error("Error updating nivelJerarquicoId {}: {}", id, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al actualizar el nivel jerarquico", e);
        }
    }

    @GetMapping("/adm/delete/{id}")
    public String deleteNivelJerarquicoAdm(@PathVariable Long id,
                                           @RequestParam(name = "fromUnidadId", required = false) Long unidadId,
                                           RedirectAttributes redirectAttributes) {
        try {
            nivelJerarquicoService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Nivel Jerárquico eliminado correctamente.");
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("error", "No se puede eliminar: el nivel jerárquico está en uso.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ocurrió un error inesperado al eliminar.");
        }

        String redirectUrl = "/niveles-jerarquicos/adm/list";
        if (unidadId != null) {
            redirectUrl += "?fromUnidadId=" + unidadId;
        }
        return "redirect:" + redirectUrl;
    }
}