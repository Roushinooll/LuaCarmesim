package com.cls.projetoluacarmesim.model;

import com.cls.projetoluacarmesim.enums.StatusRun;
import java.time.LocalDateTime;

public class Progresso {

    private int idProgresso;
    private int idJogador;
    private int salaAtual;
    private int andarAtual;
    private StatusRun statusRun;
    private LocalDateTime atualizadoEm;

    public Progresso() {
        this.salaAtual = 1;
        this.andarAtual = 1;
        this.statusRun = StatusRun.EM_ANDAMENTO;
    }

    public Progresso(int idJogador) {
        this();
        this.idJogador = idJogador;
    }

    public Progresso(int idProgresso, int idJogador, int salaAtual,
                     int andarAtual, StatusRun statusRun, LocalDateTime atualizadoEm) {
        this.idProgresso = idProgresso;
        this.idJogador = idJogador;
        this.salaAtual = salaAtual;
        this.andarAtual = andarAtual;
        this.statusRun = statusRun;
        this.atualizadoEm = atualizadoEm;
    }

    // Getters e Setters
    public int getIdProgresso() { return idProgresso; }
    public void setIdProgresso(int idProgresso) { this.idProgresso = idProgresso; }

    public int getIdJogador() { return idJogador; }
    public void setIdJogador(int idJogador) { this.idJogador = idJogador; }

    public int getSalaAtual() { return salaAtual; }
    public void setSalaAtual(int salaAtual) { this.salaAtual = salaAtual; }

    public int getAndarAtual() { return andarAtual; }
    public void setAndarAtual(int andarAtual) { this.andarAtual = andarAtual; }

    public StatusRun getStatusRun() { return statusRun; }
    public void setStatusRun(StatusRun statusRun) { this.statusRun = statusRun; }

    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
    public void setAtualizadoEm(LocalDateTime atualizadoEm) { this.atualizadoEm = atualizadoEm; }

    @Override
    public String toString() {
        return "Progresso{id=" + idProgresso + ", jogador=" + idJogador
                + ", sala=" + salaAtual + ", andar=" + andarAtual
                + ", status=" + statusRun + "}";
    }
}
