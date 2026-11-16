package com.aquasense.controller;

import com.aquasense.dto.LeituraDTO;
import com.aquasense.dto.LocalDTO;
import com.aquasense.interfaces.ILeituraService;
import com.aquasense.interfaces.ILocalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/locais")
@Tag(name = "Locais")
public class LocalController {

    private final ILocalService localService;
    private final ILeituraService leituraService;

    public LocalController(ILocalService localService, ILeituraService leituraService) {
        this.localService = localService;
        this.leituraService = leituraService;
    }

    @Operation(summary = "Lista os locais monitorados")
    @GetMapping
    public List<LocalDTO> listar() {
        return localService.listar();
    }

    @Operation(summary = "Cadastra um novo local monitorado")
    @PostMapping
    public ResponseEntity<LocalDTO> criar(@Valid @RequestBody LocalDTO dto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(localService.criar(dto));
    }

    @Operation(summary = "Lista as leituras de um local (mais recentes primeiro)")
    @GetMapping("/{id}/leituras")
    public List<LeituraDTO> listarLeituras(@PathVariable Long id) {
        return leituraService.listarPorLocal(id);
    }
}
