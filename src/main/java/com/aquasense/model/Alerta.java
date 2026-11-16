package com.aquasense.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "alertas")
@Data
public class Alerta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "local_id")
    private Local local;

    private Double nivelAgua;
    private String nivelRisco;
    private String mensagem;
    private LocalDateTime dataGeracao;
}
