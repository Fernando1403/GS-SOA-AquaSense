package com.aquasense.interfaces;

import com.aquasense.dto.LeituraDTO;

import java.util.List;

public interface ILeituraService {

    LeituraDTO registrar(LeituraDTO dto);

    List<LeituraDTO> listarPorLocal(Long localId);
}
