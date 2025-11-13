package com.app.thym.ddejim.mafudis.controller;

import com.app.thym.ddejim.mafudis.Service.UnidadService;
import com.app.thym.ddejim.mafudis.model.Cargo;
import com.app.thym.ddejim.mafudis.model.Unidad;
import com.app.thym.ddejim.mafudis.repository.CargoRepository;
import com.app.thym.ddejim.mafudis.repository.UnidadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class OrganigramaController {

    private static final Logger logger = LoggerFactory.getLogger(OrganigramaController.class);

    @Autowired
    private CargoRepository cargoRepository;

    @Autowired
    private UnidadRepository unidadRepository;

    @Autowired
    private UnidadService unidadService;

    @GetMapping("/organigrama")
    public String showOrganigrama() {
        return "organigrama";
    }

    @GetMapping("/organigrama1")
    public String showOrganigrama1(Model model) {
        try {
            List<Cargo> cargos = cargoRepository.findAll();
            logger.info("Cargos encontrados: {}", cargos.size());
            model.addAttribute("cargos", cargos);
            return "organigrama2";
        } catch (Exception e) {
            logger.error("Error en showOrganigrama1: ", e);
            model.addAttribute("error", "Error al cargar los datos del organigrama");
            return "error";
        }
    }

    @GetMapping("/organigrama2")
    public String showOrganigrama2(Model model) {
        try {
            List<Cargo> cargos = cargoRepository.findAll();
            model.addAttribute("cargos", cargos);
            return "organigramah";
        } catch (Exception e) {
            logger.error("Error en showOrganigrama2: ", e);
            model.addAttribute("error", "Error al cargar los datos del organigrama");
            return "error";
        }
    }

    @GetMapping("/organigrama3")
    public String showOrganigrama3(Model model) {
        try {
            List<Cargo> cargos = cargoRepository.findAll();
            model.addAttribute("cargos", cargos);
            return "organigrama1";
        } catch (Exception e) {
            logger.error("Error en showOrganigrama3: ", e);
            model.addAttribute("error", "Error al cargar los datos del organigrama");
            return "error";
        }
    }

    @GetMapping("/organigrama4")
    public String showOrganigrama4(Model model) {
        try {
            List<Cargo> cargos = cargoRepository.findAll();
            model.addAttribute("cargos", cargos);
            return "organigrama3";
        } catch (Exception e) {
            logger.error("Error en showOrganigrama4: ", e);
            model.addAttribute("error", "Error al cargar los datos del organigrama");
            return "error";
        }
    }

    @GetMapping("/organigrama5")
    public String showOrganigrama5(Model model) {
        try {
            List<Cargo> cargos = cargoRepository.findAll();
            model.addAttribute("cargos", cargos);
            return "organigrama4";
        } catch (Exception e) {
            logger.error("Error en showOrganigrama5: ", e);
            model.addAttribute("error", "Error al cargar los datos del organigrama");
            return "error";
        }
    }

    @GetMapping("/organigrama6")
    public String showOrganigrama6(Model model) {
        try {
            List<Cargo> cargos = cargoRepository.findAll();
            model.addAttribute("cargos", cargos);
            return "organigrama5";
        } catch (Exception e) {
            logger.error("Error en showOrganigrama6: ", e);
            model.addAttribute("error", "Error al cargar los datos del organigrama");
            return "error";
        }
    }

    @GetMapping("/organigrama7")
    public String showOrganigrama7(Model model) {
        try {
            List<Cargo> cargos = cargoRepository.findAll();
            model.addAttribute("cargos", cargos);
            return "organigrama6";
        } catch (Exception e) {
            logger.error("Error en showOrganigrama7: ", e);
            model.addAttribute("error", "Error al cargar los datos del organigrama");
            return "error";
        }
    }

    @GetMapping("/organigrama8")
    public String showOrganigrama8(Model model) {
        try {
            List<Unidad> unidades = unidadRepository.findAll().stream().map(unidad -> {
                // Prepare Unidad for JSON serialization - manejo seguro de null
                if (unidad.getTipoUnidad() != null) {
                    unidad.setTipoUnidadName(unidad.getTipoUnidad().getNombre() != null ?
                            unidad.getTipoUnidad().getNombre() : "N/A");
                } else {
                    unidad.setTipoUnidadName("N/A");
                }
                return unidad;
            }).collect(Collectors.toList());

            logger.info("Unidades encontradas: {}", unidades.size());
            model.addAttribute("unidades", unidades);
            return "organigrama7";
        } catch (Exception e) {
            logger.error("Error en showOrganigrama8: ", e);
            model.addAttribute("error", "Error al cargar los datos del organigrama");
            return "error";
        }
    }

    @GetMapping("/organigrama9")
    public String showOrganigrama9(Model model) {
        try {
            List<Cargo> cargos = cargoRepository.findAll();
            model.addAttribute("cargos", cargos);
            return "protos/organigrama1";
        } catch (Exception e) {
            logger.error("Error en showOrganigrama9: ", e);
            model.addAttribute("error", "Error al cargar los datos del organigrama");
            return "error";
        }
    }

    @GetMapping("/organigrama10")
    public String showOrganigrama10(Model model) {
        try {
            List<Unidad> unidades = unidadRepository.findAll();
            logger.info("Unidades para organigrama10: {}", unidades.size());
            model.addAttribute("unidades", unidades);
            return "organigrama10";
        } catch (Exception e) {
            logger.error("Error en showOrganigrama10: ", e);
            model.addAttribute("error", "Error al cargar los datos del organigrama");
            return "error";
        }
    }

    @GetMapping("/organigrama11")
    public String showOrganigrama11(Model model) {
        try {
            List<Cargo> cargos = cargoRepository.findAll();
            model.addAttribute("cargos", cargos);
            return "organigrama11";
        } catch (Exception e) {
            logger.error("Error en showOrganigrama11: ", e);
            model.addAttribute("error", "Error al cargar los datos del organigrama");
            return "error";
        }
    }

    @GetMapping("/organigrama12")
    public String showOrganigrama12(Model model) {
        try {
            List<Cargo> cargos = cargoRepository.findAll();
            model.addAttribute("cargos", cargos);
            return "organigrama12";
        } catch (Exception e) {
            logger.error("Error en showOrganigrama12: ", e);
            model.addAttribute("error", "Error al cargar los datos del organigrama");
            return "error";
        }
    }
    @GetMapping("/organigrama13")
    public String showOrganigrama13(Model model) {
        try {
            List<Unidad> unidades = unidadRepository.findAll().stream().map(unidad -> {
                // Prepare Unidad for JSON serialization - manejo seguro de null
                if (unidad.getTipoUnidad() != null) {
                    unidad.setTipoUnidadName(unidad.getTipoUnidad().getNombre() != null ?
                            unidad.getTipoUnidad().getNombre() : "N/A");
                } else {
                    unidad.setTipoUnidadName("N/A");
                }
                return unidad;
            }).collect(Collectors.toList());

            logger.info("Unidades encontradas: {}", unidades.size());
            model.addAttribute("unidades", unidades);
            return "organigrama13";
        } catch (Exception e) {
            logger.error("Error en showOrganigrama8: ", e);
            model.addAttribute("error", "Error al cargar los datos del organigrama");
            return "error";
        }
    }

    @GetMapping("/organigrama14")
    public String showOrganigrama14(Model model) {
        try {
            List<Unidad> unidades = unidadRepository.findAll().stream().map(unidad -> {
                // Prepare Unidad for JSON serialization - manejo seguro de null
                if (unidad.getTipoUnidad() != null) {
                    unidad.setTipoUnidadName(unidad.getTipoUnidad().getNombre() != null ?
                            unidad.getTipoUnidad().getNombre() : "N/A");
                } else {
                    unidad.setTipoUnidadName("N/A");
                }
                return unidad;
            }).collect(Collectors.toList());

            logger.info("Unidades encontradas: {}", unidades.size());
            model.addAttribute("unidades", unidades);
            return "organigrama14";
        } catch (Exception e) {
            logger.error("Error en showOrganigrama8: ", e);
            model.addAttribute("error", "Error al cargar los datos del organigrama");
            return "error";
        }
    }

    @GetMapping("/organigrama15")
    public String showOrganigrama15(Model model) {
        try {
            List<Cargo> cargos = cargoRepository.findAll();
            model.addAttribute("cargos", cargos);
            return "organigrama15";
        } catch (Exception e) {
            logger.error("Error en showOrganigrama12: ", e);
            model.addAttribute("error", "Error al cargar los datos del organigrama");
            return "error";
        }
    }
    @GetMapping("/organigrama-fragment")
    public String showOrganigramaFragment(Model model) {
        try {
            List<Cargo> cargos = cargoRepository.findAll();
            cargos.forEach(cargo -> {
                if (cargo.getTags() == null) {
                    cargo.setTags(new java.util.ArrayList<>());
                }
            });
            logger.info("Cargos para el fragmento del organigrama: {}", cargos.size());
            model.addAttribute("cargos", cargos);

            // Apunta directamente al archivo y al nombre del fragmento. Correcto.
            return "organigrama-fragment :: organigrama-content";

        } catch (Exception e) {
            logger.error("Error en showOrganigramaFragment: ", e);
            model.addAttribute("error", "Error al cargar el fragmento del organigrama");
            return "organigrama-fragment :: organigrama-content";
        }
    }
}