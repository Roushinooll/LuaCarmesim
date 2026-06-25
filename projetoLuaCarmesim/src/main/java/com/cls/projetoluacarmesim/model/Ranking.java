package com.cls.projetoluacarmesim.model;

import java.time.LocalDateTime;

public class Ranking {

    private int idRanking;
    private int idJogador;
    private int totalSalasZeradas;
    private int melhorSequencia;
    private LocalDateTime dataRecorde;

    public Ranking() {
        this.totalSalasZeradas = 0;
        this.melhorSequencia = 10;
    }

    public Ranking(int idJogador) {
        this();
        this.idJogador = idJogador;
    }

    public Ranking(int idRanking, int idJogador, int totalSalasZeradas,
                   int melhorSequencia, LocalDateTime dataRecorde) {
        this.idRanking = idRanking;
        this.idJogador = idJogador;
        this.totalSalasZeradas = totalSalasZeradas;
        this.melhorSequencia = melhorSequencia;
        this.dataRecorde = dataRecorde;
    }

    
    public int getIdRanking() { return idRanking; }
    public void setIdRanking(int idRanking) { this.idRanking = idRanking; }

    public int getIdJogador() { return idJogador; }
    public void setIdJogador(int idJogador) { this.idJogador = idJogador; }

    public int getTotalSalasZeradas() { return totalSalasZeradas; }
    public void setTotalSalasZeradas(int totalSalasZeradas) { this.totalSalasZeradas = totalSalasZeradas; }

    public int getMelhorSequencia() { return melhorSequencia; }
    public void setMelhorSequencia(int melhorSequencia) { this.melhorSequencia = melhorSequencia; }

    public LocalDateTime getDataRecorde() { return dataRecorde; }
    public void setDataRecorde(LocalDateTime dataRecorde) { this.dataRecorde = dataRecorde; }

    @Override
    public String toString() {
        return "Ranking{id=" + idRanking + ", jogador=" + idJogador
                + ", salas=" + totalSalasZeradas + ", melhorSeq=" + melhorSequencia + "}";
    }
}
