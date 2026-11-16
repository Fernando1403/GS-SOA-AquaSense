package com.aquasense.interfaces;

import com.aquasense.dto.LocalDTO;

import java.util.List;

public interface ILocalService {

    List<LocalDTO> listar();

    LocalDTO criar(LocalDTO dto);
}
