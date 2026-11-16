package com.aquasense.model;

/**
 * Nível de risco de alagamento (limiares didáticos, não oficiais).
 * Menos de 10 cm = NORMAL, de 10 a menos de 25 cm = ATENCAO, 25 cm ou mais = CRITICO.
 */
public enum NivelRisco {

    NORMAL("Nível de água normal. Sem risco no momento."),
    ATENCAO("Nível de água em atenção. Acompanhe a situação e evite áreas baixas."),
    CRITICO("Nível de água crítico. Risco de alagamento: evite o local e procure um ponto seguro.");

    public static final double LIMITE_ATENCAO_CM = 10;
    public static final double LIMITE_CRITICO_CM = 25;

    private final String mensagem;

    NivelRisco(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getMensagem() {
        return mensagem;
    }

    public static NivelRisco doNivelAgua(double nivelAguaCm) {

        if (nivelAguaCm < 0) {
            throw new IllegalArgumentException("O nível de água não pode ser negativo.");
        }

        if (nivelAguaCm >= LIMITE_CRITICO_CM) {
            return CRITICO;
        }

        if (nivelAguaCm >= LIMITE_ATENCAO_CM) {
            return ATENCAO;
        }

        return NORMAL;
    }
}
