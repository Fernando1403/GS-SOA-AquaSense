package com.aquasense.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class AlertaDTO {

    private Long id;
    private String local;
    private Double nivelAgua;
    private String nivelRisco;
    private String mensagem;
    private LocalDateTime dataGeracao;
}
