package com.app.thym.ddejim.mafudis.controller;

import com.app.thym.ddejim.mafudis.Service.*;
import com.app.thym.ddejim.mafudis.dto.Breadcrumb;
import com.app.thym.ddejim.mafudis.dto.UnidadDTO;
import com.app.thym.ddejim.mafudis.model.*;
import com.app.thym.ddejim.mafudis.repository.UnidadRepository;
import jakarta.validation.Valid;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class UnidadController {

    @Autowired
    private UnidadRepository unidadRepository;

    @Autowired
    private ObjetivoUnidadService objetivoUnidadService; // Inyectar servicio de objetivos

    @Autowired
    private FuncionGeneralUnidadService funcionGeneralUnidadService; // Inyectar servicio de funciones

    @Autowired
    private UnidadService unidadService;

    @Autowired
    private CargoService cargoService;

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Autowired
    private TipoUnidadService tipoUnidadService;

    @Autowired
    private ClasificacionUnidadService clasificacionUnidadService;

    @Autowired
    private NivelJerarquicoService nivelJerarquicoService;

    @Autowired
    private DependenciaService dependenciaService;



    @Autowired
    private RelacionInternaService relacionInternaService;

    @Autowired
    private RelacionExternaService relacionExternaService;

    // --- INICIO: AÑADIR MÉTODO AUXILIAR PARA BREADCRUMBS ---
    private void addUnidadBreadcrumbs(Unidad unidad, String currentPageLabel, Model model) {
        List<Breadcrumb> breadcrumbs = new ArrayList<>();
        breadcrumbs.add(new Breadcrumb("Inicio", "/dashboard"));
        // Puedes cambiar "/organigrama12" por la URL correcta de tu organigrama si es diferente
        breadcrumbs.add(new Breadcrumb("Organigrama de unidades", "/organigrama13"));

        // Si la unidad tiene un padre, se podría añadir aquí para más detalle (opcional)

        breadcrumbs.add(new Breadcrumb("Detalle de Unidad ", "/adm/unidades/details/" + unidad.getId()));


        if (currentPageLabel != null && !currentPageLabel.isEmpty()) {
            breadcrumbs.add(new Breadcrumb(currentPageLabel, null)); // Página actual
        }

        model.addAttribute("breadcrumbs", breadcrumbs);

        // Lógica para el botón "Volver"
        String backUrl = "/adm/unidades/details/" + unidad.getId(); // Volver a los detalles de la unidad
        if (breadcrumbs.size() > 1) {
            backUrl = breadcrumbs.get(breadcrumbs.size() - 2).getUrl();
        }
        model.addAttribute("backUrl", backUrl);
    }
    // --- FIN: MÉTODO AUXILIAR ---

    /*@GetMapping("/api/unidades/{id}")
    @ResponseBody
    public ResponseEntity<UnidadDTO> getUnidadDetails(@PathVariable("id") Long id) {
        Unidad unidad = unidadRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unidad no encontrada: " + id));

        // Crear un DTO y copiar solo los campos necesarios
        UnidadDTO dto = new UnidadDTO(
                unidad.getId(),
                unidad.getNombreUnidad()

        );

        return ResponseEntity.ok(dto);
    }*/

    @GetMapping("/unidades/details/{id}")
    public String showUnidadDetails(@PathVariable("id") Long id, Model model) {
        Unidad unidad = unidadRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unidad no encontrada: " + id));

        //////////***/////////////

        // Initialize cargos and their relationships to avoid lazy loading issues
        Hibernate.initialize(unidad.getCargos());
        Map<Long, List<RelacionInterna>> cargoRelacionesInternas = new HashMap<>();
        Map<Long, List<RelacionExterna>> cargoRelacionesExternas = new HashMap<>();

        for (Cargo cargo : unidad.getCargos()) {
            Hibernate.initialize(cargo.getRelacionesInternas());
            Hibernate.initialize(cargo.getRelacionesExternas());

            // Internal relationships (as in CargoController)
            List<RelacionInterna> relacionesComoOrigen = relacionInternaService.findByOrigenId(cargo.getId());
            List<RelacionInterna> relacionesComoDestino = relacionInternaService.findByDestinoId(cargo.getId());
            List<RelacionInterna> relacionesInternas = new ArrayList<>();
            relacionesInternas.addAll(relacionesComoOrigen);
            relacionesInternas.addAll(relacionesComoDestino);
            relacionesInternas.sort(Comparator.comparing(RelacionInterna::getTipoRelacion));
            cargoRelacionesInternas.put(cargo.getId(), relacionesInternas);

            // External relationships (as in CargoController)
            List<RelacionExterna> relacionesExternas = new ArrayList<>(cargo.getRelacionesExternas());
            relacionesExternas.sort(Comparator.comparing(RelacionExterna::getOrderIndex).thenComparing(RelacionExterna::getNombre));
            cargoRelacionesExternas.put(cargo.getId(), relacionesExternas);
        }


        ///////////////////////////////////////////
        model.addAttribute("unidad", unidad);
        model.addAttribute("nombreUnidad", unidad.getNombreUnidad());
        model.addAttribute("tipoUnidad", unidad.getTipoUnidad() != null ? unidad.getTipoUnidad().getNombre() : "N/A");
        model.addAttribute("clasificacion", unidad.getClasificacion() != null ? unidad.getClasificacion().getNombre() : "N/A");
        model.addAttribute("nivelJerarquico", unidad.getNivelJerarquico() != null ? unidad.getNivelJerarquico().getNombre() : "N/A");
        model.addAttribute("dependencia", unidad.getDependencia() != null ? unidad.getDependencia().getNombre() : "N/A");

        // Associated cargos
        String cargosText = unidad.getCargos().stream()
                .map(Cargo::getName)
                .collect(Collectors.joining(", "));
        model.addAttribute("cargos", cargosText.isEmpty() ? "Ninguno" : cargosText);

        // Add cargo relationships to model
        model.addAttribute("cargoRelacionesInternas", cargoRelacionesInternas);
        model.addAttribute("cargoRelacionesExternas", cargoRelacionesExternas);

        model.addAttribute("tiposUnidadListUrl", "/tipos-unidad/list");
        model.addAttribute("clasificacionesUnidadListUrl", "/clasificaciones-unidad/list");
        model.addAttribute("nivelesJerarquicosListUrl", "/niveles-jerarquicos/list");
        model.addAttribute("dependenciasListUrl", "/dependencias/list");





        return "unidad-details";
    }

    @GetMapping("/unidades/{id}/edit-tipo")
    public String showEditTipoUnidadForm(@PathVariable("id") Long id, Model model) {
        Unidad unidad = unidadRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unidad no encontrada: " + id));
        model.addAttribute("unidad", unidad);
        model.addAttribute("tiposUnidad", tipoUnidadService.findAll());
        return "unidad-edit-tipo";
    }

    @PostMapping("/unidades/{id}/update-tipo")
    public String updateTipoUnidad(@PathVariable("id") Long id, @RequestParam Long tipoUnidadId) {
        Unidad unidad = unidadRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unidad no encontrada: " + id));
        TipoUnidad tipoUnidad = tipoUnidadService.findById(tipoUnidadId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tipo de unidad no encontrado: " + tipoUnidadId));
        unidad.setTipoUnidad(tipoUnidad);
        unidadRepository.save(unidad);
        return "redirect:/unidades/details/" + id;
    }

    @GetMapping("/unidades/{id}/edit-clasificacion")
    public String showEditClasificacionUnidadForm(@PathVariable("id") Long id, Model model) {
        Unidad unidad = unidadRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unidad no encontrada: " + id));
        model.addAttribute("unidad", unidad);
        model.addAttribute("clasificacionesUnidad", clasificacionUnidadService.findAll());
        // Breadcrumb para la página de "Editar Clasificación"
        List<Breadcrumb> breadcrumbs = new ArrayList<>();
        breadcrumbs.add(new Breadcrumb("Dashboard", "/dashboard"));
        breadcrumbs.add(new Breadcrumb("Organigrama", "/organigrama13"));
        breadcrumbs.add(new Breadcrumb(unidad.getNombreUnidad(), "/adm/unidades/details/" + id));
        breadcrumbs.add(new Breadcrumb("Editar Clasificación de Unidad", null)); // Página actual

        model.addAttribute("breadcrumbs", breadcrumbs);
        model.addAttribute("backUrl", "/adm/unidades/details/" + id);
        return "unidad-edit-clasificacion";
    }

    @PostMapping("/unidades/{id}/update-clasificacion")
    public String updateClasificacionUnidad(@PathVariable("id") Long id, @RequestParam Long clasificacionUnidadId) {
        Unidad unidad = unidadRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unidad no encontrada: " + id));
        ClasificacionUnidad clasificacionUnidad = clasificacionUnidadService.findById(clasificacionUnidadId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Clasificacion de unidad no encontrada: " + clasificacionUnidadId));
        unidad.setClasificacion(clasificacionUnidad);
        unidadRepository.save(unidad);
        return "redirect:/unidades/details/" + id;
    }

    @GetMapping("/unidades/{id}/edit-nivel-jerarquico")
    public String showEditNivelJerarquicoForm(@PathVariable("id") Long id, Model model) {
        Unidad unidad = unidadRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unidad no encontrada: " + id));
        model.addAttribute("unidad", unidad);
        model.addAttribute("nivelesJerarquicos", nivelJerarquicoService.findAll());
        return "unidad-edit-nivel-jerarquico";
    }

    @PostMapping("/unidades/{id}/update-nivel-jerarquico")
    public String updateNivelJerarquico(@PathVariable("id") Long id, @RequestParam Long nivelJerarquicoId) {
        Unidad unidad = unidadRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unidad no encontrada: " + id));
        NivelJerarquico nivelJerarquico = nivelJerarquicoService.findById(nivelJerarquicoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nivel jerarquico no encontrado: " + nivelJerarquicoId));
        unidad.setNivelJerarquico(nivelJerarquico);
        unidadRepository.save(unidad);
        return "redirect:/unidades/details/" + id;
    }


    @GetMapping("/unidades/{id}/edit-dependencia")
    public String showEditDependenciaForm(@PathVariable("id") Long id, Model model) {
        Unidad unidad = unidadRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unidad no encontrada: " + id));
        model.addAttribute("unidad", unidad);
        model.addAttribute("dependencias", dependenciaService.findAll());
        return "unidad-edit-dependencia";
    }

    @PostMapping("/unidades/{id}/update-dependencia")
    public String updateDependencia(@PathVariable("id") Long id, @RequestParam Long dependenciaId) {
        Unidad unidad = unidadRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unidad no encontrada: " + id));
        Dependencia dependencia = dependenciaService.findById(dependenciaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dependencia no encontrada: " + dependenciaId));
        unidad.setDependencia(dependencia);
        unidadRepository.save(unidad);
        return "redirect:/unidades/details/" + id;
    }

    @GetMapping("/unidades/{id}/cargos")
    public String listCargosForUnidad(@PathVariable("id") Long id, Model model) {
        Unidad unidad = unidadRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unidad no encontrada: " + id));
        model.addAttribute("unidad", unidad);
        model.addAttribute("cargos", unidad.getCargos());
        // Filter cargos where pid is null
        model.addAttribute("allCargos", cargoService.findAll().stream()
                .filter(cargo -> cargo.getPid() == null)
                .toList());
        return "unidad-cargos";
    }

    @PostMapping("/unidades/{id}/cargos/update-associations")
    public String updateCargoAssociations(@PathVariable("id") Long id, @RequestParam(value = "selectedCargo", required = false) Long selectedCargoId) {
        Unidad unidad = unidadRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unidad no encontrada: " + id));

        // Clear existing associations
        for (Cargo cargo : unidad.getCargos()) {
            cargo.setUnidad(null);
            cargoService.save(cargo);
        }

        // Set new association if a cargo is selected
        if (selectedCargoId != null) {
            Cargo selectedCargo = cargoService.findById(selectedCargoId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cargo no encontrado: " + selectedCargoId));
            selectedCargo.setUnidad(unidad);
            cargoService.save(selectedCargo);
        }

        return "redirect:/unidades/" + id + "/cargos";
    }



    @GetMapping("adm/unidades/details/{id}")
    public String showUnidadDetailsadm(@PathVariable("id") Long id, Model model) {
        Unidad unidad = unidadRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unidad no encontrada: " + id));

        //////////***/////////////

        // Initialize cargos and their relationships to avoid lazy loading issues
        Hibernate.initialize(unidad.getCargos());
        Map<Long, List<RelacionInterna>> cargoRelacionesInternas = new HashMap<>();
        Map<Long, List<RelacionExterna>> cargoRelacionesExternas = new HashMap<>();

        for (Cargo cargo : unidad.getCargos()) {
            Hibernate.initialize(cargo.getRelacionesInternas());
            Hibernate.initialize(cargo.getRelacionesExternas());

            // Internal relationships (as in CargoController)
            List<RelacionInterna> relacionesComoOrigen = relacionInternaService.findByOrigenId(cargo.getId());
            List<RelacionInterna> relacionesComoDestino = relacionInternaService.findByDestinoId(cargo.getId());
            List<RelacionInterna> relacionesInternas = new ArrayList<>();
            relacionesInternas.addAll(relacionesComoOrigen);
            relacionesInternas.addAll(relacionesComoDestino);
            relacionesInternas.sort(Comparator.comparing(RelacionInterna::getTipoRelacion));
            cargoRelacionesInternas.put(cargo.getId(), relacionesInternas);

            // External relationships (as in CargoController)
            List<RelacionExterna> relacionesExternas = new ArrayList<>(cargo.getRelacionesExternas());
            relacionesExternas.sort(Comparator.comparing(RelacionExterna::getOrderIndex).thenComparing(RelacionExterna::getNombre));
            cargoRelacionesExternas.put(cargo.getId(), relacionesExternas);
        }

        ////
        // 1. Obtener la lista de objetivos ORDENADA usando el servicio.
        List<ObjetivoUnidad> objetivosOrdenados = objetivoUnidadService.findByUnidadId(id);

        // 2. Obtener la lista de funciones ORDENADA usando el servicio.
        List<FuncionGeneralUnidad> funcionesOrdenadas = funcionGeneralUnidadService.findByUnidadId(id);

        // 3. Añadir las listas ordenadas al modelo para que la vista las use.
        objetivosOrdenados.sort((o1, o2) -> Integer.compare(o1.getOrderIndex(), o2.getOrderIndex()));
        funcionesOrdenadas.sort((f1, f2) -> Integer.compare(f1.getOrderIndex(), f2.getOrderIndex()));
        model.addAttribute("objetivos", objetivosOrdenados);

        model.addAttribute("funciones", funcionesOrdenadas);



        ///////////////////////////////////////////
        model.addAttribute("unidad", unidad);
        model.addAttribute("nombreUnidad", unidad.getNombreUnidad());
        model.addAttribute("tipoUnidad", unidad.getTipoUnidad() != null ? unidad.getTipoUnidad().getNombre() : "N/A");
        model.addAttribute("clasificacion", unidad.getClasificacion() != null ? unidad.getClasificacion().getNombre() : "N/A");
        model.addAttribute("nivelJerarquico", unidad.getNivelJerarquico() != null ? unidad.getNivelJerarquico().getNombre() : "N/A");
        model.addAttribute("dependencia", unidad.getDependencia() != null ? unidad.getDependencia().getNombre() : "N/A");

        // Associated cargos
        String cargosText = unidad.getCargos().stream()
                .map(Cargo::getName)
                .collect(Collectors.joining(", "));
        model.addAttribute("cargos", cargosText.isEmpty() ? "Ninguno" : cargosText);

        // Add cargo relationships to model
        model.addAttribute("cargoRelacionesInternas", cargoRelacionesInternas);
        model.addAttribute("cargoRelacionesExternas", cargoRelacionesExternas);

        model.addAttribute("tiposUnidadListUrl", "/tipos-unidad/list");
        model.addAttribute("clasificacionesUnidadListUrl", "/clasificaciones-unidad/list");
        model.addAttribute("nivelesJerarquicosListUrl", "/niveles-jerarquicos/list");
        model.addAttribute("dependenciasListUrl", "/dependencias/list");

        return "adm-unidad-details";
    }

    @GetMapping("/adm/unidades/{id}/edit-tipo")
    public String showEditTipoUnidadFormadm(@PathVariable("id") Long id, Model model) {
        Unidad unidad = unidadRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unidad no encontrada: " + id));
        model.addAttribute("unidad", unidad);
        model.addAttribute("tiposUnidad", tipoUnidadService.findAll());
        addUnidadBreadcrumbs(unidad, "Editar Tipo de Unidad", model);
        return "adm-unidad-edit-tipo";
    }

    @PostMapping("adm/unidades/{id}/update-tipo")
    public String updateTipoUnidadadm(@PathVariable("id") Long id, @RequestParam Long tipoUnidadId) {
        Unidad unidad = unidadRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unidad no encontrada: " + id));
        TipoUnidad tipoUnidad = tipoUnidadService.findById(tipoUnidadId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tipo de unidad no encontrado: " + tipoUnidadId));
        unidad.setTipoUnidad(tipoUnidad);
        unidadRepository.save(unidad);
        return "redirect:/adm/unidades/details/" + id;
    }

    @GetMapping("/adm/unidades/{id}/edit-clasificacion")
    public String showEditUnidadClasificacionForm(@PathVariable("id") Long id, Model model) {
        Unidad unidad = unidadRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unidad no encontrada: " + id));
        model.addAttribute("unidad", unidad);
        model.addAttribute("clasificacionesUnidad", clasificacionUnidadService.findAll());

        List<Breadcrumb> breadcrumbs = new ArrayList<>();
        breadcrumbs.add(new Breadcrumb("Inicio", "/dashboard"));
        breadcrumbs.add(new Breadcrumb("Organigrama de unidades", "/organigrama13"));
        breadcrumbs.add(new Breadcrumb("Detalle de Unidad", "/adm/unidades/details/" + id));
        breadcrumbs.add(new Breadcrumb("Editar Clasificación", null)); // Página actual

        model.addAttribute("breadcrumbs", breadcrumbs);
        model.addAttribute("backUrl", "/adm/unidades/details/" + id); // Para el botón "Volver"
        return "adm-unidad-edit-clasificacion";
    }

    @PostMapping("/adm/unidades/{id}/update-clasificacion")
    public String updateClasificacionUnidadadm(@PathVariable("id") Long id, @RequestParam Long clasificacionUnidadId) {
        Unidad unidad = unidadRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unidad no encontrada: " + id));
        ClasificacionUnidad clasificacionUnidad = clasificacionUnidadService.findById(clasificacionUnidadId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Clasificacion de unidad no encontrada: " + clasificacionUnidadId));
        unidad.setClasificacion(clasificacionUnidad);
        unidadRepository.save(unidad);
        return "redirect:/adm/unidades/details/" + id;
    }


    @GetMapping("/adm/unidades/{id}/edit-nivel-jerarquico")
    public String showEditNivelJerarquicoFormadm(@PathVariable("id") Long id, Model model) {
        Unidad unidad = unidadRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unidad no encontrada: " + id));
        model.addAttribute("unidad", unidad);
        model.addAttribute("nivelesJerarquicos", nivelJerarquicoService.findAll());

        List<Breadcrumb> breadcrumbs = new ArrayList<>();
        breadcrumbs.add(new Breadcrumb("Inicio", "/dashboard"));
        breadcrumbs.add(new Breadcrumb("Organigrama de unidades", "/organigrama13"));
        breadcrumbs.add(new Breadcrumb("Detalle de Unidad", "/adm/unidades/details/" + id));
        breadcrumbs.add(new Breadcrumb("Editar Nivel Jerárquico", null)); // Página actual

        model.addAttribute("breadcrumbs", breadcrumbs);
        model.addAttribute("backUrl", "/adm/unidades/details/" + id); // Para el botón "Volver"
        return "adm-unidad-edit-nivel-jerarquico";
    }

    @PostMapping("/adm/unidades/{id}/update-nivel-jerarquico")
    public String updateNivelJerarquicoadm(@PathVariable("id") Long id, @RequestParam Long nivelJerarquicoId) {
        Unidad unidad = unidadRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unidad no encontrada: " + id));
        NivelJerarquico nivelJerarquico = nivelJerarquicoService.findById(nivelJerarquicoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nivel jerarquico no encontrado: " + nivelJerarquicoId));
        unidad.setNivelJerarquico(nivelJerarquico);
        unidadRepository.save(unidad);
        return "redirect:/adm/unidades/details/" + id;
    }


    @GetMapping("/adm/unidades/{id}/edit-dependencia")
    public String showEditDependenciaFormadm(@PathVariable("id") Long id, Model model) {
        Unidad unidad = unidadRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unidad no encontrada: " + id));
        model.addAttribute("unidad", unidad);
        model.addAttribute("dependencias", dependenciaService.findAll());
        List<Breadcrumb> breadcrumbs = new ArrayList<>();
        breadcrumbs.add(new Breadcrumb("Inicio", "/dashboard"));
        breadcrumbs.add(new Breadcrumb("Organigrama de unidades", "/organigrama13"));
        breadcrumbs.add(new Breadcrumb("Detalle de Unidad", "/adm/unidades/details/" + id));
        breadcrumbs.add(new Breadcrumb("Editar Dependencia", null)); // Página actual

        model.addAttribute("breadcrumbs", breadcrumbs);
        model.addAttribute("backUrl", "/adm/unidades/details/" + id); // Para el botón "Volver"
        return "adm-unidad-edit-dependencia";
    }

    @PostMapping("/adm/unidades/{id}/update-dependencia")
    public String updateDependenciaadm(@PathVariable("id") Long id, @RequestParam Long dependenciaId) {
        Unidad unidad = unidadRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unidad no encontrada: " + id));
        Dependencia dependencia = dependenciaService.findById(dependenciaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dependencia no encontrada: " + dependenciaId));
        unidad.setDependencia(dependencia);
        unidadRepository.save(unidad);
        return "redirect:/adm/unidades/details/" + id;
    }
    @GetMapping("/adm/unidades/{id}/cargos")
    public String listCargosForUnidadadm(@PathVariable("id") Long id, Model model) {
        Unidad unidad = unidadRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unidad no encontrada: " + id));
        model.addAttribute("unidad", unidad);
        model.addAttribute("cargos", unidad.getCargos());
        // Filter cargos where pid is null
        model.addAttribute("allCargos", cargoService.findAll().stream()
                .filter(cargo -> cargo.getPid() == null)
                .toList());

        List<Breadcrumb> breadcrumbs = new ArrayList<>();
        breadcrumbs.add(new Breadcrumb("Inicio", "/dashboard"));
        breadcrumbs.add(new Breadcrumb("Organigrama de Unidades", "/organigrama13"));
        breadcrumbs.add(new Breadcrumb("Detalle de Unidad", "/adm/unidades/details/" + id));
        breadcrumbs.add(new Breadcrumb("Asignar Cargos", null)); // Página actual

        model.addAttribute("breadcrumbs", breadcrumbs);
        model.addAttribute("backUrl", "/adm/unidades/details/" + id);

        return "adm-unidad-edit-cargo";
    }

    @PostMapping("/adm/unidades/{id}/cargos/update-associations")
    public String updateCargoAssociationsadm(@PathVariable("id") Long id, @RequestParam(value = "selectedCargo", required = false) Long selectedCargoId) {
        Unidad unidad = unidadRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unidad no encontrada: " + id));

        // Clear existing associations
        for (Cargo cargo : unidad.getCargos()) {
            cargo.setUnidad(null);
            cargoService.save(cargo);
        }

        // Set new association if a cargo is selected
        if (selectedCargoId != null) {
            Cargo selectedCargo = cargoService.findById(selectedCargoId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cargo no encontrado: " + selectedCargoId));
            selectedCargo.setUnidad(unidad);
            cargoService.save(selectedCargo);
        }

        return "redirect:/adm/unidades/details/" + id;
    }
}