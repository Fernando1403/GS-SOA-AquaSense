package com.aquasense.interfaces;

import com.aquasense.dto.AlertaDTO;

import java.util.List;

public interface IAlertaService {

    AlertaDTO gerarAlerta(Long localId);

    List<AlertaDTO> listar();
}
