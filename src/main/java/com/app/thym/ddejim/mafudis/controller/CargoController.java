package com.app.thym.ddejim.mafudis.controller;

import com.app.thym.ddejim.mafudis.Service.*;

import com.app.thym.ddejim.mafudis.dto.Breadcrumb;
import com.app.thym.ddejim.mafudis.dto.CargoDTO;
import com.app.thym.ddejim.mafudis.model.*;
import com.app.thym.ddejim.mafudis.repository.CargoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


import java.util.*;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/cargos") // Base path for cargo-related operations
public class CargoController {

    @Autowired
    private CargoService cargoService;


    @Autowired
    private TagService tagService;

    @Autowired
    private RelacionInternaService relacionInternaService;

    @Autowired // Inject ResponsabilidadService
    private ResponsabilidadService responsabilidadService;

    @Autowired // Inyectar el nuevo servicio
    private RelacionExternaService relacionExternaService;

    @Autowired
    private FuncionActividadService funcionActividadService;

    @Autowired
    private DocumentosGeneraService documentosGeneraService;
    // Listar todos los cargos (existing method)

    @Autowired
    private PerfilContratacionService perfilContratacionService;

    @Autowired
    private OtrosConocimientosService otrosConocimientosService;

    @Autowired
    private HabilidadesDestrezasService habilidadesDestrezasService;

    @Autowired
    private GradoAcademicoMinimoService gradoAcademicoMinimoService;



    @GetMapping
    public String listCargos(Model model) {
        model.addAttribute("cargos", cargoService.findAll());
        return "cargos/list"; // Assumes cargos/list.html exists
    }
 /*   @GetMapping("/api/{id}") //
    @ResponseBody
    public ResponseEntity<CargoDTO> getCargoDetails(@PathVariable("id") Long id) {
        Cargo cargo = cargoService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cargo no encontrado: " + id));

        CargoDTO dto = new CargoDTO(cargo.getName());
        return ResponseEntity.ok(dto);
    }*/
    // Mostrar formulario para crear un nuevo cargo (existing method)
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("cargo", new Cargo());
        model.addAttribute("cargos", cargoService.findAll()); // For parent selection
        model.addAttribute("tags", tagService.findAll());
        // Return the form for creating/editing a Cargo
        // Assuming it's named cargos/form.html based on your original structure
        return "cargos/form";
    }

    // Guardar un cargo (crear o actualizar) (existing method - might need adjustments later if form changes)
    @PostMapping
    public String saveCargo(@ModelAttribute("cargo") Cargo cargo,
                            @RequestParam(value = "tagIds", required = false) List<Long> tagIds,
                            @RequestParam(value = "relacionExternaIds", required = false) List<Long> relacionExternaIds) {
        try {
            if (tagIds != null && !tagIds.isEmpty()) {
                List<Tag> selectedTags = tagService.findByIds(tagIds);
                cargo.setTags(selectedTags);
            } else {
                cargo.setTags(new ArrayList<>());
            }

            if (relacionExternaIds != null && !relacionExternaIds.isEmpty()) {
                Set<RelacionExterna> selectedRelaciones = relacionExternaService.findByIds(relacionExternaIds);
                cargo.setRelacionesExternas(selectedRelaciones);
            } else {
                cargo.setRelacionesExternas(new HashSet<>());
            }

            cargoService.save(cargo);
            return "redirect:/cargos";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/cargos?error=Error al guardar el cargo: " + e.getMessage();
        }
    }


    // Mostrar formulario para editar un cargo (existing method)
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        try {
            Cargo cargo = cargoService.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cargo no encontrado: " + id));
            model.addAttribute("cargo", cargo);
            model.addAttribute("cargos", cargoService.findAll());
            model.addAttribute("tags", tagService.findAll());
            model.addAttribute("allRelacionesInternas", relacionInternaService.listar()); // <-- AÑADIR ESTO
            return "cargos/form";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/cargos?error=Error al cargar el formulario de edición: " + e.getMessage();
        }
    }

    // *** NUEVO MÉTODO PARA MOSTRAR DETALLES DEL CARGO Y SUS RESPONSABILIDADES ***
    @GetMapping("/details/{id}")
    public String showCargoDetails(@PathVariable("id") Long id, Model model) {
        Cargo cargo = cargoService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cargo no encontrado: " + id));

        model.addAttribute("cargo", cargo);

        // Fetch all cargos to build the hierarchy
        List<Cargo> allCargos = cargoService.findAll();

        // Find the hierarchy from root to the selected cargo
        List<CargoOrgChartDTO> hierarchy = buildHierarchyToCargo(cargo, allCargos); // Updated type
        model.addAttribute("hierarchyCargos", hierarchy);

        // Tags
        List<String> tagNames = cargo.getTags().stream().map(Tag::getName).collect(Collectors.toList());
        model.addAttribute("tags", tagNames.isEmpty() ? "Ninguna" : String.join(", ", tagNames));

        // Responsabilidades
        List<Responsabilidad> responsabilidades = responsabilidadService.findByCargoId(id);
        model.addAttribute("responsabilidades", responsabilidades);

        // Relaciones Internas
        List<RelacionInterna> relacionesComoOrigen = relacionInternaService.findByOrigenId(id);
        List<RelacionInterna> relacionesComoDestino = relacionInternaService.findByDestinoId(id);
        List<RelacionInterna> relacionesInternas = new ArrayList<>();
        relacionesInternas.addAll(relacionesComoOrigen);
        relacionesInternas.addAll(relacionesComoDestino);
        relacionesInternas.sort(Comparator.comparing(RelacionInterna::getTipoRelacion));
        model.addAttribute("relacionesInternas", relacionesInternas);

        // Relaciones Externas
        List<RelacionExterna> relacionesExternas = new ArrayList<>(cargo.getRelacionesExternas());
        relacionesExternas.sort(Comparator.comparing(RelacionExterna::getOrderIndex).thenComparing(RelacionExterna::getNombre));
        model.addAttribute("relacionesExternas", relacionesExternas);

        // Funciones y/o actividades
        List<FuncionActividad> funcionesActividades = funcionActividadService.findByCargoId(id);
        model.addAttribute("funcionesActividades", funcionesActividades);

        // Documentos que genera
        List<DocumentosGenera> documentosGenera = documentosGeneraService.findByCargoId(id);
        model.addAttribute("documentosGenera", documentosGenera);

        // Perfil de contratación
        List<PerfilContratacion> perfilesContratacion = perfilContratacionService.findByCargoId(id);
        model.addAttribute("perfilesContratacion", perfilesContratacion);

        // Grado Académico Mìnimo
        List<GradoAcademicoMinimo> gradosAcademicos = gradoAcademicoMinimoService.findByCargoId(id);
        model.addAttribute("gradosAcademicos", gradosAcademicos);

        // Otros conocimientos
        List<OtrosConocimientos> otrosConocimientos = otrosConocimientosService.findByCargoId(id);
        model.addAttribute("otrosConocimientos", otrosConocimientos);

        // Habilidades y destrezas
        List<HabilidadesDestrezas> habilidadesDestrezas = habilidadesDestrezasService.findByCargoId(id);
        model.addAttribute("habilidadesDestrezas", habilidadesDestrezas);

        // Superior
        String superiorText = "Ninguno";
        if (cargo.getPid() != null) {
            Optional<Cargo> superiorOptional = cargoService.findById(cargo.getPid());
            superiorText = superiorOptional.map(Cargo::getName).orElse("Ninguno (ID no encontrado)");
        }
        model.addAttribute("superior", superiorText);

        // Dependientes
        List<Cargo> dependientes = allCargos.stream()
                .filter(c -> c.getPid() != null && c.getPid().equals(id))
                .collect(Collectors.toList());
        String dependientesText = dependientes.stream()
                .map(Cargo::getName)
                .collect(Collectors.joining(", "));
        model.addAttribute("dependientes", dependientesText.isEmpty() ? "Ninguno" : dependientesText);

        return "cargo-details";
    }

    // Helper method to build hierarchy from root to selected cargo
    private List<CargoOrgChartDTO> buildHierarchyToCargo(Cargo selectedCargo, List<Cargo> allCargos) {
        List<CargoOrgChartDTO> hierarchy = new ArrayList<>();

        // Step 1: Find the path from selected cargo to root
        List<Cargo> pathToRoot = new ArrayList<>();
        Cargo current = selectedCargo;
        pathToRoot.add(current);

        while (current.getPid() != null) {
            Long parentId = current.getPid();
            Optional<Cargo> parent = allCargos.stream()
                    .filter(c -> c.getId().equals(parentId))
                    .findFirst();
            if (parent.isPresent()) {
                current = parent.get();
                pathToRoot.add(current);
            } else {
                break; // No parent found
            }
        }

        // Reverse the path to start from root
        Collections.reverse(pathToRoot);

        // Step 2: Convert path to DTOs
        for (Cargo cargo : pathToRoot) {
            hierarchy.add(convertToOrgChartDTO(cargo));
        }

        // Step 3: Add direct children of the selected cargo
        List<Cargo> children = allCargos.stream()
                .filter(c -> c.getPid() != null && c.getPid().equals(selectedCargo.getId()))
                .collect(Collectors.toList());
        for (Cargo child : children) {
            hierarchy.add(convertToOrgChartDTO(child));
        }

        return hierarchy;
    }

    private CargoOrgChartDTO convertToOrgChartDTO(Cargo cargo) {
        CargoOrgChartDTO dto = new CargoOrgChartDTO();
        dto.setId(cargo.getId());
        dto.setPid(cargo.getPid());
        dto.setName(cargo.getName());
        dto.setTitle(cargo.getTitle());
        dto.setImg(cargo.getImg());
        dto.setEmail(cargo.getEmail());
        dto.setPhone(cargo.getPhone());
        dto.setDescription(cargo.getDescription());
        if (cargo.getTags() != null && !cargo.getTags().isEmpty()) {
            dto.setTags(cargo.getTags().stream()
                    .map(Tag::getName)
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    // Eliminar un cargo (existing method)
    @GetMapping("/delete/{id}")
    public String deleteCargo(@PathVariable("id") Long id) {
        try {
            // Consider adding checks here (e.g., cannot delete if it has children)
            cargoService.deleteById(id);
            return "redirect:/cargos";
        } catch (Exception e) {
            e.printStackTrace();
            // Consider adding error message to redirect attributes
            return "redirect:/cargos?error=Error al eliminar el cargo: " + e.getMessage();
        }
    }
    /**
     * Muestra el formulario para seleccionar/deseleccionar relaciones internas
     * para un cargo específico.
     */
    @PostMapping("/{cargoId}/relaciones-internas")
    public String updateCargoRelacionesInternas(@PathVariable Long cargoId,
                                                @RequestParam(value = "relacionIds", required = false) List<Long> relacionIds) {
        try {
            Set<Long> relacionInternaIds = relacionIds != null ? new HashSet<>(relacionIds) : new HashSet<>();
            cargoService.updateRelacionesInternas(cargoId, relacionInternaIds);
            return "redirect:/cargos/details/" + cargoId;
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/cargos/details/" + cargoId + "?error=Error al actualizar relaciones";
        }
    }

    @PostMapping("/{cargoId}/relaciones-internas/reorder")
    @ResponseBody
    public ResponseEntity<?> reorderRelacionesInternas(@PathVariable Long cargoId,
                                                       @RequestBody List<Long> orderedRelacionIds) {
        try {
            cargoService.reorderRelacionesInternas(cargoId, orderedRelacionIds);
            return ResponseEntity.ok("Orden actualizado correctamente.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar el orden: " + e.getMessage());
        }
    }

    @GetMapping("/{cargoId}/relaciones-externas")
    public String showManageRelacionesExternasForm(@PathVariable Long cargoId, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cargo no encontrado: " + cargoId));
        List<RelacionExterna> allRelaciones = relacionExternaService.findAll();

        model.addAttribute("cargo", cargo);
        model.addAttribute("allRelaciones", allRelaciones);
        return "cargos/relaciones-externas-form";
    }

    @PostMapping("/{cargoId}/relaciones-externas")
    public String updateCargoRelacionesExternas(@PathVariable Long cargoId,
                                                @RequestParam(value = "relacionIds", required = false) List<Long> relacionIds) {
        try {
            Cargo cargo = cargoService.findById(cargoId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cargo no encontrado: " + cargoId));

            Set<RelacionExterna> selectedRelaciones;
            if (relacionIds != null && !relacionIds.isEmpty()) {
                selectedRelaciones = relacionExternaService.findByIds(relacionIds);
            } else {
                selectedRelaciones = new HashSet<>();
            }

            cargo.setRelacionesExternas(selectedRelaciones);
            cargoService.save(cargo);

            return "redirect:/cargos/details/" + cargoId;
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/cargos/details/" + cargoId + "?error=Error al actualizar relaciones";
        }
    }



    @GetMapping("/adm/details/{id}")
    public String showCargoDetailsadm(@PathVariable("id") Long id, Model model) {
        Cargo cargo = cargoService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cargo no encontrado: " + id));

        // --- INICIO: Añadir migas de pan ---
        List<Breadcrumb> breadcrumbs = new ArrayList<>();
        breadcrumbs.add(new Breadcrumb("Inicio", "/dashboard")); // Asumiendo que esta es la URL de la lista de cargos
        breadcrumbs.add(new Breadcrumb("Organigrama de Cargos", "/organigrama12")); // La página actual no necesita URL
        breadcrumbs.add(new Breadcrumb("Detalle del cargo", null));
        model.addAttribute("breadcrumbs", breadcrumbs);
        // --- FIN: Añadir migas de pan ---


        model.addAttribute("cargo", cargo);

        // Fetch all cargos to build the hierarchy
        List<Cargo> allCargos = cargoService.findAll();

        // Find the hierarchy from root to the selected cargo
        List<CargoOrgChartDTO> hierarchy = buildHierarchyToCargo(cargo, allCargos); // Updated type
        model.addAttribute("hierarchyCargos", hierarchy);

        // Tags
        List<String> tagNames = cargo.getTags().stream().map(Tag::getName).collect(Collectors.toList());
        model.addAttribute("tags", tagNames.isEmpty() ? "Ninguna" : String.join(", ", tagNames));

        // Responsabilidades
        List<Responsabilidad> responsabilidades = responsabilidadService.findByCargoId(id);
        model.addAttribute("responsabilidades", responsabilidades);

        // Relaciones Internas
        List<RelacionInterna> relacionesComoOrigen = relacionInternaService.findByOrigenId(id);
        List<RelacionInterna> relacionesComoDestino = relacionInternaService.findByDestinoId(id);
        List<RelacionInterna> relacionesInternas = new ArrayList<>();
        relacionesInternas.addAll(relacionesComoOrigen);
        relacionesInternas.addAll(relacionesComoDestino);
        relacionesInternas.sort(Comparator.comparing(RelacionInterna::getTipoRelacion));
        model.addAttribute("relacionesInternas", relacionesInternas);

        // Relaciones Externas
        List<RelacionExterna> relacionesExternas = new ArrayList<>(cargo.getRelacionesExternas());
        relacionesExternas.sort(Comparator.comparing(RelacionExterna::getOrderIndex).thenComparing(RelacionExterna::getNombre));
        model.addAttribute("relacionesExternas", relacionesExternas);

        // Funciones y/o actividades
        List<FuncionActividad> funcionesActividades = funcionActividadService.findByCargoId(id);
        model.addAttribute("funcionesActividades", funcionesActividades);

        // Documentos que genera
        List<DocumentosGenera> documentosGenera = documentosGeneraService.findByCargoId(id);
        model.addAttribute("documentosGenera", documentosGenera);

        // Perfil de contratación
        List<PerfilContratacion> perfilesContratacion = perfilContratacionService.findByCargoId(id);
        model.addAttribute("perfilesContratacion", perfilesContratacion);

        // Grado Académico Mìnimo
        List<GradoAcademicoMinimo> gradosAcademicos = gradoAcademicoMinimoService.findByCargoId(id);
        model.addAttribute("gradosAcademicos", gradosAcademicos);

        // Otros conocimientos
        List<OtrosConocimientos> otrosConocimientos = otrosConocimientosService.findByCargoId(id);
        model.addAttribute("otrosConocimientos", otrosConocimientos);

        // Habilidades y destrezas
        List<HabilidadesDestrezas> habilidadesDestrezas = habilidadesDestrezasService.findByCargoId(id);
        model.addAttribute("habilidadesDestrezas", habilidadesDestrezas);

        // Superior
        String superiorText = "Ninguno";
        if (cargo.getPid() != null) {
            Optional<Cargo> superiorOptional = cargoService.findById(cargo.getPid());
            superiorText = superiorOptional.map(Cargo::getName).orElse("Ninguno (ID no encontrado)");
        }
        model.addAttribute("superior", superiorText);

        // Dependientes
        List<Cargo> dependientes = allCargos.stream()
                .filter(c -> c.getPid() != null && c.getPid().equals(id))
                .collect(Collectors.toList());
        String dependientesText = dependientes.stream()
                .map(Cargo::getName)
                .collect(Collectors.joining(", "));
        model.addAttribute("dependientes", dependientesText.isEmpty() ? "Ninguno" : dependientesText);

        return "adm-cargo-details";
    }

    @GetMapping("/adm/{cargoId}/relaciones-externas")
    public String showManageRelacionesExternasFormadm(@PathVariable Long cargoId, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cargo no encontrado: " + cargoId));
        List<RelacionExterna> allRelaciones = relacionExternaService.findAll();

        model.addAttribute("cargo", cargo);
        model.addAttribute("allRelaciones", allRelaciones);
        //breadcrumbs.add(new Breadcrumb("Relaciones Externas", null));
        List<Breadcrumb> breadcrumbs = new ArrayList<>();
        breadcrumbs.add(new Breadcrumb("Inicio", "/dashboard")); // Asumiendo que esta es la URL de la lista de cargos
        breadcrumbs.add(new Breadcrumb("Organigrama de Cargos", "/organigrama12")); // La página actual no necesita URL
        breadcrumbs.add(new Breadcrumb("Detalle del cargo", "/cargos/adm/details/" + cargoId));
        breadcrumbs.add(new Breadcrumb("Gestionar Relaciones Externas", null));
        model.addAttribute("breadcrumbs", breadcrumbs);

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


        return "adm-cargo-edit-relaciones-externas";
    }

    @PostMapping("/adm/{cargoId}/relaciones-externas")
    public String updateCargoRelacionesExternasadm(@PathVariable Long cargoId,
                                                @RequestParam(value = "relacionIds", required = false) List<Long> relacionIds) {
        try {
            Cargo cargo = cargoService.findById(cargoId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cargo no encontrado: " + cargoId));

            Set<RelacionExterna> selectedRelaciones;
            if (relacionIds != null && !relacionIds.isEmpty()) {
                selectedRelaciones = relacionExternaService.findByIds(relacionIds);
            } else {
                selectedRelaciones = new HashSet<>();
            }

            cargo.setRelacionesExternas(selectedRelaciones);
            cargoService.save(cargo);

            return "redirect:/cargos/adm/details/" + cargoId;
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/cargos/adm/details/" + cargoId + "?error=Error al actualizar relaciones";
        }
    }

    @GetMapping("/{cargoId}/relaciones-externas/edit/{relacionExternaId}")
    public String showEditRelacionExternaForm(@PathVariable("cargoId") Long cargoId, @PathVariable("relacionExternaId") Long relacionExternaId, Model model) {
        Cargo cargo = cargoService.findById(cargoId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cargo no encontrado"));
        RelacionExterna relacionExterna = relacionExternaService.findById(relacionExternaId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Relación externa no encontrada"));

        model.addAttribute("cargo", cargo);
        model.addAttribute("relacionExterna", relacionExterna);
        return "adm-cargo-edit-relaciones-externas-form";
    }


}
