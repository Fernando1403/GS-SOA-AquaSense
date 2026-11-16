package com.aquasense.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "locais")
@Data
public class Local {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    // Coordenadas aproximadas do ponto monitorado (nunca endereço residencial)
    private Double latitude;
    private Double longitude;
}
