package com.cls.projetoluacarmesim.model;

import com.cls.projetoluacarmesim.enums.TipoIngrediente;

public class IngredienteFormula {

    private int idIngrediente;
    private int idFormula;
    private String nomeIngrediente;
    private TipoIngrediente tipoIngrediente;
    private int quantidade;

    public IngredienteFormula() {
        this.quantidade = 1;
    }

    public IngredienteFormula(int idFormula, String nomeIngrediente,
                               TipoIngrediente tipoIngrediente, int quantidade) {
        this.idFormula = idFormula;
        this.nomeIngrediente = nomeIngrediente;
        this.tipoIngrediente = tipoIngrediente;
        this.quantidade = quantidade;
    }

    public IngredienteFormula(int idIngrediente, int idFormula, String nomeIngrediente,
                               TipoIngrediente tipoIngrediente, int quantidade) {
        this.idIngrediente = idIngrediente;
        this.idFormula = idFormula;
        this.nomeIngrediente = nomeIngrediente;
        this.tipoIngrediente = tipoIngrediente;
        this.quantidade = quantidade;
    }

    // Getters e Setters
    public int getIdIngrediente() { return idIngrediente; }
    public void setIdIngrediente(int idIngrediente) { this.idIngrediente = idIngrediente; }

    public int getIdFormula() { return idFormula; }
    public void setIdFormula(int idFormula) { this.idFormula = idFormula; }

    public String getNomeIngrediente() { return nomeIngrediente; }
    public void setNomeIngrediente(String nomeIngrediente) { this.nomeIngrediente = nomeIngrediente; }

    public TipoIngrediente getTipoIngrediente() { return tipoIngrediente; }
    public void setTipoIngrediente(TipoIngrediente tipoIngrediente) { this.tipoIngrediente = tipoIngrediente; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    @Override
    public String toString() {
        return "IngredienteFormula{id=" + idIngrediente + ", nome='" + nomeIngrediente
                + "', tipo=" + tipoIngrediente + ", qtd=" + quantidade + "}";
    }
}
