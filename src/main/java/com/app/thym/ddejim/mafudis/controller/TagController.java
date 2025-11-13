package com.app.thym.ddejim.mafudis.controller;

import com.app.thym.ddejim.mafudis.Service.TagService;
import com.app.thym.ddejim.mafudis.model.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/tags")
public class TagController {
    @Autowired
    private TagService tagService;

    // Listar todas las etiquetas
    @GetMapping
    public String listTags(Model model) {
        model.addAttribute("tags", tagService.findAll());
        return "tags/list";
    }

    // Mostrar formulario para crear una nueva etiqueta
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("tag", new Tag());
        return "tags/form";
    }

    // Guardar una etiqueta (crear o actualizar)
    @PostMapping
    public String saveTag(@ModelAttribute("tag") Tag tag) {
        tagService.save(tag);
        return "redirect:/tags";
    }

    // Mostrar formulario para editar una etiqueta
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Tag tag = tagService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Etiqueta no encontrada: " + id));
        model.addAttribute("tag", tag);
        return "tags/form";
    }

    // Eliminar una etiqueta
    @GetMapping("/delete/{id}")
    public String deleteTag(@PathVariable("id") Long id) {
        tagService.deleteById(id);
        return "redirect:/tags";
    }
}