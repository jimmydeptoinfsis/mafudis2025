package com.app.thym.ddejim.mafudis.controller;

import com.app.thym.ddejim.mafudis.Service.CargoService;
import com.app.thym.ddejim.mafudis.model.Cargo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CargoApiController {
    @Autowired
    private CargoService cargoService;

    @GetMapping("/cargos")
    public ResponseEntity<?> getAllCargos() {
        try {
            List<Cargo> cargos = cargoService.findAll();
            System.out.println("Cargos devueltos: " + cargos);
            return ResponseEntity.ok(cargos);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al cargar los cargos: " + e.getMessage());
        }
    }
}