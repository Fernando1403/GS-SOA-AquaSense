package com.aquasense.controller;

import com.aquasense.dto.AlertaDTO;
import com.aquasense.interfaces.IAlertaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alertas")
@Tag(name = "Alertas")
public class AlertaController {

    private final IAlertaService alertaService;

    public AlertaController(IAlertaService alertaService) {
        this.alertaService = alertaService;
    }

    @Operation(summary = "Gera um alerta a partir da última leitura do local")
    @PostMapping("/gerar/{localId}")
    public ResponseEntity<AlertaDTO> gerar(@PathVariable Long localId) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(alertaService.gerarAlerta(localId));
    }

    @Operation(summary = "Lista os alertas gerados (mais recentes primeiro)")
    @GetMapping
    public List<AlertaDTO> listar() {
        return alertaService.listar();
    }
}
