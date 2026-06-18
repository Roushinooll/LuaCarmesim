package com.cls.projetoluacarmesim.util;

import com.cls.projetoluacarmesim.enums.TipoInimigo;
import com.cls.projetoluacarmesim.model.Inimigo;
import java.util.Random;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public final class SpriteInimigoFactory {

    public static final double TAMANHO_INIMIGO = 96;
    public static final double TAMANHO_BOSS = 96;

    private static final Random RANDOM = new Random();
    private static final String PROP_BASE = "spriteBaseInimigo";
    private static final String PROP_DIRECAO = "spriteDirecaoInimigo";
    private static final String PROP_TAMANHO = "spriteTamanhoInimigo";
    private static final String PROP_ANIM_TEMPO = "spriteAnimTempoInimigo";
    private static final String PROP_ANIM_FRAME = "spriteAnimFrameInimigo";

    private static final double TEMPO_POR_FRAME_ANDANDO = 0.13;
    private static final double[] OFFSET_Y_ANDANDO = {0, -3, 0, 2};
    private static final double[] ROTACAO_ANDANDO = {-1.4, 1.4, -0.8, 0.8};
    private static final double[] ESCALA_X_ANDANDO = {1.0, 1.035, 1.0, 0.985};
    private static final double[] ESCALA_Y_ANDANDO = {1.0, 0.965, 1.0, 1.035};

    private SpriteInimigoFactory() {
    }

    public static ImageView criar(Inimigo inimigo) {
        ImageView view = new ImageView();
        String base = escolherBase(inimigo);
        double tamanho = inimigo != null && inimigo.getTipoInimigo() == TipoInimigo.BOSS
                ? TAMANHO_BOSS
                : TAMANHO_INIMIGO;

        view.getProperties().put(PROP_BASE, base);
        view.getProperties().put(PROP_DIRECAO, "");
        view.getProperties().put(PROP_TAMANHO, tamanho);
        view.getProperties().put(PROP_ANIM_TEMPO, 0.0);
        view.getProperties().put(PROP_ANIM_FRAME, 0);
        SpriteCache.configurarPixelArt(view, tamanho);
        aplicarDirecao(view, 0, 1);

        return view;
    }

    public static void atualizarAndando(ImageView view, double direcaoX, double direcaoY, double delta) {
        if (view == null) {
            return;
        }

        aplicarDirecao(view, direcaoX, direcaoY);

        double tempo = lerDouble(view, PROP_ANIM_TEMPO);
        int frame = lerInt(view, PROP_ANIM_FRAME);

        tempo += Math.max(0, delta);

        while (tempo >= TEMPO_POR_FRAME_ANDANDO) {
            tempo -= TEMPO_POR_FRAME_ANDANDO;
            frame = (frame + 1) % OFFSET_Y_ANDANDO.length;
        }

        view.getProperties().put(PROP_ANIM_TEMPO, tempo);
        view.getProperties().put(PROP_ANIM_FRAME, frame);

        int sinalHorizontal = direcaoVisualEhEsquerda(view) ? -1 : 1;
        view.setTranslateY(OFFSET_Y_ANDANDO[frame]);
        view.setRotate(ROTACAO_ANDANDO[frame] * sinalHorizontal);
        view.setScaleX(sinalHorizontal * ESCALA_X_ANDANDO[frame]);
        view.setScaleY(ESCALA_Y_ANDANDO[frame]);
    }

    public static void pararAnimacao(ImageView view) {
        if (view == null) {
            return;
        }

        int sinalHorizontal = direcaoVisualEhEsquerda(view) ? -1 : 1;
        view.getProperties().put(PROP_ANIM_TEMPO, 0.0);
        view.getProperties().put(PROP_ANIM_FRAME, 0);
        view.setTranslateY(0);
        view.setRotate(0);
        view.setScaleX(sinalHorizontal);
        view.setScaleY(1);
    }

    public static void aplicarDirecao(ImageView view, double direcaoX, double direcaoY) {
        if (view == null) {
            return;
        }

        Object baseObj = view.getProperties().get(PROP_BASE);
        if (!(baseObj instanceof String)) {
            return;
        }

        String novaDirecao;

        if (Math.abs(direcaoX) >= Math.abs(direcaoY)) {
            novaDirecao = direcaoX < 0 ? "WEST" : "EAST";
        } else {
            novaDirecao = direcaoY < 0 ? "NORTH" : "SOUTH";
        }

        Object direcaoAtual = view.getProperties().get(PROP_DIRECAO);
        if (novaDirecao.equals(direcaoAtual)) {
            return;
        }

        view.getProperties().put(PROP_DIRECAO, novaDirecao);
        aplicarImagemDaDirecao(view, (String) baseObj, novaDirecao);
    }

    private static void aplicarImagemDaDirecao(ImageView view, String base, String direcao) {
        if ("BossGeral".equals(base)) {
            Image imagemBoss = SpriteCache.carregar("/image/sprites/BossGeral/idle_sprite.png");
            if (imagemBoss != null) {
                view.setImage(imagemBoss);
                view.setViewport(null);
                view.setSmooth(false);
                view.setScaleX(1);
            }
            return;
        }

        String caminho;

        switch (direcao) {
            case "NORTH":
                caminho = "/image/sprites/" + base + "/rotations/north.png";
                view.setScaleX(1);
                break;

            case "SOUTH":
                caminho = "/image/sprites/" + base + "/rotations/south.png";
                view.setScaleX(1);
                break;

            case "WEST":
                caminho = caminhoLados(base);
                view.setScaleX(-1);
                break;

            case "EAST":
            default:
                caminho = caminhoLados(base);
                view.setScaleX(1);
                break;
        }

        Image imagem = SpriteCache.carregar(caminho);

        if (imagem == null && caminho.contains("/Lados.png")) {
            imagem = SpriteCache.carregar(caminho.replace("/Lados.png", "/lados.png"));
        }

        if (imagem == null && caminho.contains("/lados.png")) {
            imagem = SpriteCache.carregar(caminho.replace("/lados.png", "/Lados.png"));
        }

        if (imagem != null) {
            view.setImage(imagem);
            view.setViewport(null);
            view.setSmooth(false);
        }
    }

    private static boolean direcaoVisualEhEsquerda(ImageView view) {
        Object direcaoAtual = view.getProperties().get(PROP_DIRECAO);
        return "WEST".equals(direcaoAtual);
    }

    private static double lerDouble(ImageView view, String chave) {
        Object valor = view.getProperties().get(chave);
        if (valor instanceof Number) {
            return ((Number) valor).doubleValue();
        }
        return 0.0;
    }

    private static int lerInt(ImageView view, String chave) {
        Object valor = view.getProperties().get(chave);
        if (valor instanceof Number) {
            return ((Number) valor).intValue();
        }
        return 0;
    }

    private static String escolherBase(Inimigo inimigo) {
        if (inimigo == null) {
            return "Bandido1Geral";
        }

        if (inimigo.getTipoInimigo() == TipoInimigo.BOSS) {
            return "BossGeral";
        }

        if (inimigo.getTipoInimigo() == TipoInimigo.BEYONDER) {
            return RANDOM.nextBoolean() ? "Beyonder1Geral" : "Beyonder2Geral";
        }

        return RANDOM.nextBoolean() ? "Bandido1Geral" : "Bandido2Geral";
    }

    private static String caminhoLados(String base) {
        if ("Bandido2Geral".equals(base)) {
            return "/image/sprites/" + base + "/rotations/lados.png";
        }

        return "/image/sprites/" + base + "/rotations/Lados.png";
    }
}
