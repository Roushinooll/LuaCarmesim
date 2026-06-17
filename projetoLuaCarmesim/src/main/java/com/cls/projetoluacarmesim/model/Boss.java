package com.cls.projetoluacarmesim.model;

import com.cls.projetoluacarmesim.enums.TipoInimigo;

/**
 * Boss final da Lua Carmesim.
 * É um Beyonder de Sequência 5, mas muda de Caminho conforme perde vida.
 */
public class Boss extends Beyonder {

    public static final int VIDA_MAXIMA_BOSS = 400;

    private String caminhoAtual;
    private int faseAtual;

    public Boss() {
        super(
                0,
                "Carcereiro da Lua",
                "Um Beyonder anômalo preso ao ciclo da Lua Carmesim.",
                VIDA_MAXIMA_BOSS,
                26,
                16,
                8,
                3,
                null,
                "Fragmento de Tentação Carmesim",
                200,
                false,
                100,
                5,
                240,
                12,
                "Marionetista",
                false
        );

        setTipoInimigo(TipoInimigo.BOSS);
        this.caminhoAtual = "Vidente";
        this.faseAtual = 1;
        setNomePocao("Marionetista");
        adicionarPoderMistico("Fios da Lua Carmesim");
        adicionarPoderMistico("Vontade Fragmentada");
    }

    public String getCaminhoAtual() {
        return caminhoAtual;
    }

    public int getFaseAtual() {
        return faseAtual;
    }

    public String getNomeFaseAtual() {
        return "Caminho do " + caminhoAtual + " | Sequência 5";
    }

    /**
     * Atualiza a fase pelo HP atual.
     * 400-301: Vidente
     * 300-201: Caçador
     * 200-101: Bardo
     * 100-0: Criminoso
     *
     * @return true se houve troca de fase.
     */
    public boolean atualizarFasePorVida() {
        int vida = getVidaAtual();
        int novaFase;
        String novoCaminho;
        String novaPocao;

        if (vida <= 100) {
            novaFase = 4;
            novoCaminho = "Criminoso";
            novaPocao = "Apóstolo do Desejo";
        } else if (vida <= 200) {
            novaFase = 3;
            novoCaminho = "Bardo";
            novaPocao = "Sacerdote da Luz";
        } else if (vida <= 300) {
            novaFase = 2;
            novoCaminho = "Caçador";
            novaPocao = "Ceifador";
        } else {
            novaFase = 1;
            novoCaminho = "Vidente";
            novaPocao = "Marionetista";
        }

        boolean mudou = novaFase != this.faseAtual;
        this.faseAtual = novaFase;
        this.caminhoAtual = novoCaminho;
        setNomePocao(novaPocao);
        setSequencia(5);

        return mudou;
    }

    @Override
    public int calcularDanoAtaque() {
        int danoBase = getDanoCorpoACorpo() + getDanoArma();

        switch (faseAtual) {
            case 2:
                return danoBase + 10;
            case 3:
                return danoBase + 6;
            case 4:
                return danoBase + 14;
            case 1:
            default:
                return danoBase + 8;
        }
    }

    @Override
    public String tentarPersuadir(int carismaJogador) {
        return "O Carcereiro da Lua encara você pelo reflexo do espelho. "
                + "Nenhuma palavra atravessa a prisão do ciclo.";
    }
}
