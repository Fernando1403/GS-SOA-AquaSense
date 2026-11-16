package com.aquasense.service;

import com.aquasense.dto.AlertaDTO;
import com.aquasense.exception.ResourceNotFoundException;
import com.aquasense.model.Alerta;
import com.aquasense.model.Leitura;
import com.aquasense.model.Local;
import com.aquasense.repository.AlertaRepository;
import com.aquasense.repository.LeituraRepository;
import com.aquasense.repository.LocalRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlertaServiceTest {

    @Mock
    private AlertaRepository alertaRepository;

    @Mock
    private LocalRepository localRepository;

    @Mock
    private LeituraRepository leituraRepository;

    @InjectMocks
    private AlertaService alertaService;

    private Local criarLocal() {
        Local local = new Local();
        local.setId(1L);
        local.setNome("Ponto de teste");
        return local;
    }

    private Leitura criarLeitura(Local local, double nivelAgua) {
        Leitura leitura = new Leitura();
        leitura.setLocal(local);
        leitura.setNivelAgua(nivelAgua);
        leitura.setDataHora(LocalDateTime.now());
        return leitura;
    }

    @Test
    void gerarAlerta_localInexistente_deveLancarNotFound() {
        when(localRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> alertaService.gerarAlerta(99L));

        verifyNoInteractions(alertaRepository);
    }

    @Test
    void gerarAlerta_localSemLeituras_deveLancarNotFound() {
        when(localRepository.findById(1L)).thenReturn(Optional.of(criarLocal()));
        when(leituraRepository.findFirstByLocalIdOrderByDataHoraDesc(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> alertaService.gerarAlerta(1L));

        verify(alertaRepository, never()).save(any());
    }

    @Test
    void gerarAlerta_nivelAcimaDoLimite_deveGerarAlertaCritico() {
        Local local = criarLocal();
        when(localRepository.findById(1L)).thenReturn(Optional.of(local));
        when(leituraRepository.findFirstByLocalIdOrderByDataHoraDesc(1L))
                .thenReturn(Optional.of(criarLeitura(local, 30)));
        when(alertaRepository.save(any(Alerta.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        AlertaDTO resultado = alertaService.gerarAlerta(1L);

        assertEquals("CRITICO", resultado.getNivelRisco());
        assertEquals("Ponto de teste", resultado.getLocal());
        assertEquals(30.0, resultado.getNivelAgua());
        verify(alertaRepository).save(any(Alerta.class));
    }

    @Test
    void listar_deveConverterAlertasParaDTO() {
        Alerta alerta = new Alerta();
        alerta.setLocal(criarLocal());
        alerta.setNivelAgua(15.0);
        alerta.setNivelRisco("ATENCAO");
        alerta.setMensagem("mensagem");
        alerta.setDataGeracao(LocalDateTime.now());
        when(alertaRepository.findAllByOrderByDataGeracaoDesc()).thenReturn(List.of(alerta));

        List<AlertaDTO> resultado = alertaService.listar();

        assertEquals(1, resultado.size());
        assertEquals("ATENCAO", resultado.get(0).getNivelRisco());
    }
}
