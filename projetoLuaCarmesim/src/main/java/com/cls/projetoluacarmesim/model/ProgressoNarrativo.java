package com.cls.projetoluacarmesim.model;

import java.time.LocalDateTime;

public class ProgressoNarrativo {

    private int idProgressoNarrativo;
    private int idJogador;
    private String chaveEvento;
    private LocalDateTime descobertoEm;

    public ProgressoNarrativo() {}

    public ProgressoNarrativo(int idJogador, String chaveEvento) {
        this.idJogador = idJogador;
        this.chaveEvento = chaveEvento;
    }

    public ProgressoNarrativo(int idProgressoNarrativo, int idJogador,
                               String chaveEvento, LocalDateTime descobertoEm) {
        this.idProgressoNarrativo = idProgressoNarrativo;
        this.idJogador = idJogador;
        this.chaveEvento = chaveEvento;
        this.descobertoEm = descobertoEm;
    }

    
    public int getIdProgressoNarrativo() { return idProgressoNarrativo; }
    public void setIdProgressoNarrativo(int idProgressoNarrativo) { this.idProgressoNarrativo = idProgressoNarrativo; }

    public int getIdJogador() { return idJogador; }
    public void setIdJogador(int idJogador) { this.idJogador = idJogador; }

    public String getChaveEvento() { return chaveEvento; }
    public void setChaveEvento(String chaveEvento) { this.chaveEvento = chaveEvento; }

    public LocalDateTime getDescobertaEm() { return descobertoEm; }
    public void setDescobertaEm(LocalDateTime descobertoEm) { this.descobertoEm = descobertoEm; }

    @Override
    public String toString() {
        return "ProgressoNarrativo{id=" + idProgressoNarrativo
                + ", jogador=" + idJogador + ", evento='" + chaveEvento + "'}";
    }
}
