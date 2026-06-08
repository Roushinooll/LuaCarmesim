package com.cls.projetoluacarmesim.util;

import javafx.scene.Node;

public class ObjetoInterativo {

    private Node objeto;
    private String mensagem;
    private Runnable acao;

    public ObjetoInterativo(Node objeto, String mensagem, Runnable acao) {
        this.objeto = objeto;
        this.mensagem = mensagem;
        this.acao = acao;
    }

    public boolean estaPerto(Node personagem) {
        return personagem.getBoundsInParent().intersects(objeto.getBoundsInParent());
    }

    public String getMensagem() {
        return mensagem;
    }

    public void interagir() {
        if (acao != null) {
            acao.run();
        }
    }
}