package com.aquasense.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "leituras")
@Data
public class Leitura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "local_id")
    private Local local;

    // Nível da água em centímetros
    private Double nivelAgua;

    private LocalDateTime dataHora;
}
