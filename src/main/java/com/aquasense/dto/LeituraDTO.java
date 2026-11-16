package com.aquasense.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeituraDTO {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotNull(message = "O localId é obrigatório.")
    @Schema(example = "1")
    private Long localId;

    // Nível da água em centímetros
    @NotNull(message = "O nível da água é obrigatório.")
    @DecimalMin(value = "0.0", message = "O nível da água não pode ser negativo.")
    @DecimalMax(value = "1000.0", message = "O nível da água deve ser de no máximo 1000 cm.")
    @Schema(example = "30")
    private Double nivelAgua;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime dataHora;
}
