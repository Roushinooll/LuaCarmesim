    package com.cls.projetoluacarmesim.model;

import com.cls.projetoluacarmesim.enums.TipoBuff;

public class BuffPermanente {

    private int idBuff;
    private int idJogador;
    private String nomeBuff;
    private TipoBuff tipo;
    private int valor;
    private String descricao;

    public BuffPermanente() {}

    public BuffPermanente(int idJogador, String nomeBuff, TipoBuff tipo, int valor, String descricao) {
        this.idJogador = idJogador;
        this.nomeBuff = nomeBuff;
        this.tipo = tipo;
        this.valor = valor;
        this.descricao = descricao;
    }

    public BuffPermanente(int idBuff, int idJogador, String nomeBuff,
                          TipoBuff tipo, int valor, String descricao) {
        this.idBuff = idBuff;
        this.idJogador = idJogador;
        this.nomeBuff = nomeBuff;
        this.tipo = tipo;
        this.valor = valor;
        this.descricao = descricao;
    }

    
    public int getIdBuff() { return idBuff; }
    public void setIdBuff(int idBuff) { this.idBuff = idBuff; }

    public int getIdJogador() { return idJogador; }
    public void setIdJogador(int idJogador) { this.idJogador = idJogador; }

    public String getNomeBuff() { return nomeBuff; }
    public void setNomeBuff(String nomeBuff) { this.nomeBuff = nomeBuff; }

    public TipoBuff getTipo() { return tipo; }
    public void setTipo(TipoBuff tipo) { this.tipo = tipo; }

    public int getValor() { return valor; }
    public void setValor(int valor) { this.valor = valor; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    @Override
    public String toString() {
        return "BuffPermanente{id=" + idBuff + ", nome='" + nomeBuff
                + "', tipo=" + tipo + ", valor=" + valor + "}";
    }
}
