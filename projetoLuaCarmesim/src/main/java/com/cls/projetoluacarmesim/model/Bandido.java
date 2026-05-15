package com.cls.projetoluacarmesim.model;

import com.cls.projetoluacarmesim.enums.TipoInimigo;

/**
 * Bandido — inimigo próximo de humano.
 * Combate restrito a armas e corpo a corpo, sem poderes místicos.
 * Mais fácil de persuadir que um Beyonder.
 */
public class Bandido extends Inimigo {

    public enum TipoArma {
        FACA,
        PORRETE,
        REVOLVER,
        CORRENTE,
        DESARMADO
    }

    private TipoArma tipoArma;
    private boolean estaAmedrontado; // status causado pelo blefe do jogador
    private int moedaRoubada;        // ouro/moeda que carrega (drop extra)

    public Bandido() {
        super();
        setTipoInimigo(TipoInimigo.BANDIDO);
        this.tipoArma = TipoArma.DESARMADO;
        this.estaAmedrontado = false;
        this.moedaRoubada = 0;
        setPersuadivel(true);
        setResistenciaDialogo(40); // bandidos cedem mais facilmente
    }

    public Bandido(int idInimigo, String nome, String descricao,
                   int vidaMaxima, int danoCorpoACorpo, int danoArma, int defesa,
                   int pontosDeAcao, String itemDropavel, String ingredienteDropavel,
                   int xpConhecimento, int resistenciaDialogo,
                   TipoArma tipoArma, int moedaRoubada) {
        super(idInimigo, nome, descricao, TipoInimigo.BANDIDO,
              vidaMaxima, danoCorpoACorpo, danoArma, defesa,
              pontosDeAcao, itemDropavel, ingredienteDropavel,
              xpConhecimento, true, resistenciaDialogo);
        this.tipoArma = tipoArma;
        this.moedaRoubada = moedaRoubada;
        this.estaAmedrontado = false;
    }

    /**
     * Dano do bandido depende do tipo de arma equipada.
     * Se amedrontado, ataca com metade da força.
     */
    @Override
    public int calcularDanoAtaque() {
        int danoBase = (tipoArma == TipoArma.DESARMADO)
                ? getDanoCorpoACorpo()
                : getDanoCorpoACorpo() + getDanoArma();

        return estaAmedrontado ? danoBase / 2 : danoBase;
    }

    /**
     * Bandidos podem ser persuadidos a fugir ou a dar informações.
     * Resultado depende do carisma do jogador vs. resistência do bandido.
     */
    @Override
    public String tentarPersuadir(int carismaJogador) {
        if (!isPersuadivel()) {
            return nome() + " ignora suas palavras com desprezo.";
        }
        if (carismaJogador >= getResistenciaDialogo()) {
            this.estaAmedrontado = true;
            return nome() + " hesita, olhos arregalados. Ele recua, desistindo da briga.";
        } else if (carismaJogador >= getResistenciaDialogo() / 2) {
            this.estaAmedrontado = true;
            return nome() + " está amedrontado, mas não foge. Ele ataca com menos convicção.";
        } else {
            return nome() + " ri da sua tentativa. \"Papo não paga dívida!\"";
        }
    }

    private String nome() {
        return getNome() != null ? getNome() : "O bandido";
    }

    // Getters e Setters específicos de Bandido
    public TipoArma getTipoArma() { return tipoArma; }
    public void setTipoArma(TipoArma tipoArma) { this.tipoArma = tipoArma; }

    public boolean isEstaAmedrontado() { return estaAmedrontado; }
    public void setEstaAmedrontado(boolean estaAmedrontado) { this.estaAmedrontado = estaAmedrontado; }

    public int getMoedaRoubada() { return moedaRoubada; }
    public void setMoedaRoubada(int moedaRoubada) { this.moedaRoubada = moedaRoubada; }

    @Override
    public String toString() {
        return "Bandido{id=" + getIdInimigo() + ", nome='" + getNome() + "', arma=" + tipoArma
                + ", vida=" + getVidaAtual() + "/" + getVidaMaxima()
                + ", amedrontado=" + estaAmedrontado + "}";
    }
}
