package com.cls.projetoluacarmesim.model;

import java.util.ArrayList;
import java.util.List;

public class FormulaPocao {

    private int idFormula;
    private String nomePocao;
    private int nivelSequencia;
    private String efeitoPrincipal;
    private String descricao;

    
    private List<IngredienteFormula> ingredientes = new ArrayList<>();

    public FormulaPocao() {}

    public FormulaPocao(String nomePocao, int nivelSequencia,
                        String efeitoPrincipal, String descricao) {
        this.nomePocao = nomePocao;
        this.nivelSequencia = nivelSequencia;
        this.efeitoPrincipal = efeitoPrincipal;
        this.descricao = descricao;
    }

    public FormulaPocao(int idFormula, String nomePocao, int nivelSequencia,
                        String efeitoPrincipal, String descricao) {
        this.idFormula = idFormula;
        this.nomePocao = nomePocao;
        this.nivelSequencia = nivelSequencia;
        this.efeitoPrincipal = efeitoPrincipal;
        this.descricao = descricao;
    }

    
    public int getIdFormula() { return idFormula; }
    public void setIdFormula(int idFormula) { this.idFormula = idFormula; }

    public String getNomePocao() { return nomePocao; }
    public void setNomePocao(String nomePocao) { this.nomePocao = nomePocao; }

    public int getNivelSequencia() { return nivelSequencia; }
    public void setNivelSequencia(int nivelSequencia) {
        if (nivelSequencia < 0 || nivelSequencia > 9)
            throw new IllegalArgumentException("Nível de sequência deve estar entre 0 e 9.");
        this.nivelSequencia = nivelSequencia;
    }

    public String getEfeitoPrincipal() { return efeitoPrincipal; }
    public void setEfeitoPrincipal(String efeitoPrincipal) { this.efeitoPrincipal = efeitoPrincipal; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public List<IngredienteFormula> getIngredientes() { return ingredientes; }
    public void setIngredientes(List<IngredienteFormula> ingredientes) { this.ingredientes = ingredientes; }

    public void adicionarIngrediente(IngredienteFormula ingrediente) {
        this.ingredientes.add(ingrediente);
    }

    @Override
    public String toString() {
        return "FormulaPocao{id=" + idFormula + ", nome='" + nomePocao
                + "', nivel=" + nivelSequencia + ", ingredientes=" + ingredientes.size() + "}";
    }
}
