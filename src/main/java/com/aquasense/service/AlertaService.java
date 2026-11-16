package com.aquasense.service;

import com.aquasense.dto.AlertaDTO;
import com.aquasense.exception.ResourceNotFoundException;
import com.aquasense.interfaces.IAlertaService;
import com.aquasense.model.Alerta;
import com.aquasense.model.Leitura;
import com.aquasense.model.Local;
import com.aquasense.model.NivelRisco;
import com.aquasense.repository.AlertaRepository;
import com.aquasense.repository.LeituraRepository;
import com.aquasense.repository.LocalRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AlertaService implements IAlertaService {

    private final AlertaRepository alertaRepository;
    private final LocalRepository localRepository;
    private final LeituraRepository leituraRepository;

    public AlertaService(AlertaRepository alertaRepository,
                         LocalRepository localRepository,
                         LeituraRepository leituraRepository) {
        this.alertaRepository = alertaRepository;
        this.localRepository = localRepository;
        this.leituraRepository = leituraRepository;
    }

    @Override
    public AlertaDTO gerarAlerta(Long localId) {

        Local local = localRepository.findById(localId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Local " + localId + " não encontrado."));

        Leitura leitura = leituraRepository.findFirstByLocalIdOrderByDataHoraDesc(localId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "O local " + localId + " ainda não possui leituras."));

        NivelRisco risco = NivelRisco.doNivelAgua(leitura.getNivelAgua());

        Alerta alerta = new Alerta();

        alerta.setLocal(local);
        alerta.setNivelAgua(leitura.getNivelAgua());
        alerta.setNivelRisco(risco.name());
        alerta.setMensagem(risco.getMensagem());
        alerta.setDataGeracao(LocalDateTime.now());

        return paraDTO(alertaRepository.save(alerta));
    }

    @Override
    public List<AlertaDTO> listar() {

        return alertaRepository.findAllByOrderByDataGeracaoDesc()
                .stream()
                .map(this::paraDTO)
                .toList();
    }

    private AlertaDTO paraDTO(Alerta alerta) {

        return new AlertaDTO(
                alerta.getId(),
                alerta.getLocal().getNome(),
                alerta.getNivelAgua(),
                alerta.getNivelRisco(),
                alerta.getMensagem(),
                alerta.getDataGeracao()
        );
    }
}
