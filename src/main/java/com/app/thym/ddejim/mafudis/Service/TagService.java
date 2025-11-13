package com.app.thym.ddejim.mafudis.Service;

import com.app.thym.ddejim.mafudis.model.Tag;
import com.app.thym.ddejim.mafudis.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TagService {
    @Autowired
    private TagRepository tagRepository;

    public List<Tag> findAll() {
        return tagRepository.findAll();
    }

    public Optional<Tag> findById(Long id) {
        return tagRepository.findById(id);
    }

    public Tag save(Tag tag) {
        return tagRepository.save(tag);
    }

    public void deleteById(Long id) {
        tagRepository.deleteById(id);
    }
    @Transactional(readOnly = true) // Buena práctica para métodos de lectura
    public List<Tag> findByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>(); // Devolver lista vacía si no hay IDs
        }
        // JpaRepository ya tiene un método findAllById que hace esto
        return tagRepository.findAllById(ids);
    }

}