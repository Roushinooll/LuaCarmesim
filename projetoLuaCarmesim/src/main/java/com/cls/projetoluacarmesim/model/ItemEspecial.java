package com.cls.projetoluacarmesim.model;

import com.cls.projetoluacarmesim.enums.TipoItem;

public class ItemEspecial {

    private int idItem;
    private int idJogador;
    private String nomeItem;
    private TipoItem tipoItem;
    private String efeito;
    private boolean permanente;

    public ItemEspecial() {
        this.permanente = true;
    }

    public ItemEspecial(int idJogador, String nomeItem, TipoItem tipoItem,
                        String efeito, boolean permanente) {
        this.idJogador = idJogador;
        this.nomeItem = nomeItem;
        this.tipoItem = tipoItem;
        this.efeito = efeito;
        this.permanente = permanente;
    }

    public ItemEspecial(int idItem, int idJogador, String nomeItem,
                        TipoItem tipoItem, String efeito, boolean permanente) {
        this.idItem = idItem;
        this.idJogador = idJogador;
        this.nomeItem = nomeItem;
        this.tipoItem = tipoItem;
        this.efeito = efeito;
        this.permanente = permanente;
    }

    // Getters e Setters
    public int getIdItem() { return idItem; }
    public void setIdItem(int idItem) { this.idItem = idItem; }

    public int getIdJogador() { return idJogador; }
    public void setIdJogador(int idJogador) { this.idJogador = idJogador; }

    public String getNomeItem() { return nomeItem; }
    public void setNomeItem(String nomeItem) { this.nomeItem = nomeItem; }

    public TipoItem getTipoItem() { return tipoItem; }
    public void setTipoItem(TipoItem tipoItem) { this.tipoItem = tipoItem; }

    public String getEfeito() { return efeito; }
    public void setEfeito(String efeito) { this.efeito = efeito; }

    public boolean isPermanente() { return permanente; }
    public void setPermanente(boolean permanente) { this.permanente = permanente; }

    @Override
    public String toString() {
        return "ItemEspecial{id=" + idItem + ", nome='" + nomeItem
                + "', tipo=" + tipoItem + ", permanente=" + permanente + "}";
    }
}
