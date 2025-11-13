package com.app.thym.ddejim.mafudis.controller;

import com.app.thym.ddejim.mafudis.Service.CargoService;
import com.app.thym.ddejim.mafudis.Service.DocumentosGeneraService;
import com.app.thym.ddejim.mafudis.dto.Breadcrumb;
import com.app.thym.ddejim.mafudis.model.Cargo;
import com.app.thym.ddejim.mafudis.model.DocumentosGenera;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/documentos-genera")
public class DocumentosGeneraController {

    @Autowired
    private DocumentosGeneraService documentosGeneraService;

    @Autowired
    private CargoService cargoService;

    // --- INICIO: MÉTODO AUXILIAR PARA BREADCRUMBS ---
    private void addDocumentosGeneraBreadcrumbs(Long cargoId, String currentPageLabel, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));

        List<Breadcrumb> breadcrumbs = new ArrayList<>();
        breadcrumbs.add(new Breadcrumb("Inicio", "/dashboard")); // Asumiendo que esta es la URL de la lista de cargos
        breadcrumbs.add(new Breadcrumb("Organigrama de Cargos", "/organigrama12")); // La página actual no necesita URL
        breadcrumbs.add(new Breadcrumb("Detalle del cargo", "/cargos/adm/details/" + cargoId));

        if (currentPageLabel != null && !currentPageLabel.isEmpty()) {
            breadcrumbs.add(new Breadcrumb("Documentos que Genera", "/documentos-genera/adm/cargo/" + cargoId));
            breadcrumbs.add(new Breadcrumb(currentPageLabel, null)); // Página actual
        } else {
            breadcrumbs.add(new Breadcrumb("Documentos que Genera", null)); // Página actual
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


    @GetMapping("/cargo/{cargoId}")
    public String listDocumentosGeneraPorCargo(@PathVariable Long cargoId, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));
        List<DocumentosGenera> documentosGenera = documentosGeneraService.findByCargoId(cargoId);

        model.addAttribute("cargo", cargo);
        model.addAttribute("documentosGenera", documentosGenera);

        return "documentos_genera/list";
    }

    @GetMapping("/cargo/{cargoId}/new")
    public String showNewDocumentosGeneraForm(@PathVariable Long cargoId, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));
        DocumentosGenera documentosGenera = new DocumentosGenera();
        documentosGenera.setCargo(cargo);
        model.addAttribute("cargo", cargo);
        model.addAttribute("documentosGenera", documentosGenera);
        model.addAttribute("formAction", "/documentos-genera/cargo/" + cargoId);

        return "documentos_genera/form";
    }

    @PostMapping("/cargo/{cargoId}")
    public String saveDocumentosGenera(@PathVariable Long cargoId,
                                       @Valid @ModelAttribute("documentosGenera") DocumentosGenera documentosGenera,
                                       BindingResult result, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));
        documentosGenera.setCargo(cargo);

        if (result.hasErrors()) {
            model.addAttribute("cargo", cargo);
            return "documentos_genera/form";
        }

        documentosGeneraService.save(documentosGenera);
        return "redirect:/documentos-genera/cargo/" + cargoId;
    }

    @GetMapping("/cargo/{cargoId}/edit/{id}")
    public String showEditDocumentosGeneraForm(@PathVariable Long cargoId, @PathVariable Long id, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));
        DocumentosGenera documentosGenera = documentosGeneraService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Documento no encontrado: " + id));
        model.addAttribute("cargo", cargo);
        model.addAttribute("documentosGenera", documentosGenera);
        model.addAttribute("formAction", "/documentos-genera/cargo/" + cargoId + "/edit/" + id);
        addDocumentosGeneraBreadcrumbs(cargoId, "Editar", model);
        return "documentos_genera/form";
    }

    @PostMapping("/cargo/{cargoId}/edit/{id}")
    public String updateDocumentosGenera(@PathVariable Long cargoId, @PathVariable Long id,
                                         @Valid @ModelAttribute("documentosGenera") DocumentosGenera documentosGenera,
                                         BindingResult result, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));
        documentosGenera.setCargo(cargo);
        documentosGenera.setId(id);

        if (result.hasErrors()) {
            model.addAttribute("cargo", cargo);
            return "documentos_genera/form";
        }

        documentosGeneraService.save(documentosGenera);
        return "redirect:/documentos-genera/cargo/" + cargoId;
    }

    @GetMapping("/cargo/{cargoId}/delete/{id}")
    public String deleteDocumentosGenera(@PathVariable Long cargoId, @PathVariable Long id) {
        documentosGeneraService.deleteById(id);
        return "redirect:/documentos-genera/cargo/" + cargoId;
    }

    @PostMapping("/cargo/{cargoId}/reorder")
    @ResponseBody
    public ResponseEntity<?> reorderDocumentosGenera(@PathVariable Long cargoId, @RequestBody List<Long> orderedIds) {
        try {
            documentosGeneraService.updateOrder(cargoId, orderedIds);
            return ResponseEntity.ok("Orden actualizado correctamente.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar el orden: " + e.getMessage());
        }
    }

    @GetMapping("/adm/cargo/{cargoId}")
    public String listDocumentosGeneraPorCargoadm(@PathVariable Long cargoId, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));
        List<DocumentosGenera> documentosGenera = documentosGeneraService.findByCargoId(cargoId);

        model.addAttribute("cargo", cargo);
        model.addAttribute("documentosGenera", documentosGenera);
        addDocumentosGeneraBreadcrumbs(cargoId, null, model);
        return "adm-cargo-edit-documentos-genera-list";
    }

    @GetMapping("/adm/cargo/{cargoId}/new")
    public String showNewDocumentosGeneraFormadm(@PathVariable Long cargoId, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));
        DocumentosGenera documentosGenera = new DocumentosGenera();
        documentosGenera.setCargo(cargo);
        model.addAttribute("cargo", cargo);
        model.addAttribute("documentosGenera", documentosGenera);
        model.addAttribute("formAction", "/documentos-genera/adm/cargo/" + cargoId);
        addDocumentosGeneraBreadcrumbs(cargoId, "Nuevo", model);
        return "adm-cargo-edit-documentos-genera-form";
    }

    @PostMapping("/adm/cargo/{cargoId}")
    public String saveDocumentosGeneraadm(@PathVariable Long cargoId,
                                       @Valid @ModelAttribute("documentosGenera") DocumentosGenera documentosGenera,
                                       BindingResult result, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));
        documentosGenera.setCargo(cargo);

        if (result.hasErrors()) {
            model.addAttribute("cargo", cargo);
            addDocumentosGeneraBreadcrumbs(cargoId, documentosGenera.getId() == null ? "Nuevo" : "Editar", model);
            return "adm-cargo-edit-documentos-genera-form";
        }

        documentosGeneraService.save(documentosGenera);
        return "redirect:/documentos-genera/adm/cargo/" + cargoId;
    }

    @GetMapping("/adm/cargo/{cargoId}/edit/{id}")
    public String showEditDocumentosGeneraFormadm(@PathVariable Long cargoId, @PathVariable Long id, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));
        DocumentosGenera documentosGenera = documentosGeneraService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Documento no encontrado: " + id));
        model.addAttribute("cargo", cargo);
        model.addAttribute("documentosGenera", documentosGenera);
        model.addAttribute("formAction", "/documentos-genera/adm/cargo/" + cargoId + "/edit/" + id);
        addDocumentosGeneraBreadcrumbs(cargoId, "Editar", model);
        return "adm-cargo-edit-documentos-genera-form";
    }

    @PostMapping("/adm/cargo/{cargoId}/edit/{id}")
    public String updateDocumentosGeneraadm(@PathVariable Long cargoId, @PathVariable Long id,
                                            @Valid @ModelAttribute("documentosGenera") DocumentosGenera documentosGenera,
                                            BindingResult result, Model model) {
        Cargo cargo = cargoService.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoId));

        DocumentosGenera existingDocumento = documentosGeneraService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Documento no encontrado: " + id));

        // IMPORTANTE: Preservar el orderIndex original
        Integer originalOrderIndex = existingDocumento.getOrderIndex();

        existingDocumento.setDescripcion(documentosGenera.getDescripcion());
        existingDocumento.setCargo(cargo);
        existingDocumento.setOrderIndex(originalOrderIndex); // ← MANTENER EL ORDEN ORIGINAL

        if (result.hasErrors()) {
            model.addAttribute("cargo", cargo);
            addDocumentosGeneraBreadcrumbs(cargoId, "Editar", model);
            return "adm-cargo-edit-documentos-genera-form";
        }

        documentosGeneraService.save(existingDocumento);
        return "redirect:/documentos-genera/adm/cargo/" + cargoId;
    }

    @GetMapping("/adm/cargo/{cargoId}/delete/{id}")
    public String deleteDocumentosGeneraadm(@PathVariable Long cargoId, @PathVariable Long id) {
        documentosGeneraService.deleteById(id);
        return "redirect:/documentos-genera/adm/cargo/" + cargoId;
    }

    @PostMapping("/adm/cargo/{cargoId}/reorder")
    @ResponseBody
    public ResponseEntity<?> reorderDocumentosGeneraadm(@PathVariable Long cargoId, @RequestBody List<Long> orderedIds) {
        try {
            documentosGeneraService.updateOrder(cargoId, orderedIds);
            return ResponseEntity.ok("Orden actualizado correctamente.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar el orden: " + e.getMessage());
        }
    }
}
