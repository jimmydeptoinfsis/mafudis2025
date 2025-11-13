package com.app.thym.ddejim.mafudis.controller;

import com.app.thym.ddejim.mafudis.Service.*;
import com.app.thym.ddejim.mafudis.dto.CargoDTO;
import com.app.thym.ddejim.mafudis.dto.CargoDetalleDTO;
import com.app.thym.ddejim.mafudis.dto.UnidadDetalleDTO;
import com.app.thym.ddejim.mafudis.model.*;
import com.app.thym.ddejim.mafudis.repository.RelacionInternaRepository;
import com.app.thym.ddejim.mafudis.repository.UnidadRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ApiRestController {

    @Autowired
    private UnidadRepository unidadRepository;

    @Autowired
    private CargoService cargoService;

    @Autowired
    private RelacionInternaRepository relacionInternaRepository;

    @Autowired
    private ResponsabilidadService responsabilidadService;
    @Autowired
    private RelacionInternaService relacionInternaService;
    @Autowired
    private FuncionActividadService funcionActividadService;
    @Autowired
    private DocumentosGeneraService documentosGeneraService;
    @Autowired
    private PerfilContratacionService perfilContratacionService;
    @Autowired
    private GradoAcademicoMinimoService gradoAcademicoMinimoService;
    @Autowired
    private OtrosConocimientosService otrosConocimientosService;
    @Autowired
    private HabilidadesDestrezasService habilidadesDestrezasService;


    @Transactional(readOnly = true)
    @GetMapping("/unidades/{id}")
    public ResponseEntity<UnidadDetalleDTO> getUnidadDetails(@PathVariable("id") Long id) {
        Unidad unidad = unidadRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unidad no encontrada: " + id));

        // Forzar la carga de datos relacionados
        Hibernate.initialize(unidad.getCargos());
        Hibernate.initialize(unidad.getObjetivos());
        Hibernate.initialize(unidad.getFuncionesGenerales());
        Hibernate.initialize(unidad.getCoordinaCon());
        Hibernate.initialize(unidad.getEsCoordinadoPor());
        Hibernate.initialize(unidad.getDependencia());

        // Crear el DTO
        UnidadDetalleDTO dto = new UnidadDetalleDTO();
        dto.setNombreUnidad(unidad.getNombreUnidad());
        dto.setTipoUnidad(unidad.getTipoUnidad() != null ? unidad.getTipoUnidad().getNombre() : "No especificado");
        dto.setClasificacion(unidad.getClasificacion() != null ? unidad.getClasificacion().getNombre() : "No especificado");
        dto.setNivelJerarquico(unidad.getNivelJerarquico() != null ? unidad.getNivelJerarquico().getNombre() : "No especificado");
        dto.setDependencia(unidad.getDependencia() != null ? unidad.getDependencia().getNombre() : "Nivel Superior");

        // Cargos
        dto.setCargos(unidad.getCargos().stream()
                .map(cargo -> new UnidadDetalleDTO.CargoSimpleDTO(cargo.getName()))
                .collect(Collectors.toList()));

        // Objetivos
        dto.setObjetivos(unidad.getObjetivos().stream()
                .sorted(Comparator.comparing(ObjetivoUnidad::getOrderIndex))
                .map(ObjetivoUnidad::getDescripcion)
                .collect(Collectors.toList()));

        // Funciones
        dto.setFunciones(unidad.getFuncionesGenerales().stream()
                .sorted(Comparator.comparing(FuncionGeneralUnidad::getOrderIndex))
                .map(FuncionGeneralUnidad::getDescripcion)
                .collect(Collectors.toList()));

        // Relaciones Externas (sin duplicados y ordenadas)
        Set<RelacionExterna> relacionesExternasUnicas = unidad.getCargos().stream()
                .flatMap(cargo -> {
                    Hibernate.initialize(cargo.getRelacionesExternas());
                    return cargo.getRelacionesExternas().stream();
                })
                .collect(Collectors.toSet());

        dto.setRelacionesExternas(relacionesExternasUnicas.stream()
                .map(rel -> new UnidadDetalleDTO.RelacionExternaSimpleDTO(rel.getNombre()))
                .sorted(Comparator.comparing(UnidadDetalleDTO.RelacionExternaSimpleDTO::getNombre))
                .collect(Collectors.toList()));

        // *** CORRECCIÓN: RELACIONES INTERNAS CON DEBUG ***
        Set<Long> idsCargosDeLaUnidad = unidad.getCargos().stream()
                .map(Cargo::getId)
                .collect(Collectors.toSet());

        System.out.println("=== DEBUG RELACIONES INTERNAS ===");
        System.out.println("Unidad: " + unidad.getNombreUnidad() + " (ID: " + id + ")");
        System.out.println("Cargos de la unidad: " + idsCargosDeLaUnidad);

        if (!idsCargosDeLaUnidad.isEmpty()) {
            // Buscar relaciones donde los cargos de esta unidad participan
            List<RelacionInterna> relacionesComoOrigen = relacionInternaRepository.findByOrigenIdIn(new ArrayList<>(idsCargosDeLaUnidad));
            List<RelacionInterna> relacionesComoDestino = relacionInternaRepository.findByDestinoIdIn(new ArrayList<>(idsCargosDeLaUnidad));

            System.out.println("Relaciones como origen encontradas: " + relacionesComoOrigen.size());
            System.out.println("Relaciones como destino encontradas: " + relacionesComoDestino.size());

            // Forzar inicialización de las relaciones cargadas
            relacionesComoOrigen.forEach(rel -> {
                Hibernate.initialize(rel.getOrigen());
                Hibernate.initialize(rel.getDestino());
                if (rel.getOrigen() != null) {
                    Hibernate.initialize(rel.getOrigen().getUnidad());
                }
                if (rel.getDestino() != null) {
                    Hibernate.initialize(rel.getDestino().getUnidad());
                }
            });

            relacionesComoDestino.forEach(rel -> {
                Hibernate.initialize(rel.getOrigen());
                Hibernate.initialize(rel.getDestino());
                if (rel.getOrigen() != null) {
                    Hibernate.initialize(rel.getOrigen().getUnidad());
                }
                if (rel.getDestino() != null) {
                    Hibernate.initialize(rel.getDestino().getUnidad());
                }
            });

            // Combinar ambas listas
            Set<RelacionInterna> relacionesTotales = new HashSet<>();
            relacionesTotales.addAll(relacionesComoOrigen);
            relacionesTotales.addAll(relacionesComoDestino);

            System.out.println("Total de relaciones únicas: " + relacionesTotales.size());

            // Debug: Mostrar cada relación antes del filtro
            relacionesTotales.forEach(rel -> {
                System.out.println("  Relación ID: " + rel.getId());
                System.out.println("    Tipo: " + rel.getTipoRelacion());
                System.out.println("    Origen: " + (rel.getOrigen() != null ? rel.getOrigen().getName() : "NULL"));
                System.out.println("    Unidad Origen: " + (rel.getOrigen() != null && rel.getOrigen().getUnidad() != null ? rel.getOrigen().getUnidad().getNombreUnidad() : "NULL"));
                System.out.println("    Destino: " + (rel.getDestino() != null ? rel.getDestino().getName() : "NULL"));
                System.out.println("    Unidad Destino: " + (rel.getDestino() != null && rel.getDestino().getUnidad() != null ? rel.getDestino().getUnidad().getNombreUnidad() : "NULL"));
            });

            // Convertir a DTOs - VERSIÓN FLEXIBLE que acepta cargos sin unidad
            List<UnidadDetalleDTO.RelacionInternaSimpleDTO> relacionesInternasList = relacionesTotales.stream()
                    .filter(rel -> {
                        boolean isValid = rel != null &&
                                rel.getOrigen() != null &&
                                rel.getDestino() != null;
                        if (!isValid) {
                            System.out.println("  ⚠️ Relación filtrada (origen o destino NULL): ID " + (rel != null ? rel.getId() : "NULL"));
                        }
                        return isValid;
                    })
                    .map(rel -> {
                        boolean esOrigen = idsCargosDeLaUnidad.contains(rel.getOrigen().getId());
                        String tipoRelacion = rel.getTipoRelacion();

                        // Determinar el nombre a mostrar
                        String nombreOtraUnidadOCargo;
                        if (esOrigen) {
                            // La unidad actual es el origen, mostrar destino
                            if (rel.getDestino().getUnidad() != null) {
                                nombreOtraUnidadOCargo = rel.getDestino().getUnidad().getNombreUnidad();
                            } else {
                                // Si el cargo destino no tiene unidad, mostrar el nombre del cargo
                                nombreOtraUnidadOCargo = rel.getDestino().getName() ;
                            }
                        } else {
                            // La unidad actual es el destino, mostrar origen
                            if (rel.getOrigen().getUnidad() != null) {
                                nombreOtraUnidadOCargo = rel.getOrigen().getUnidad().getNombreUnidad();
                            } else {
                                // Si el cargo origen no tiene unidad, mostrar el nombre del cargo
                                nombreOtraUnidadOCargo = rel.getOrigen().getName() ;
                            }
                        }

                       /* System.out.println("  ✓ Agregando: " + tipoRelacion + " -> " + nombreOtraUnidadOCargo);*/
                        return new UnidadDetalleDTO.RelacionInternaSimpleDTO(tipoRelacion, nombreOtraUnidadOCargo);
                    })
                    .distinct()
                    .sorted(Comparator.comparing(UnidadDetalleDTO.RelacionInternaSimpleDTO::getTipoRelacion)
                            .thenComparing(UnidadDetalleDTO.RelacionInternaSimpleDTO::getNombreUnidadDestino))
                    .collect(Collectors.toList());

            dto.setRelacionesInternas(relacionesInternasList);

            System.out.println("Relaciones Internas finales: " + relacionesInternasList.size());
            System.out.println("=== FIN DEBUG ===\n");
        } else {
            System.out.println("⚠️ La unidad no tiene cargos asignados");
            System.out.println("=== FIN DEBUG ===\n");
            dto.setRelacionesInternas(new ArrayList<>());
        }

        // Jerarquía (null por ahora como en tu código)
        dto.setJerarquiaUnidad(null);

        return ResponseEntity.ok(dto);
    }


    @Transactional(readOnly = true)
    @GetMapping("/cargos/{id}")
    public ResponseEntity<CargoDetalleDTO> getCargoDetalleCompleto(@PathVariable("id") Long id) {
        Cargo cargo = cargoService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cargo no encontrado: " + id));

        // Forzar inicialización
        Hibernate.initialize(cargo.getTags());
        Hibernate.initialize(cargo.getRelacionesExternas());

        // Crear el DTO
        CargoDetalleDTO dto = new CargoDetalleDTO();
        dto.setNombreCargo(cargo.getName());

        // Superior
        String superiorText = "Ninguno";
        if (cargo.getPid() != null) {
            Optional<Cargo> superiorOptional = cargoService.findById(cargo.getPid());
            superiorText = superiorOptional.map(Cargo::getName).orElse("Nivel Superior");
        }
        dto.setSuperior(superiorText);

        // Dependientes
        List<Cargo> allCargos = cargoService.findAll();
        List<Cargo> dependientes = allCargos.stream()
                .filter(c -> c.getPid() != null && c.getPid().equals(id))
                .collect(Collectors.toList());
        String dependientesText = dependientes.stream()
                .map(Cargo::getName)
                .collect(Collectors.joining(", "));
        dto.setDependientes(dependientesText.isEmpty() ? "Ninguno" : dependientesText);

        // Clasificación
        List<String> tagNames = cargo.getTags().stream().map(Tag::getName).collect(Collectors.toList());
        String clasificacion = tagNames.isEmpty() ? "Lineal" :
                (tagNames.contains("assistant") ? "Staff" : String.join(", ", tagNames));
        dto.setClasificacion(clasificacion);

        // Responsabilidades
        List<Responsabilidad> responsabilidades = responsabilidadService.findByCargoId(id);
        dto.setResponsabilidades(responsabilidades.stream()
                .sorted(Comparator.comparing(Responsabilidad::getOrderIndex))
                .map(Responsabilidad::getDescripcion)
                .collect(Collectors.toList()));

        // Relaciones Internas
        List<RelacionInterna> relacionesComoOrigen = relacionInternaService.findByOrigenId(id);
        List<RelacionInterna> relacionesComoDestino = relacionInternaService.findByDestinoId(id);

        relacionesComoOrigen.forEach(rel -> {
            Hibernate.initialize(rel.getOrigen());
            Hibernate.initialize(rel.getDestino());
        });

        relacionesComoDestino.forEach(rel -> {
            Hibernate.initialize(rel.getOrigen());
            Hibernate.initialize(rel.getDestino());
        });

        List<CargoDetalleDTO.RelacionInternaSimpleDTO> relacionesInternas = new ArrayList<>();
        relacionesComoOrigen.forEach(rel ->
                relacionesInternas.add(new CargoDetalleDTO.RelacionInternaSimpleDTO(
                        rel.getTipoRelacion(),
                        rel.getDestino().getName()
                ))
        );
        relacionesComoDestino.forEach(rel ->
                relacionesInternas.add(new CargoDetalleDTO.RelacionInternaSimpleDTO(
                        rel.getTipoRelacion(),
                        rel.getOrigen().getName()
                ))
        );
        dto.setRelacionesInternas(relacionesInternas);

        // Relaciones Externas
        dto.setRelacionesExternas(cargo.getRelacionesExternas().stream()
                .sorted(Comparator.comparing(RelacionExterna::getOrderIndex))
                .map(RelacionExterna::getNombre)
                .collect(Collectors.toList()));

        // Funciones y Actividades
        List<FuncionActividad> funcionesActividades = funcionActividadService.findByCargoId(id);
        dto.setFuncionesActividades(funcionesActividades.stream()
                .sorted(Comparator.comparing(FuncionActividad::getOrderIndex))
                .map(FuncionActividad::getDescripcion)
                .collect(Collectors.toList()));

        // Documentos que genera
        List<DocumentosGenera> documentosGenera = documentosGeneraService.findByCargoId(id);
        dto.setDocumentosGenera(documentosGenera.stream()
                .sorted(Comparator.comparing(DocumentosGenera::getOrderIndex))
                .map(DocumentosGenera::getDescripcion)
                .collect(Collectors.toList()));

        // Perfil de Contratación
        List<PerfilContratacion> perfilesContratacion = perfilContratacionService.findByCargoId(id);
        dto.setPerfilContratacion(perfilesContratacion.stream()
                .sorted(Comparator.comparing(PerfilContratacion::getOrderIndex))
                .map(PerfilContratacion::getDescripcion)
                .collect(Collectors.toList()));

        // Grado Académico Mínimo
        List<GradoAcademicoMinimo> gradosAcademicos = gradoAcademicoMinimoService.findByCargoId(id);
        dto.setGradoAcademico(gradosAcademicos.stream()
                .sorted(Comparator.comparing(GradoAcademicoMinimo::getOrderIndex))
                .map(GradoAcademicoMinimo::getDescripcion)
                .collect(Collectors.toList()));

        // Otros Conocimientos
        List<OtrosConocimientos> otrosConocimientos = otrosConocimientosService.findByCargoId(id);
        dto.setOtrosConocimientos(otrosConocimientos.stream()
                .sorted(Comparator.comparing(OtrosConocimientos::getOrderIndex))
                .map(OtrosConocimientos::getDescripcion)
                .collect(Collectors.toList()));

        // Habilidades y Destrezas
        List<HabilidadesDestrezas> habilidadesDestrezas = habilidadesDestrezasService.findByCargoId(id);
        dto.setHabilidadesDestrezas(habilidadesDestrezas.stream()
                .sorted(Comparator.comparing(HabilidadesDestrezas::getOrderIndex))
                .map(HabilidadesDestrezas::getDescripcion)
                .collect(Collectors.toList()));

        return ResponseEntity.ok(dto);
    }


/*    @Transactional(readOnly = true)
    @GetMapping("/cargos/{id}")
    public ResponseEntity<CargoDTO> getCargoDetails(@PathVariable("id") Long id) {
        Cargo cargo = cargoService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cargo no encontrado: " + id));

        CargoDTO dto = new CargoDTO(cargo.getName());
        return ResponseEntity.ok(dto);
    }*/
}