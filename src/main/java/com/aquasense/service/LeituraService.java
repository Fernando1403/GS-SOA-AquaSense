package com.aquasense.service;

import com.aquasense.dto.LeituraDTO;
import com.aquasense.exception.ResourceNotFoundException;
import com.aquasense.interfaces.ILeituraService;
import com.aquasense.model.Leitura;
import com.aquasense.model.Local;
import com.aquasense.repository.LeituraRepository;
import com.aquasense.repository.LocalRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LeituraService implements ILeituraService {

    private final LeituraRepository leituraRepository;
    private final LocalRepository localRepository;

    public LeituraService(LeituraRepository leituraRepository, LocalRepository localRepository) {
        this.leituraRepository = leituraRepository;
        this.localRepository = localRepository;
    }

    @Override
    public LeituraDTO registrar(LeituraDTO dto) {

        Local local = localRepository.findById(dto.getLocalId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Local " + dto.getLocalId() + " não encontrado."));

        Leitura leitura = new Leitura();

        leitura.setLocal(local);
        leitura.setNivelAgua(dto.getNivelAgua());
        leitura.setDataHora(LocalDateTime.now());

        return paraDTO(leituraRepository.save(leitura));
    }

    @Override
    public List<LeituraDTO> listarPorLocal(Long localId) {

        if (!localRepository.existsById(localId)) {
            throw new ResourceNotFoundException("Local " + localId + " não encontrado.");
        }

        return leituraRepository.findByLocalIdOrderByDataHoraDesc(localId)
                .stream()
                .map(this::paraDTO)
                .toList();
    }

    private LeituraDTO paraDTO(Leitura leitura) {

        return new LeituraDTO(
                leitura.getId(),
                leitura.getLocal().getId(),
                leitura.getNivelAgua(),
                leitura.getDataHora()
        );
    }
}
