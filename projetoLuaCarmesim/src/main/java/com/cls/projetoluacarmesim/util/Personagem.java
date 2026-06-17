package com.cls.projetoluacarmesim.util;

import javafx.scene.image.ImageView;

public class Personagem {

    public static final double TAMANHO_VISUAL = 96;

    private static final int FRAME_W = 128;
    private static final int FRAME_H = 128;
    private static final int TOTAL_FRAMES = 6;
    private static final double TEMPO_POR_FRAME = 0.11;

    private static final String SPRITE_LADOS = "/image/sprites/KleinGeral/SpriteSheetsKlein/KleinLadosWalk.png";
    private static final String SPRITE_NORTE = "/image/sprites/KleinGeral/SpriteSheetsKlein/KleinNorthWalk.png";
    private static final String SPRITE_SUL = "/image/sprites/KleinGeral/SpriteSheetsKlein/KleinSulWalk.png";

    private static final String IDLE_LADOS = "/image/sprites/KleinGeral/rotations/Lados.png";
    private static final String IDLE_NORTE = "/image/sprites/KleinGeral/rotations/north.png";
    private static final String IDLE_SUL = "/image/sprites/KleinGeral/rotations/south.png";

    private ImageView personagem;
    private double speed = 450;
    private double tempoAnimacao = 0;
    private int frameAtual = 0;
    private Direcao direcaoAtual = Direcao.SUL;
    private String spriteAtual = "";
    private boolean andando = false;

    public Personagem(ImageView personagem) {
        this.personagem = personagem;
        SpriteCache.configurarPixelArt(personagem, TAMANHO_VISUAL);
        aplicarIdle(Direcao.SUL);
    }

    public void update(double delta, Input input) {
        double dx = 0;
        double dy = 0;

        if (input.up) {
            dy -= 1;
        }
        if (input.down) {
            dy += 1;
        }
        if (input.left) {
            dx -= 1;
        }
        if (input.right) {
            dx += 1;
        }

        double length = Math.sqrt(dx * dx + dy * dy);
        boolean emMovimento = length > 0;

        if (emMovimento) {
            dx = dx / length * speed * delta;
            dy = dy / length * speed * delta;

            personagem.setTranslateX(personagem.getTranslateX() + dx);
            personagem.setTranslateY(personagem.getTranslateY() + dy);

            Direcao novaDirecao = calcularDirecao(dx, dy);
            atualizarAnimacao(novaDirecao, delta);
        } else {
            if (andando) {
                frameAtual = 0;
                tempoAnimacao = 0;
                andando = false;
                aplicarIdle(direcaoAtual);
            }
        }
    }

    private Direcao calcularDirecao(double dx, double dy) {
        if (Math.abs(dx) >= Math.abs(dy)) {
            return dx < 0 ? Direcao.OESTE : Direcao.LESTE;
        }

        return dy < 0 ? Direcao.NORTE : Direcao.SUL;
    }

    private void atualizarAnimacao(Direcao direcao, double delta) {
        andando = true;
        direcaoAtual = direcao;
        tempoAnimacao += delta;

        if (tempoAnimacao >= TEMPO_POR_FRAME) {
            tempoAnimacao = 0;
            frameAtual = (frameAtual + 1) % TOTAL_FRAMES;
        }

        String sprite = spriteDeAndar(direcao);

        if (!sprite.equals(spriteAtual)) {
            spriteAtual = sprite;
            frameAtual = 0;
        }

        personagem.setScaleX(direcao == Direcao.OESTE ? -1 : 1);
        SpriteCache.aplicarFrame(
                personagem,
                sprite,
                TAMANHO_VISUAL,
                frameAtual * FRAME_W,
                0,
                FRAME_W,
                FRAME_H
        );
    }

    private void aplicarIdle(Direcao direcao) {
        direcaoAtual = direcao;
        spriteAtual = "IDLE_" + direcao.name();
        personagem.setScaleX(direcao == Direcao.OESTE ? -1 : 1);
        SpriteCache.aplicarImagem(personagem, spriteIdle(direcao), TAMANHO_VISUAL);
    }

    private String spriteDeAndar(Direcao direcao) {
        switch (direcao) {
            case NORTE:
                return SPRITE_NORTE;
            case SUL:
                return SPRITE_SUL;
            case OESTE:
            case LESTE:
            default:
                return SPRITE_LADOS;
        }
    }

    private String spriteIdle(Direcao direcao) {
        switch (direcao) {
            case NORTE:
                return IDLE_NORTE;
            case SUL:
                return IDLE_SUL;
            case OESTE:
            case LESTE:
            default:
                return IDLE_LADOS;
        }
    }

    public ImageView getPersonagem() {
        return personagem;
    }

    public void setPersonagem(ImageView personagem) {
        this.personagem = personagem;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    private enum Direcao {
        NORTE,
        SUL,
        LESTE,
        OESTE
    }
}
