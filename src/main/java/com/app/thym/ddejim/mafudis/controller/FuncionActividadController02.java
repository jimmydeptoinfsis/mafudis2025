package com.app.thym.ddejim.mafudis.controller;

import com.app.thym.ddejim.mafudis.Service.CargoService;
import com.app.thym.ddejim.mafudis.Service.FuncionActividadService;
import com.app.thym.ddejim.mafudis.model.Cargo;
import com.app.thym.ddejim.mafudis.model.FuncionActividad;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@Controller
@RequestMapping("/funciones-actividades01") // Changed to hyphen
public class FuncionActividadController02 {

    @Autowired
    private FuncionActividadService funcionActividadService;

    @Autowired
    private CargoService cargoService;

    @GetMapping("/cargo/{cargoId}")
    public String listFuncionesActividadesPorCargo(@PathVariable Long cargoId, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));
        List<FuncionActividad> funcionesActividades = funcionActividadService.findByCargoId(cargoId);

        model.addAttribute("cargo", cargo);
        model.addAttribute("funcionesActividades", funcionesActividades);
        return "funciones_actividades/list"; // Changed to "list" to use list.html
    }

    @GetMapping("/cargo/{cargoId}/new")
    public String showNewFuncionActividadForm(@PathVariable Long cargoId, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));
        FuncionActividad funcionActividad = new FuncionActividad();
        funcionActividad.setCargo(cargo);
        model.addAttribute("cargo", cargo);
        model.addAttribute("funcionActividad", funcionActividad);
        model.addAttribute("formAction", "/funciones-actividades/cargo/" + cargoId); // Set form action
        return "funciones_actividades/form";
    }

    @PostMapping("/cargo/{cargoId}")
    public String saveFuncionActividad(@PathVariable Long cargoId,
                                       @Valid @ModelAttribute("funcionActividad") FuncionActividad funcionActividad,
                                       BindingResult result, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));
        funcionActividad.setCargo(cargo);

        if (result.hasErrors()) {
            model.addAttribute("cargo", cargo);
            return "funciones_actividades/form";
        }

        funcionActividadService.save(funcionActividad);
        return "redirect:/funciones-actividades/cargo/" + cargoId;
    }

    @GetMapping("/cargo/{cargoId}/edit/{id}")
    public String showEditFuncionActividadForm(@PathVariable Long cargoId, @PathVariable Long id, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));
        FuncionActividad funcionActividad = funcionActividadService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Función o actividad no encontrada: " + id));
        model.addAttribute("cargo", cargo);
        model.addAttribute("funcionActividad", funcionActividad);
        model.addAttribute("formAction", "/funciones-actividades/cargo/" + cargoId + "/edit/" + id); // Set form action
        return "funciones_actividades/form";
    }

    @PostMapping("/cargo/{cargoId}/edit/{id}")
    public String updateFuncionActividad(@PathVariable Long cargoId, @PathVariable Long id,
                                         @Valid @ModelAttribute("funcionActividad") FuncionActividad funcionActividad,
                                         BindingResult result, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));
        funcionActividad.setCargo(cargo);
        funcionActividad.setId(id);

        if (result.hasErrors()) {
            model.addAttribute("cargo", cargo);
            return "funciones_actividades/form";
        }

        funcionActividadService.save(funcionActividad);
        return "redirect:/funciones-actividades/cargo/" + cargoId;
    }

    @GetMapping("/cargo/{cargoId}/delete/{id}")
    public String deleteFuncionActividad(@PathVariable Long cargoId, @PathVariable Long id) {
        funcionActividadService.deleteById(id);
        return "redirect:/funciones-actividades/cargo/" + cargoId;
    }

    @PostMapping("/cargo/{cargoId}/reorder")
    @ResponseBody
    public ResponseEntity<?> reorderFuncionesActividades(@PathVariable Long cargoId, @RequestBody List<Long> orderedIds) {
        try {
            funcionActividadService.updateOrder(cargoId, orderedIds);
            return ResponseEntity.ok("Orden actualizado correctamente.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar el orden: " + e.getMessage());
        }
    }

    @PostMapping("/{cargoId}/funciones-actividades/crear")
    public String crearFuncionActividad(@PathVariable Long cargoId,
                                        @Valid @ModelAttribute("nuevaFuncionActividad") FuncionActividad nuevaFuncionActividad,
                                        BindingResult result, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));
        nuevaFuncionActividad.setCargo(cargo);

        if (result.hasErrors()) {
            model.addAttribute("cargo", cargo);
            model.addAttribute("funcionesActividades", funcionActividadService.findByCargoId(cargoId));
            return "funciones_actividades/funciones_actividades"; // Updated path
        }

        funcionActividadService.save(nuevaFuncionActividad);
        return "redirect:/funciones-actividades/cargo/" + cargoId;
    }

    @PostMapping("/{cargoId}/funciones-actividades/editar/{id}")
    public String editarFuncionActividad(@PathVariable Long cargoId, @PathVariable Long id,
                                         @Valid @ModelAttribute("funcionActividad") FuncionActividad funcionActividad,
                                         BindingResult result, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));
        funcionActividad.setCargo(cargo);
        funcionActividad.setId(id);

        if (result.hasErrors()) {
            model.addAttribute("cargo", cargo);
            model.addAttribute("funcionesActividades", funcionActividadService.findByCargoId(cargoId));
            return "funciones_actividades/funciones_actividades"; // Updated path
        }

        funcionActividadService.save(funcionActividad);
        return "redirect:/funciones-actividades/cargo/" + cargoId;
    }

    @PostMapping("/{cargoId}/funciones-actividades/eliminar/{id}")
    public String eliminarFuncionActividad(@PathVariable Long cargoId, @PathVariable Long id) {
        funcionActividadService.deleteById(id);
        return "redirect:/funciones-actividades/cargo/" + cargoId;
    }

    @PostMapping("/{cargoId}/funciones-actividades/reordenar")
    @ResponseBody
    public ResponseEntity<?> reordenarFuncionesActividades(@PathVariable Long cargoId, @RequestBody List<Long> orderedIds) {
        try {
            funcionActividadService.updateOrder(cargoId, orderedIds);
            return ResponseEntity.ok("Orden actualizado correctamente.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar el orden: " + e.getMessage());
        }
    }
}