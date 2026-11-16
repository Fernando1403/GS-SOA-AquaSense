package com.aquasense.repository;

import com.aquasense.model.Leitura;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LeituraRepository extends JpaRepository<Leitura, Long> {

    List<Leitura> findByLocalIdOrderByDataHoraDesc(Long localId);

    Optional<Leitura> findFirstByLocalIdOrderByDataHoraDesc(Long localId);
}
