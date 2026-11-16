package com.aquasense.controller;

import com.aquasense.dto.LeituraDTO;
import com.aquasense.interfaces.ILeituraService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/leituras")
@Tag(name = "Leituras")
public class LeituraController {

    private final ILeituraService leituraService;

    public LeituraController(ILeituraService leituraService) {
        this.leituraService = leituraService;
    }

    @Operation(summary = "Registra uma leitura de nível de água (enviada pelo sensor ESP32)")
    @PostMapping
    public ResponseEntity<LeituraDTO> registrar(@Valid @RequestBody LeituraDTO dto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(leituraService.registrar(dto));
    }
}
