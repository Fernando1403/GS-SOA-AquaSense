package com.aquasense.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NivelRiscoTest {

    @ParameterizedTest
    @CsvSource({
            "0,    NORMAL",
            "9.9,  NORMAL",
            "10,   ATENCAO",
            "24.9, ATENCAO",
            "25,   CRITICO",
            "80,   CRITICO"
    })
    void deveClassificarONivelDeAgua(double nivelCm, NivelRisco esperado) {
        assertEquals(esperado, NivelRisco.doNivelAgua(nivelCm));
    }

    @Test
    void deveLancarExcecaoParaNivelNegativo() {
        assertThrows(IllegalArgumentException.class, () -> NivelRisco.doNivelAgua(-1));
    }
}
