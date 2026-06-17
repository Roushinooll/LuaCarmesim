package com.cls.projetoluacarmesim.combate;

public enum TipoStatusCombate {
    REVELADO("Revelado"),
    AMEDRONTADO("Amedrontado"),
    CONFUSO("Confuso"),
    SANGRAMENTO("Sangramento"),
    PROVOCADO("Provocado"),
    IMOBILIZADO("Imobilizado"),
    MARCADO("Marcado"),
    SENTENCIADO("Sentenciado"),
    ATORDOADO("Atordoado"),
    DESEJO_CORROMPIDO("Desejo Corrompido"),
    DECRETADO("Decretado"),
    FRAGILIZADO("Fragilizado");

    private final String nomeExibicao;

    TipoStatusCombate(String nomeExibicao) {
        this.nomeExibicao = nomeExibicao;
    }

    public String getNomeExibicao() {
        return nomeExibicao;
    }
}
