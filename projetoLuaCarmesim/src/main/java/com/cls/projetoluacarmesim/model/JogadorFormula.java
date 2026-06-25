package com.cls.projetoluacarmesim.model;

import java.time.LocalDateTime;

public class JogadorFormula {

    private int idJogador;
    private int idFormula;
    private LocalDateTime aprendidaEm;

    
    private FormulaPocao formula;

    public JogadorFormula() {}

    public JogadorFormula(int idJogador, int idFormula) {
        this.idJogador = idJogador;
        this.idFormula = idFormula;
    }

    public JogadorFormula(int idJogador, int idFormula, LocalDateTime aprendidaEm) {
        this.idJogador = idJogador;
        this.idFormula = idFormula;
        this.aprendidaEm = aprendidaEm;
    }

    
    public int getIdJogador() { return idJogador; }
    public void setIdJogador(int idJogador) { this.idJogador = idJogador; }

    public int getIdFormula() { return idFormula; }
    public void setIdFormula(int idFormula) { this.idFormula = idFormula; }

    public LocalDateTime getAprendidaEm() { return aprendidaEm; }
    public void setAprendidaEm(LocalDateTime aprendidaEm) { this.aprendidaEm = aprendidaEm; }

    public FormulaPocao getFormula() { return formula; }
    public void setFormula(FormulaPocao formula) { this.formula = formula; }

    @Override
    public String toString() {
        return "JogadorFormula{jogador=" + idJogador + ", formula=" + idFormula
                + ", aprendidaEm=" + aprendidaEm + "}";
    }
}
