package com.aquasense.service;

import com.aquasense.dto.LeituraDTO;
import com.aquasense.exception.ResourceNotFoundException;
import com.aquasense.model.Leitura;
import com.aquasense.model.Local;
import com.aquasense.repository.LeituraRepository;
import com.aquasense.repository.LocalRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LeituraServiceTest {

    @Mock
    private LeituraRepository leituraRepository;

    @Mock
    private LocalRepository localRepository;

    @InjectMocks
    private LeituraService leituraService;

    @Test
    void registrar_localInexistente_deveLancarNotFound() {
        when(localRepository.findById(99L)).thenReturn(Optional.empty());

        LeituraDTO dto = new LeituraDTO(null, 99L, 10.0, null);

        assertThrows(ResourceNotFoundException.class, () -> leituraService.registrar(dto));

        verify(leituraRepository, never()).save(any());
    }

    @Test
    void registrar_localExistente_deveSalvarLeitura() {
        Local local = new Local();
        local.setId(1L);
        when(localRepository.findById(1L)).thenReturn(Optional.of(local));
        when(leituraRepository.save(any(Leitura.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        LeituraDTO resultado = leituraService.registrar(new LeituraDTO(null, 1L, 18.5, null));

        assertEquals(1L, resultado.getLocalId());
        assertEquals(18.5, resultado.getNivelAgua());
        assertNotNull(resultado.getDataHora());
        verify(leituraRepository).save(any(Leitura.class));
    }
}
