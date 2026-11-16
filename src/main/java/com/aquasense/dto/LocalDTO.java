package com.aquasense.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocalDTO {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "O nome é obrigatório.")
    @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres.")
    @Schema(example = "Ponto de teste - Av. Exemplo")
    private String nome;

    @NotNull(message = "A latitude é obrigatória.")
    @DecimalMin(value = "-90.0", message = "A latitude deve estar entre -90 e 90.")
    @DecimalMax(value = "90.0", message = "A latitude deve estar entre -90 e 90.")
    @Schema(example = "-23.55")
    private Double latitude;

    @NotNull(message = "A longitude é obrigatória.")
    @DecimalMin(value = "-180.0", message = "A longitude deve estar entre -180 e 180.")
    @DecimalMax(value = "180.0", message = "A longitude deve estar entre -180 e 180.")
    @Schema(example = "-46.63")
    private Double longitude;
}
