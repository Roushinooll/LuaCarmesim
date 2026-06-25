package com.cls.projetoluacarmesim.model;

import com.cls.projetoluacarmesim.enums.TipoInimigo;





public class Boss extends Beyonder {

    public static final int VIDA_MAXIMA_BOSS = 340;

    private String caminhoAtual;
    private int faseAtual;

    public Boss() {
        super(
                0,
                "Carcereiro da Lua",
                "Um Beyonder anômalo preso ao ciclo da Lua Carmesim.",
                VIDA_MAXIMA_BOSS,
                21,
                12,
                5,
                2,
                null,
                "Fragmento de Tentação Carmesim",
                200,
                false,
                100,
                5,
                190,
                16,
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

    








    public boolean atualizarFasePorVida() {
        int vida = getVidaAtual();
        int novaFase;
        String novoCaminho;
        String novaPocao;

        if (vida <= 85) {
            novaFase = 4;
            novoCaminho = "Criminoso";
            novaPocao = "Apóstolo do Desejo";
        } else if (vida <= 170) {
            novaFase = 3;
            novoCaminho = "Bardo";
            novaPocao = "Sacerdote da Luz";
        } else if (vida <= 255) {
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
                return danoBase + 6;
            case 3:
                return danoBase + 4;
            case 4:
                return danoBase + 8;
            case 1:
            default:
                return danoBase + 5;
        }
    }

    @Override
    public String tentarPersuadir(int carismaJogador) {
        return "O Carcereiro da Lua encara você pelo reflexo do espelho. "
                + "Nenhuma palavra atravessa a prisão do ciclo.";
    }
}
