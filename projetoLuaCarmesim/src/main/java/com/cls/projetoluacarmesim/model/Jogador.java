package com.cls.projetoluacarmesim.model;

import java.time.LocalDateTime;

public class Jogador {

    private int idJogador;
    private String nome;
    private int sequenciaAtual;
    private String caminhoAtual;
    private int sanidadeMaxima;
    private int sanidadeAtual;
    private LocalDateTime criadoEm;

    public Jogador() {
        this.sequenciaAtual = 10;
        this.sanidadeMaxima = 100;
        this.sanidadeAtual = 100;
    }

    public Jogador(String nome) {
        this();
        this.nome = nome;
    }

    public Jogador(int idJogador, String nome, int sequenciaAtual,
                   int sanidadeMaxima, int sanidadeAtual, LocalDateTime criadoEm) {
        this.idJogador = idJogador;
        this.nome = nome;
        this.sequenciaAtual = sequenciaAtual;
        this.sanidadeMaxima = sanidadeMaxima;
        this.sanidadeAtual = sanidadeAtual;
        this.criadoEm = criadoEm;
    }

    
    public int getIdJogador() { return idJogador; }
    public void setIdJogador(int idJogador) { this.idJogador = idJogador; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public int getSequenciaAtual() { return sequenciaAtual; }
    public void setSequenciaAtual(int sequenciaAtual) {
        if (sequenciaAtual < 0 || sequenciaAtual > 10)
            throw new IllegalArgumentException("Sequência deve estar entre 0 e 10.");
        this.sequenciaAtual = sequenciaAtual;
    }

    public String getCaminhoAtual() { return caminhoAtual; }
    public void setCaminhoAtual(String caminhoAtual) { this.caminhoAtual = caminhoAtual; }

    public int getSanidadeMaxima() { return sanidadeMaxima; }
    public void setSanidadeMaxima(int sanidadeMaxima) { this.sanidadeMaxima = sanidadeMaxima; }

    public int getSanidadeAtual() { return sanidadeAtual; }
    public void setSanidadeAtual(int sanidadeAtual) {
        if (sanidadeAtual < 0 || sanidadeAtual > sanidadeMaxima)
            throw new IllegalArgumentException("Sanidade fora dos limites permitidos.");
        this.sanidadeAtual = sanidadeAtual;
    }

    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }

    @Override
    public String toString() {
        return "Jogador{id=" + idJogador
                + ", nome='" + nome + "'"
                + ", sequencia=" + sequenciaAtual
                + ", caminho='" + caminhoAtual + "'"
                + ", sanidade=" + sanidadeAtual + "/" + sanidadeMaxima
                + "}";
    }
}
