package com.aquasense.service;

import com.aquasense.dto.LocalDTO;
import com.aquasense.interfaces.ILocalService;
import com.aquasense.model.Local;
import com.aquasense.repository.LocalRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocalService implements ILocalService {

    private final LocalRepository localRepository;

    public LocalService(LocalRepository localRepository) {
        this.localRepository = localRepository;
    }

    @Override
    public List<LocalDTO> listar() {

        return localRepository.findAll()
                .stream()
                .map(this::paraDTO)
                .toList();
    }

    @Override
    public LocalDTO criar(LocalDTO dto) {

        Local local = new Local();

        local.setNome(dto.getNome());
        local.setLatitude(dto.getLatitude());
        local.setLongitude(dto.getLongitude());

        return paraDTO(localRepository.save(local));
    }

    private LocalDTO paraDTO(Local local) {

        return new LocalDTO(
                local.getId(),
                local.getNome(),
                local.getLatitude(),
                local.getLongitude()
        );
    }
}
