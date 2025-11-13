package com.app.thym.ddejim.mafudis.Service;

import com.app.thym.ddejim.mafudis.model.FuncionGeneralUnidad;
import com.app.thym.ddejim.mafudis.repository.FuncionGeneralUnidadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FuncionGeneralUnidadService {

    @Autowired
    private FuncionGeneralUnidadRepository funcionGeneralUnidadRepository;

    public List<FuncionGeneralUnidad> findByUnidadId(Long unidadId) {
        return funcionGeneralUnidadRepository.findByUnidadId(unidadId);
    }

    public Optional<FuncionGeneralUnidad> findById(Long id) {
        return funcionGeneralUnidadRepository.findById(id);
    }

    public FuncionGeneralUnidad save(FuncionGeneralUnidad funcion) {
        try {
            if (funcion.getId() == null) { // New funcion
                List<FuncionGeneralUnidad> funciones = findByUnidadId(funcion.getUnidad().getId());
                int maxOrder = funciones.stream()
                        .mapToInt(obj -> obj.getOrderIndex() != null ? obj.getOrderIndex() : 0)
                        .max()
                        .orElse(0);
                funcion.setOrderIndex(maxOrder + 1);
            }
            FuncionGeneralUnidad saved = funcionGeneralUnidadRepository.save(funcion);
            return saved;
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar la  funcion: " + e.getMessage(), e);
        }
    }

    public void deleteById(Long id) {
        try {
            funcionGeneralUnidadRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar la funcion con id " + id + ": " + e.getMessage(), e);
        }
    }

    @Transactional
    public void updateOrder(Long unidadId, List<Long> orderedIds) {
        List<FuncionGeneralUnidad> funciones = funcionGeneralUnidadRepository.findByUnidadId(unidadId);

        Map<Long, FuncionGeneralUnidad> funcionesMap = funciones.stream()
                .collect(Collectors.toMap(FuncionGeneralUnidad::getId, funcion -> funcion));

        for (int i = 0; i < orderedIds.size(); i++) {
            Long funcionId = orderedIds.get(i);
            FuncionGeneralUnidad funcion = funcionesMap.get(funcionId);
            if (funcion != null) {
                funcion.setOrderIndex(i + 1); // Asigna el nuevo orden
            }
        }

        // Guarda TODOS los cambios en la base de datos en una sola operación.
        funcionGeneralUnidadRepository.saveAll(funcionesMap.values());
        funcionGeneralUnidadRepository.flush();
    }
}
