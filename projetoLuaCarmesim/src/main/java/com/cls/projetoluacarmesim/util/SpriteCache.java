package com.cls.projetoluacarmesim.util;

import java.util.HashMap;
import java.util.Map;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public final class SpriteCache {

    private static final Map<String, Image> CACHE = new HashMap<>();

    private SpriteCache() {
    }

    public static Image carregar(String caminho) {
        if (caminho == null || caminho.isBlank()) {
            return null;
        }

        if (CACHE.containsKey(caminho)) {
            return CACHE.get(caminho);
        }

        java.net.URL recurso = SpriteCache.class.getResource(caminho);

        if (recurso == null) {
            System.out.println("Sprite não encontrado: " + caminho);
            CACHE.put(caminho, null);
            return null;
        }

        Image imagem = new Image(recurso.toExternalForm());
        CACHE.put(caminho, imagem);
        return imagem;
    }

    public static void configurarPixelArt(ImageView view, double tamanho) {
        if (view == null) {
            return;
        }

        view.setFitWidth(tamanho);
        view.setFitHeight(tamanho);
        view.setPreserveRatio(true);
        view.setSmooth(false);
        view.setPickOnBounds(false);
    }

    public static void aplicarImagem(ImageView view, String caminho, double tamanho) {
        if (view == null) {
            return;
        }

        Image imagem = carregar(caminho);

        if (imagem == null) {
            return;
        }

        view.setImage(imagem);
        view.setViewport(null);
        configurarPixelArt(view, tamanho);
    }

    public static void aplicarFrame(ImageView view, String caminho, double tamanho, int frameX, int frameY, int frameW, int frameH) {
        if (view == null) {
            return;
        }

        Image imagem = carregar(caminho);

        if (imagem == null) {
            return;
        }

        view.setImage(imagem);
        view.setViewport(new Rectangle2D(frameX, frameY, frameW, frameH));
        configurarPixelArt(view, tamanho);
    }
}
