package com.cls.projetoluacarmesim;

import com.cls.projetoluacarmesim.enums.TipoItem;
import com.cls.projetoluacarmesim.model.ItemEspecial;
import com.cls.projetoluacarmesim.util.Input;
import com.cls.projetoluacarmesim.util.ObjetoInterativo;
import com.cls.projetoluacarmesim.util.Personagem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

public class RestroomController {

    @FXML
    private ImageView personagemView;

    @FXML
    private Rectangle mesa;

    @FXML
    private Rectangle porta;

    @FXML
    private Label textoInteracao;

    @FXML
    private Pane caixaDialogo;

    @FXML
    private Label textoDialogo;

    private Personagem personagem;
    private Input input;
    private AnimationTimer loop;

    private boolean pegouArma = false;
    private boolean podeInteragir = true;
    private boolean dialogoAberto = false;
    private boolean inventarioAberto = false;

    private final List<ObjetoInterativo> objetosInterativos = new ArrayList<>();

    public void startGame(Scene scene) {

        input = new Input(scene);
        personagem = new Personagem(personagemView);

        configurarControles(scene);
        configurarObjetosInterativos();

        Platform.runLater(() -> scene.getRoot().requestFocus());

        loop = new AnimationTimer() {

            long lastTime = 0;

            @Override
            public void handle(long now) {

                if (lastTime == 0) {
                    lastTime = now;
                    return;
                }

                double delta = (now - lastTime) / 1_000_000_000.0;
                lastTime = now;

                if (!dialogoAberto && !inventarioAberto) {
                    personagem.update(delta, input);
                    verificarInteracoes();
                }
            }
        };

        loop.start();
    }

    private void configurarControles(Scene scene) {

        scene.addEventFilter(KeyEvent.KEY_PRESSED, e -> {

            if (dialogoAberto) {

                if (e.getCode() == KeyCode.ENTER) {
                    fecharDialogo();
                    sairParaRua();
                    e.consume();
                }

                if (e.getCode() == KeyCode.ESCAPE) {
                    fecharDialogo();
                    e.consume();
                }

                return;
            }

            if (e.getCode() == KeyCode.I) {
                abrirInventario();
                e.consume();
            }
        });
    }

    private void configurarObjetosInterativos() {

        objetosInterativos.clear();

        ObjetoInterativo mesaInterativa = new ObjetoInterativo(
                mesa,
                "Pressione F para pegar a arma",
                this::pegarArma
        );

        ObjetoInterativo portaInterativa = new ObjetoInterativo(
                porta,
                "Pressione F para sair para a rua",
                this::tentarSairParaRua
        );

        objetosInterativos.add(mesaInterativa);
        objetosInterativos.add(portaInterativa);
    }

    private void verificarInteracoes() {

        ObjetoInterativo objetoAtual = null;

        for (ObjetoInterativo objeto : objetosInterativos) {
            if (objeto.estaPerto(personagemView)) {
                objetoAtual = objeto;
                break;
            }
        }

        if (objetoAtual != null) {
            textoInteracao.setText(objetoAtual.getMensagem());

            if (input.interact && podeInteragir) {
                podeInteragir = false;
                input.interact = false;
                objetoAtual.interagir();
            }

        } else {
            textoInteracao.setText("");
        }

        if (!input.interact) {
            podeInteragir = true;
        }
    }

    private void pegarArma() {

        if (pegouArma) {
            textoInteracao.setText("Você já pegou a arma.");
            return;
        }

        pegouArma = true;

        ItemEspecial arma = new ItemEspecial(
                0,
                "Revólver Enferrujado",
                TipoItem.REVOLVER,
                "Uma arma antiga encontrada sobre a mesa.",
                false
        );

        EstadoJogo estado = EstadoJogo.getInstance();

        estado.getInventario().adicionarItem(arma);

        /*
         * TESTE TEMPORÁRIO:
         * Só deixe esse bloco se existir uma fórmula com id_formula = 1 no banco.
         * Caso contrário, comente para evitar erro de chave estrangeira.
         */
        if (estado.getJogadorAtual() != null) {
            estado.getInventario().aprenderReceita(
                    estado.getJogadorAtual().getIdJogador(),
                    1
            );
        }

        textoInteracao.setText("Você pegou: Revólver Enferrujado.");

        mesa.setStyle("-fx-fill: #2a2a2a;");
    }

    private void tentarSairParaRua() {

        boolean possuiArma = EstadoJogo.getInstance()
                .getInventario()
                .possuiItem("Revólver Enferrujado");

        if (possuiArma) {
            sairParaRua();
            return;
        }

        abrirDialogo(
                "Tem certeza que não esqueceu nada?\nVocê ainda não pegou a arma."
        );
    }

    private void abrirDialogo(String texto) {
        dialogoAberto = true;
        podeInteragir = false;
        input.interact = false;

        textoDialogo.setText(texto);
        textoInteracao.setText("");

        caixaDialogo.setVisible(true);
        caixaDialogo.toFront();

        Platform.runLater(() -> caixaDialogo.getScene().getRoot().requestFocus());
    }

    private void fecharDialogo() {
        dialogoAberto = false;
        podeInteragir = true;
        input.interact = false;

        caixaDialogo.setVisible(false);

        Platform.runLater(() -> caixaDialogo.getScene().getRoot().requestFocus());
    }

    private void abrirInventario() {
        try {
            inventarioAberto = true;

            if (loop != null) {
                loop.stop();
            }

            input.interact = false;
            podeInteragir = false;

            App.setRoot("inventario");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sairParaRua() {
        try {
            if (loop != null) {
                loop.stop();
            }

            App.setRoot("streets");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}