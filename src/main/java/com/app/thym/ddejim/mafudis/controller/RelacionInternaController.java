package com.app.thym.ddejim.mafudis.controller;

import com.app.thym.ddejim.mafudis.Service.CargoService;
import com.app.thym.ddejim.mafudis.dto.Breadcrumb;
import com.app.thym.ddejim.mafudis.model.Cargo;
import com.app.thym.ddejim.mafudis.model.RelacionInterna;
import com.app.thym.ddejim.mafudis.repository.CargoRepository;
import com.app.thym.ddejim.mafudis.Service.RelacionInternaService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/relaciones-internas")
public class RelacionInternaController {

    private final RelacionInternaService relacionService;
    private final CargoRepository cargoRepo;
    private final CargoService cargoService;

    public RelacionInternaController(RelacionInternaService relacionService, CargoRepository cargoRepo, CargoService cargoService) {
        this.relacionService = relacionService;
        this.cargoRepo = cargoRepo;
        this.cargoService = cargoService;
    }

    // --- INICIO: MÉTODO AUXILIAR PARA BREADCRUMBS ---
    private void addRelacionesInternasBreadcrumbs(Long cargoId, String currentPageLabel, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));

        List<Breadcrumb> breadcrumbs = new ArrayList<>();
        breadcrumbs.add(new Breadcrumb("Inicio", "/dashboard")); // Asumiendo que esta es la URL de la lista de cargos
        breadcrumbs.add(new Breadcrumb("Organigrama de Cargos", "/organigrama12")); // La página actual no necesita URL
        breadcrumbs.add(new Breadcrumb("Detalle del cargo", "/cargos/adm/details/" + cargoId));

        if (currentPageLabel != null && !currentPageLabel.isEmpty()) {
            breadcrumbs.add(new Breadcrumb("Relaciones Internas", "/relaciones-internas/adm/list/" + cargoId));
            breadcrumbs.add(new Breadcrumb(currentPageLabel, null));
        } else {
            breadcrumbs.add(new Breadcrumb("Relaciones Internas", null));
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

    /*@GetMapping
    public String listar(Model model) {
        model.addAttribute("relaciones", relacionService.listar());
        return "relaciones_internas/list";
    }*/

    @GetMapping("/list/{cargoId}")
    public String listarRelacionesPorCargo(
            @PathVariable("cargoId") Long cargoId,
            Model model) {

        // Obtener el cargo para mostrar su nombre
        Cargo cargo = cargoRepo.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));

        // Obtener relaciones donde el cargo es origen o destino
        List<RelacionInterna> relacionesComoOrigen = relacionService.findByOrigenId(cargoId);
        List<RelacionInterna> relacionesComoDestino = relacionService.findByDestinoId(cargoId);

        model.addAttribute("cargo", cargo);
        model.addAttribute("relacionesComoOrigen", relacionesComoOrigen);
        model.addAttribute("relacionesComoDestino", relacionesComoDestino);

        return "relaciones_internas/list_por_cargo";
    }

    // Nueva relación con origen en la URL
    @GetMapping("/new/origen/{origenId}")
    public String nuevaRelacionConOrigen(
            @PathVariable("origenId") Long origenId,
            Model model) {

        Cargo cargoOrigen = cargoRepo.findById(origenId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo origen no encontrado"));

        RelacionInterna relacion = new RelacionInterna();
        relacion.setOrigen(cargoOrigen);

        model.addAttribute("relacionInterna", relacion);
        model.addAttribute("cargosDisponibles", cargoRepo.findAll());
        model.addAttribute("modoDesdeOrganigrama", true);

        return "relaciones_internas/form";
    }

    // Nueva relación sin origen (para acceso directo)
    @GetMapping("/new")
    public String nuevaRelacion(Model model) {
        model.addAttribute("relacionInterna", new RelacionInterna());
        model.addAttribute("cargosDisponibles", cargoRepo.findAll());
        model.addAttribute("modoDesdeOrganigrama", false);
        return "relaciones_internas/form";
    }

    /*@PostMapping
    public String guardar(
            @Valid @ModelAttribute("relacionInterna") RelacionInterna relacion,
            BindingResult result,
            Model model,
            RedirectAttributes redirect) {

        // Validaciones
        if (relacion.getOrigen() == null || relacion.getDestino() == null) {
            result.reject("", "Debe seleccionar ambos cargos");
        } else if (relacion.getOrigen().getId().equals(relacion.getDestino().getId())) {
            result.rejectValue("destino", null, "Un cargo no puede relacionarse consigo mismo.");
        } else if (relacionService.existeRelacion(relacion.getOrigen().getId(), relacion.getDestino().getId())) {
            result.rejectValue("destino", null, "Ya existe una relación entre estos cargos.");
        }

        if (result.hasErrors()) {
            model.addAttribute("cargosDisponibles", cargoRepo.findAll());
            model.addAttribute("modoDesdeOrganigrama", relacion.getOrigen() != null);
            return "relaciones_internas/form";
        }

        relacionService.guardar(relacion);
        redirect.addFlashAttribute("exito", "Relación guardada exitosamente.");

        // Redirige al organigrama si venía desde allí
        if (model.getAttribute("modoDesdeOrganigrama") != null && (Boolean) model.getAttribute("modoDesdeOrganigrama")) {
            return "redirect:/organigrama7";
        }
        return "redirect:/relaciones-internas";
    }
*/
    @GetMapping("/edit/{id}")
    public String editar(@PathVariable Long id, Model model) {
        RelacionInterna relacion = relacionService.obtenerPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("ID no válido: " + id));

        model.addAttribute("relacionInterna", relacion);
        model.addAttribute("cargosDisponibles", cargoRepo.findAll());
        model.addAttribute("modoDesdeOrganigrama", false);

        return "relaciones_internas/form";
    }

    @GetMapping("/delete/{id}")
    public String eliminar(
            @PathVariable Long id,
            @RequestParam(name = "fromOrganigrama", required = false) Boolean fromOrganigrama,
            RedirectAttributes redirect) {

        relacionService.eliminar(id);
        redirect.addFlashAttribute("exito", "Relación eliminada correctamente.");

        return fromOrganigrama != null && fromOrganigrama ?
                "redirect:/organigrama7" :
                "redirect:/relaciones-internas";
    }



    /*******************************************/

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("relaciones", relacionService.listar());
        return "adm-cargo-edit-relaciones-internas-list";
    }


    @GetMapping("/adm/list/{cargoId}")
    public String listarRelacionesPorCargoadm(
            @PathVariable("cargoId") Long cargoId,
            Model model) {

        // Obtener el cargo para mostrar su nombre
        Cargo cargo = cargoRepo.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));

        // Obtener relaciones donde el cargo es origen o destino
        List<RelacionInterna> relacionesComoOrigen = relacionService.findByOrigenId(cargoId);
        List<RelacionInterna> relacionesComoDestino = relacionService.findByDestinoId(cargoId);

        model.addAttribute("cargo", cargo);
        model.addAttribute("relacionesComoOrigen", relacionesComoOrigen);
        model.addAttribute("relacionesComoDestino", relacionesComoDestino);
        addRelacionesInternasBreadcrumbs(cargoId, null, model);
        return "adm-cargo-edit-relaciones-internas";
    }

    // Nueva relación con origen en la URL
    @GetMapping("/adm/new/origen/{origenId}")
    public String nuevaRelacionConOrigen(
            @PathVariable("origenId") Long origenId,
            @RequestParam("fromCargoId") Long fromCargoId, // Recibe fromCargoId
            Model model) {

        Cargo cargoOrigen = cargoRepo.findById(origenId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo origen no encontrado"));

        RelacionInterna relacion = new RelacionInterna();
        relacion.setOrigen(cargoOrigen);

        model.addAttribute("relacionInterna", relacion);
        model.addAttribute("cargosDisponibles", cargoRepo.findAll());
        model.addAttribute("fromCargoId", fromCargoId); // Añade fromCargoId al modelo
        addRelacionesInternasBreadcrumbs(fromCargoId, "Nueva", model);
        return "adm-cargo-edit-relaciones-internas-form";
    }

    @PostMapping("/adm/save")
    public String guardar(
            @Valid @ModelAttribute("relacionInterna") RelacionInterna relacion,
            BindingResult result,
            @RequestParam("fromCargoId") Long fromCargoId, // Recibe fromCargoId
            Model model,
            RedirectAttributes redirect) {

        if (result.hasErrors()) {
            model.addAttribute("cargosDisponibles", cargoRepo.findAll());
            model.addAttribute("fromCargoId", fromCargoId); // Añade fromCargoId de nuevo si hay errores
            addRelacionesInternasBreadcrumbs(relacion.getOrigen().getId(), relacion.getId() == null ? "Nueva" : "Editar", model);
            return "adm-cargo-edit-relaciones-internas-form";
        }

        relacionService.guardar(relacion);
        redirect.addFlashAttribute("exito", "Relación guardada exitosamente.");

        // Redirige a la página de detalles del cargo original
        return "redirect:/relaciones-internas/adm/list/" + fromCargoId;
    }

    // Nueva relación sin origen (para acceso directo)
    @GetMapping("/adm/new")
    public String nuevaRelacionadm(Model model) {
        model.addAttribute("relacionInterna", new RelacionInterna());
        model.addAttribute("cargosDisponibles", cargoRepo.findAll());
        model.addAttribute("modoDesdeOrganigrama", false);
        return "adm-cargo-edit-relaciones-internas-form";
    }

    @PostMapping
    public String guardar(
            @Valid @ModelAttribute("relacionInterna") RelacionInterna relacion,
            BindingResult result,
            Model model,
            RedirectAttributes redirect) {

        // Validaciones
        if (relacion.getOrigen() == null || relacion.getDestino() == null) {
            result.reject("", "Debe seleccionar ambos cargos");
        } else if (relacion.getOrigen().getId().equals(relacion.getDestino().getId())) {
            result.rejectValue("destino", null, "Un cargo no puede relacionarse consigo mismo.");
        } else if (relacionService.existeRelacion(relacion.getOrigen().getId(), relacion.getDestino().getId())) {
            result.rejectValue("destino", null, "Ya existe una relación entre estos cargos.");
        }

        if (result.hasErrors()) {
            model.addAttribute("cargosDisponibles", cargoRepo.findAll());
            model.addAttribute("modoDesdeOrganigrama", relacion.getOrigen() != null);

            return "relaciones_internas/form";
        }

        relacionService.guardar(relacion);

        redirect.addFlashAttribute("exito", "Relación guardada exitosamente.");

        // Redirige al organigrama si venía desde allí
        if (model.getAttribute("modoDesdeOrganigrama") != null && (Boolean) model.getAttribute("modoDesdeOrganigrama")) {
            return "redirect:/organigrama7";
        }
        return "redirect:/relaciones-internas";
    }

    @GetMapping("/adm/edit/{id}")
    public String editaradm(@PathVariable Long id,
                            @RequestParam(name = "fromCargoId") Long fromCargoId, // <-- AÑADE ESTE PARÁMETRO
                            Model model) {
        RelacionInterna relacion = relacionService.obtenerPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("ID no válido: " + id));

        model.addAttribute("relacionInterna", relacion);
        model.addAttribute("cargosDisponibles", cargoRepo.findAll());
        model.addAttribute("modoDesdeOrganigrama", false);
        model.addAttribute("fromCargoId", fromCargoId); // <-- AÑADE ESTA LÍNEA
        addRelacionesInternasBreadcrumbs(fromCargoId, "Editar", model);
        return "adm-cargo-edit-relaciones-internas-form";
    }

    @PostMapping("/adm/edit/{id}")
    public String actualizaradm(@PathVariable Long id,
                                @Valid @ModelAttribute("relacionInterna") RelacionInterna relacion,
                                BindingResult bindingResult,
                                @RequestParam(name = "fromCargoId") Long fromCargoId,
                                Model model,
                                RedirectAttributes redirect) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("cargosDisponibles", cargoRepo.findAll());
            model.addAttribute("fromCargoId", fromCargoId); // Reenviar por si hay error
            return "adm-cargo-edit-relaciones-internas-form";
        }

        // Aseguramos que el ID de la relación a actualizar es el correcto
        relacion.setId(id);

        // Aquí puedes mantener tu lógica de validación como en el paso anterior...

        relacionService.guardar(relacion);
        redirect.addFlashAttribute("exito", "Relación actualizada exitosamente.");

        // ¡CAMBIO CLAVE AQUÍ!
        // ANTES: return "redirect:/cargos/adm/details/" + fromCargoId;
        // AHORA:
        return "redirect:/relaciones-internas/adm/list/" + fromCargoId;
    }
    @PostMapping("/adm/new/origen/{origenId}")
    public String guardarNuevaRelacionAdm(@PathVariable Long origenId,
                                          @Valid @ModelAttribute("relacionInterna") RelacionInterna relacion,
                                          BindingResult bindingResult,
                                          @RequestParam(name = "fromCargoId") Long fromCargoId,
                                          RedirectAttributes redirect,
                                          Model model) {

        // Asignamos el cargo origen basado en el ID de la URL
        Cargo origen = cargoRepo.findById(origenId)
                .orElseThrow(() -> new IllegalArgumentException("ID de cargo origen no válido: " + origenId));
        relacion.setOrigen(origen);

        // Lógica de validación
        if (bindingResult.hasErrors()) {
            model.addAttribute("cargosDisponibles", cargoRepo.findAll());
            model.addAttribute("fromCargoId", fromCargoId);
            // Si hay errores, volvemos al formulario de creación
            return "adm-cargo-edit-relaciones-internas-form";
        }

        if (relacion.getOrigen().getId().equals(relacion.getDestino().getId())) {
            bindingResult.rejectValue("destino", "error.relacionInterna", "Un cargo no puede tener una relación consigo mismo.");
        }
        if (relacionService.existeRelacion(relacion.getOrigen().getId(), relacion.getDestino().getId())) {
            bindingResult.rejectValue("destino", "error.relacionInterna", "Ya existe una relación entre los cargos seleccionados.");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("cargosDisponibles", cargoRepo.findAll());
            model.addAttribute("fromCargoId", fromCargoId);
            return "adm-cargo-edit-relaciones-internas-form";
        }

        relacionService.guardar(relacion);
        redirect.addFlashAttribute("exito", "Relación creada exitosamente.");

        // Redirección a la lista de relaciones del cargo
        return "redirect:/relaciones-internas/adm/list/" + fromCargoId;
    }


    @GetMapping("/adm/delete/{id}")
    public String eliminaradm(
            @PathVariable Long id,
            @RequestParam(name = "fromCargoId", required = false) Long fromCargoId, // Recibe el ID del cargo
            @RequestParam(name = "fromOrganigrama", required = false) Boolean fromOrganigrama,
            RedirectAttributes redirect) {

        relacionService.eliminar(id);
        redirect.addFlashAttribute("exito", "Relación eliminada correctamente.");

        // ¡CAMBIO CLAVE AQUÍ!
        // Si se proporciona fromCargoId, redirige a la lista de relaciones de ese cargo.
        if (fromCargoId != null) {
            return "redirect:/relaciones-internas/adm/list/" + fromCargoId;
        }

        // Mantenemos la lógica anterior como alternativa si no vienes desde la vista de cargo
        if (fromOrganigrama != null && fromOrganigrama) {
            return "redirect:/organigrama12";
        }

        // Redirección por defecto si no se especifica ningún contexto
        return "redirect:/relaciones-internas";
    }



}