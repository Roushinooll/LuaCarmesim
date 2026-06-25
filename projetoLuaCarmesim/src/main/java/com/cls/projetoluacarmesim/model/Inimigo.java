package com.cls.projetoluacarmesim.model;

import com.cls.projetoluacarmesim.enums.TipoInimigo;

public abstract class Inimigo {

    private int idInimigo;
    private String nome;
    private String descricao;
    private TipoInimigo tipoInimigo;

    
    private int vidaMaxima;
    private int vidaAtual;
    private int danoCorpoACorpo;
    private int danoArma;
    private int defesa;
    private int pontosDeAcao;

    
    private String itemDropavel;       
    private String ingredienteDropavel; 
    private int xpConhecimento;        

    
    private boolean persuadivel;       
    private int resistenciaDialogo;    

    public Inimigo() {
        this.vidaMaxima = 100;
        this.vidaAtual = 100;
        this.pontosDeAcao = 2;
        this.defesa = 0;
        this.persuadivel = false;
        this.resistenciaDialogo = 100;
    }

    public Inimigo(int idInimigo, String nome, String descricao, TipoInimigo tipoInimigo,
                   int vidaMaxima, int danoCorpoACorpo, int danoArma, int defesa,
                   int pontosDeAcao, String itemDropavel, String ingredienteDropavel,
                   int xpConhecimento, boolean persuadivel, int resistenciaDialogo) {
        this.idInimigo = idInimigo;
        this.nome = nome;
        this.descricao = descricao;
        this.tipoInimigo = tipoInimigo;
        this.vidaMaxima = vidaMaxima;
        this.vidaAtual = vidaMaxima;
        this.danoCorpoACorpo = danoCorpoACorpo;
        this.danoArma = danoArma;
        this.defesa = defesa;
        this.pontosDeAcao = pontosDeAcao;
        this.itemDropavel = itemDropavel;
        this.ingredienteDropavel = ingredienteDropavel;
        this.xpConhecimento = xpConhecimento;
        this.persuadivel = persuadivel;
        this.resistenciaDialogo = resistenciaDialogo;
    }

    
    public abstract int calcularDanoAtaque();

    
    public abstract String tentarPersuadir(int carismaJogador);

    
    public void receberDano(int dano) {
        int danoEfetivo = Math.max(0, dano - this.defesa);
        this.vidaAtual = Math.max(0, this.vidaAtual - danoEfetivo);
    }

    public boolean estaVivo() {
        return this.vidaAtual > 0;
    }

    
    public int getIdInimigo() { return idInimigo; }
    public void setIdInimigo(int idInimigo) { this.idInimigo = idInimigo; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public TipoInimigo getTipoInimigo() { return tipoInimigo; }
    public void setTipoInimigo(TipoInimigo tipoInimigo) { this.tipoInimigo = tipoInimigo; }

    public int getVidaMaxima() { return vidaMaxima; }
    public void setVidaMaxima(int vidaMaxima) { this.vidaMaxima = vidaMaxima; }

    public int getVidaAtual() { return vidaAtual; }
    public void setVidaAtual(int vidaAtual) {
        if (vidaAtual < 0 || vidaAtual > vidaMaxima)
            throw new IllegalArgumentException("Vida fora dos limites permitidos.");
        this.vidaAtual = vidaAtual;
    }

    public int getDanoCorpoACorpo() { return danoCorpoACorpo; }
    public void setDanoCorpoACorpo(int danoCorpoACorpo) { this.danoCorpoACorpo = danoCorpoACorpo; }

    public int getDanoArma() { return danoArma; }
    public void setDanoArma(int danoArma) { this.danoArma = danoArma; }

    public int getDefesa() { return defesa; }
    public void setDefesa(int defesa) { this.defesa = defesa; }

    public int getPontosDeAcao() { return pontosDeAcao; }
    public void setPontosDeAcao(int pontosDeAcao) { this.pontosDeAcao = pontosDeAcao; }

    public String getItemDropavel() { return itemDropavel; }
    public void setItemDropavel(String itemDropavel) { this.itemDropavel = itemDropavel; }

    public String getIngredienteDropavel() { return ingredienteDropavel; }
    public void setIngredienteDropavel(String ingredienteDropavel) { this.ingredienteDropavel = ingredienteDropavel; }

    public int getXpConhecimento() { return xpConhecimento; }
    public void setXpConhecimento(int xpConhecimento) { this.xpConhecimento = xpConhecimento; }

    public boolean isPersuadivel() { return persuadivel; }
    public void setPersuadivel(boolean persuadivel) { this.persuadivel = persuadivel; }

    public int getResistenciaDialogo() { return resistenciaDialogo; }
    public void setResistenciaDialogo(int resistenciaDialogo) {
        if (resistenciaDialogo < 0 || resistenciaDialogo > 100)
            throw new IllegalArgumentException("Resistência ao diálogo deve estar entre 0 e 100.");
        this.resistenciaDialogo = resistenciaDialogo;
    }

    @Override
    public String toString() {
        return "Inimigo{id=" + idInimigo + ", nome='" + nome + "', tipo=" + tipoInimigo
                + ", vida=" + vidaAtual + "/" + vidaMaxima + "}";
    }
}
