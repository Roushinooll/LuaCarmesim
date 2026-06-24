package com.cls.projetoluacarmesim.util;

import java.net.URL;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public final class ImagemUtils {

    private ImagemUtils() {
    }

    public static Image carregarImagem(Class<?> classeReferencia, String... caminhos) {
        if (caminhos == null) {
            return null;
        }

        for (String caminho : caminhos) {
            if (caminho == null || caminho.isBlank()) {
                continue;
            }

            URL recurso = localizarRecurso(classeReferencia, caminho);

            if (recurso == null) {
                continue;
            }

            try {
                Image imagem = new Image(recurso.toExternalForm(), false);

                if (!imagem.isError()) {
                    return imagem;
                }

                if (imagem.getException() != null) {
                    System.out.println("Erro ao carregar imagem " + caminho + ": " + imagem.getException().getMessage());
                }
            } catch (Exception erro) {
                System.out.println("Erro ao carregar imagem " + caminho + ": " + erro.getMessage());
            }
        }

        System.out.println("Nenhuma imagem encontrada/carregada nos caminhos informados.");
        return null;
    }

    public static ImageView criarFundo(Class<?> classeReferencia, String... caminhos) {
        Image imagem = carregarImagem(classeReferencia, caminhos);

        if (imagem == null) {
            return null;
        }

        ImageView fundo = new ImageView(imagem);
        configurarFundo(fundo);
        return fundo;
    }

    public static void configurarFundo(ImageView fundo) {
        if (fundo == null) {
            return;
        }

        fundo.setFitWidth(1280);
        fundo.setFitHeight(720);
        fundo.setPreserveRatio(false);
        fundo.setSmooth(false);
        fundo.setMouseTransparent(true);
        fundo.setLayoutX(0);
        fundo.setLayoutY(0);
    }

    private static URL localizarRecurso(Class<?> classeReferencia, String caminho) {
        URL recurso = null;

        if (classeReferencia != null) {
            recurso = classeReferencia.getResource(caminho);
        }

        if (recurso == null) {
            String caminhoSemBarra = caminho.startsWith("/") ? caminho.substring(1) : caminho;
            recurso = Thread.currentThread().getContextClassLoader().getResource(caminhoSemBarra);
        }

        return recurso;
    }
}
