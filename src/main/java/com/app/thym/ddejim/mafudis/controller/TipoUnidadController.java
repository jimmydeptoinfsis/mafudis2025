package com.app.thym.ddejim.mafudis.controller;

import com.app.thym.ddejim.mafudis.Service.TipoUnidadService;
import com.app.thym.ddejim.mafudis.Service.UnidadService;
import com.app.thym.ddejim.mafudis.dto.Breadcrumb;
import com.app.thym.ddejim.mafudis.model.TipoUnidad;
import com.app.thym.ddejim.mafudis.model.Unidad;
import jakarta.servlet.http.HttpServletRequest;
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
@RequestMapping("/tipos-unidad")
public class TipoUnidadController {

    private static final Logger logger = LoggerFactory.getLogger(TipoUnidadController.class);

    @Autowired
    private TipoUnidadService tipoUnidadService;
    @Autowired
    private UnidadService unidadService;
    // --- MÉTODO AUXILIAR DE BREADCRUMBS MODIFICADO ---
    private void addTipoUnidadBreadcrumbs(Long unidadId, String currentPageLabel, Model model) {
        List<Breadcrumb> breadcrumbs = new ArrayList<>();
        breadcrumbs.add(new Breadcrumb("Inicio", "/dashboard"));

        // Si el flujo viene desde una Unidad específica
        if (unidadId != null) {
            Unidad unidad = unidadService.findById(unidadId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unidad no encontrada"));

            breadcrumbs.add(new Breadcrumb("Organigrama de unidades", "/organigrama13"));
            breadcrumbs.add(new Breadcrumb("Detalle de Unidad", "/adm/unidades/details/" + unidadId));
            breadcrumbs.add(new Breadcrumb("Editar Tipo de Unidad", "/adm/unidades/" + unidadId + "/edit-tipo"));

            // --- LÓGICA CLAVE ---
            if ("list".equals(currentPageLabel)) {
                breadcrumbs.add(new Breadcrumb("Gestionar Catálogo de Tipo de Unidad", null));
            } else if (currentPageLabel != null) {
                breadcrumbs.add(new Breadcrumb("Gestionar Catálogo de Tipo de Unidad", "/tipos-unidad/adm/list?fromUnidadId=" + unidadId));
                breadcrumbs.add(new Breadcrumb(currentPageLabel, null));
            }
        } else { // Si se accede a la gestión de tipos directamente
            breadcrumbs.add(new Breadcrumb("Gestionar Tipos de Unidad", null));
        }

        model.addAttribute("breadcrumbs", breadcrumbs);

        // Lógica para el botón "Volver"
        String backUrl = "/dashboard";
        if (breadcrumbs.size() > 1) {
            backUrl = breadcrumbs.get(breadcrumbs.size() - 2).getUrl();
        }
        model.addAttribute("backUrl", backUrl);
    }
    // --- FIN AUXILIAR---
    @GetMapping("/list")
    public String listTiposUnidad(Model model) {

        logger.info("Listing all tipos de unidad");
        try {
            model.addAttribute("tiposUnidad", tipoUnidadService.findAll());
            model.addAttribute("pageTitle", "Gestión de Tipos de Unidad");//Título de la página
            return "tipos-unidad/list";
        } catch (Exception e) {
            logger.error("Error listing tipos de unidad: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al listar los tipos de unidad", e);
        }
    }

    @GetMapping("/new")
    public String showNewTipoUnidadForm(Model model) {
        logger.info("Showing new tipo unidad form");
        model.addAttribute("tipoUnidad", new TipoUnidad());
        model.addAttribute("pageTitle", "Nuevo Tipo de Unidad");//Título de la página

        return "tipos-unidad/form";
    }

    @PostMapping("/save")
    public String saveTipoUnidad(@Valid @ModelAttribute("tipoUnidad") TipoUnidad tipoUnidad,
                                 BindingResult result, Model model) {
        logger.info("Saving tipo unidad: {}", tipoUnidad);
        try {
            if (result.hasErrors()) {
                logger.error("Validation errors: {}", result.getAllErrors());
                return "tipos-unidad/form";
            }
            tipoUnidadService.save(tipoUnidad);
            return "redirect:/tipos-unidad/list";
        } catch (Exception e) {
            logger.error("Error saving tipo unidad: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al guardar el tipo de unidad", e);
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditTipoUnidadForm(@PathVariable Long id, Model model) {
        logger.info("Showing edit form for tipoUnidadId: {}", id);
        try {
            TipoUnidad tipoUnidad = tipoUnidadService.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tipo de unidad no encontrado: " + id));
            model.addAttribute("tipoUnidad", tipoUnidad);
            model.addAttribute("pageTitle", "Editar Tipo de Unidad");//Título de la página
            return "tipos-unidad/form";
        } catch (Exception e) {
            logger.error("Error showing edit form for tipoUnidadId {}: {}", id, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al mostrar el formulario de edición", e);
        }
    }

    @PostMapping("/edit/{id}")
    public String updateTipoUnidad(@PathVariable Long id,
                                   @Valid @ModelAttribute("tipoUnidad") TipoUnidad tipoUnidad,
                                   BindingResult result, Model model) {
        logger.info("Updating tipoUnidadId: {}. TipoUnidad: {}", id, tipoUnidad);
        try {
            if (result.hasErrors()) {
                logger.error("Validation errors: {}", result.getAllErrors());
                return "tipos-unidad/form";
            }

            TipoUnidad existingTipoUnidad = tipoUnidadService.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tipo de unidad no encontrado: " + id));
            existingTipoUnidad.setNombre(tipoUnidad.getNombre());
            tipoUnidadService.save(existingTipoUnidad);
            return "redirect:/tipos-unidad/list";
        } catch (Exception e) {
            logger.error("Error updating tipoUnidadId {}: {}", id, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al actualizar el tipo de unidad", e);
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteTipoUnidad(@PathVariable Long id) {
        logger.info("Deleting tipoUnidadId: {}", id);
        try {
            tipoUnidadService.deleteById(id);
            return "redirect:/tipos-unidad/list";
        } catch (Exception e) {
            logger.error("Error deleting tipoUnidadId {}: {}", id, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al eliminar el tipo de unidad", e);
        }
    }

    @GetMapping("/adm/list")
    public String listTiposUnidadadm(@RequestParam(name = "fromUnidadId", required = false) Long unidadId, Model model) {
        model.addAttribute("tiposUnidad", tipoUnidadService.findAll());
        //model.addAttribute("pageTitle", "Gestión de Tipos de Unidad");
        model.addAttribute("fromUnidadId", unidadId); // Pasamos el ID a la vista
        addTipoUnidadBreadcrumbs(unidadId, "list", model);
        return "adm-unidad-edit-tipo-list";
    }

    @GetMapping("/adm/new")
    public String showNewTipoUnidadFormadm(@RequestParam(name = "fromUnidadId", required = false) Long unidadId, Model model) {
        model.addAttribute("tipoUnidad", new TipoUnidad());
        model.addAttribute("pageTitle", "Nuevo Tipo de Unidad");
        model.addAttribute("fromUnidadId", unidadId);
        addTipoUnidadBreadcrumbs(unidadId, "Nuevo", model);
        return "adm-unidad-edit-tipo-form";
    }


    @PostMapping("/adm/save")
    public String saveTipoUnidadadm(@Valid @ModelAttribute("tipoUnidad") TipoUnidad tipoUnidad,
                                    BindingResult result,
                                    @RequestParam(name = "fromUnidadId", required = false) Long unidadId,
                                    Model model) {
        if (result.hasErrors()) {
            model.addAttribute("pageTitle", "Error en Formulario");
            model.addAttribute("fromUnidadId", unidadId);
            addTipoUnidadBreadcrumbs(unidadId, tipoUnidad.getId() == null ? "Nuevo" : "Editar", model);
            return "adm-unidad-edit-tipo-form";
        }
        tipoUnidadService.save(tipoUnidad);
        return "redirect:/tipos-unidad/adm/list" + (unidadId != null ? "?fromUnidadId=" + unidadId : "");
    }

    @GetMapping("/adm/edit/{id}")
    public String showEditTipoUnidadFormadm(@PathVariable Long id, @RequestParam(name = "fromUnidadId", required = false) Long unidadId, Model model) {
        TipoUnidad tipoUnidad = tipoUnidadService.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        model.addAttribute("tipoUnidad", tipoUnidad);
        model.addAttribute("pageTitle", "Editar Tipo de Unidad");
        model.addAttribute("fromUnidadId", unidadId);
        addTipoUnidadBreadcrumbs(unidadId, "Editar", model);
        return "adm-unidad-edit-tipo-form";
    }


    @PostMapping("/adm/edit/{id}")
    public String updateTipoUnidadadm(@PathVariable Long id,
                                      @Valid @ModelAttribute("tipoUnidad") TipoUnidad tipoUnidad,
                                      BindingResult result, Model model) {
        logger.info("Updating tipoUnidadId: {} from admin", id);
        if (result.hasErrors()) {
            logger.error("Validation errors: {}", result.getAllErrors());
            // Si hay errores, volvemos a mostrar el formulario
            return "adm/tipos-unidad/form";
        }

        tipoUnidadService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tipo de unidad no encontrado: " + id));

        // Es importante asignar el ID al objeto que viene del formulario para que JPA sepa que es una actualización
        tipoUnidad.setId(id);
        tipoUnidadService.save(tipoUnidad);

        // Redirección CORREGIDA a la lista de administrador
        return "redirect:/tipos-unidad/adm/list";
    }

    @GetMapping("/adm/delete/{id}")
    public String deleteTipoUnidadadm(@PathVariable Long id,
                                      @RequestParam(name = "fromUnidadId", required = false) Long unidadId,
                                      // 1. Añadimos RedirectAttributes para enviar mensajes
                                      RedirectAttributes redirectAttributes) {

        try {
            // 2. Intentamos eliminar el objeto
            tipoUnidadService.deleteById(id);
            // Si todo va bien, preparamos un mensaje de éxito
            redirectAttributes.addFlashAttribute("success", "El tipo de unidad ha sido eliminado correctamente.");

        } catch (DataIntegrityViolationException e) {
            // 3. Si ocurre un error de integridad (está en uso), lo capturamos
            logger.error("Intento de eliminar un TipoUnidad en uso. ID: {}", id, e);
            // Preparamos un mensaje de error claro para el usuario
            redirectAttributes.addFlashAttribute("error", "No se puede eliminar: El tipo de unidad está asignado a una o más unidades.");

        } catch (Exception e) {
            // Captura de cualquier otro error inesperado
            logger.error("Error al eliminar el TipoUnidad con ID: {}", id, e);
            redirectAttributes.addFlashAttribute("error", "Ocurrió un error inesperado al intentar eliminar.");
        }

        // 4. Construimos la URL de redirección (esto ya lo teníamos)
        // para asegurarnos de que el breadcrumb no se pierda.
        String redirectUrl = "/tipos-unidad/adm/list";
        if (unidadId != null) {
            redirectUrl += "?fromUnidadId=" + unidadId;
        }

        return "redirect:" + redirectUrl;
    }
}