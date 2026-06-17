package com.cls.projetoluacarmesim.combate;

public class HabilidadeCombate {

    private final String nome;
    private final String caminho;
    private final int sequenciaMinima;
    private final int custoSanidade;
    private final boolean suprema;
    private final String descricaoCurta;

    public HabilidadeCombate(String nome, String caminho, int sequenciaMinima,
                             int custoSanidade, boolean suprema, String descricaoCurta) {
        this.nome = nome;
        this.caminho = caminho;
        this.sequenciaMinima = sequenciaMinima;
        this.custoSanidade = custoSanidade;
        this.suprema = suprema;
        this.descricaoCurta = descricaoCurta;
    }

    public String getNome() {
        return nome;
    }

    public String getCaminho() {
        return caminho;
    }

    public int getSequenciaMinima() {
        return sequenciaMinima;
    }

    public int getCustoSanidade() {
        return custoSanidade;
    }

    public boolean isSuprema() {
        return suprema;
    }

    public String getDescricaoCurta() {
        return descricaoCurta;
    }

    public String getTextoMenu() {
        String prefixo = suprema ? "[Suprema] " : "";
        return prefixo + nome + " (" + custoSanidade + " Sanidade)";
    }
}
