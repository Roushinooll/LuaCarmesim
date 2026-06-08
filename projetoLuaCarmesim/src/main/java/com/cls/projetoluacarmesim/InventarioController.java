package com.cls.projetoluacarmesim;

import com.cls.projetoluacarmesim.model.Jogador;
import com.cls.projetoluacarmesim.model.JogadorFormula;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

public class InventarioController {

    @FXML
    private VBox rootInventario;

    @FXML
    private ListView<String> listaItens;

    @FXML
    private ListView<String> listaReceitas;

    @FXML
    public void initialize() {
        carregarItens();
        carregarReceitas();

        rootInventario.setFocusTraversable(true);

        rootInventario.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.I) {
                voltarParaTelaAnterior();
                e.consume();
            }
        });

        Platform.runLater(() -> {
            rootInventario.requestFocus();
            if (listaItens != null) {
                listaItens.setFocusTraversable(false);
            }
            if (listaReceitas != null) {
                listaReceitas.setFocusTraversable(false);
            }
        });
    }

    private void carregarItens() {
        listaItens.getItems().clear();

        Map<String, Integer> itensAgrupados = EstadoJogo.getInstance()
                .getInventario()
                .getItensAgrupadosParaTela();

        if (itensAgrupados.isEmpty()) {
            listaItens.getItems().add("Nenhum item obtido.");
            return;
        }

        for (Map.Entry<String, Integer> entrada : itensAgrupados.entrySet()) {
            String nomeItem = entrada.getKey();
            int quantidade = entrada.getValue();

            if (quantidade > 1) {
                listaItens.getItems().add(nomeItem + " x" + quantidade);
            } else {
                listaItens.getItems().add(nomeItem);
            }
        }
    }

    private void carregarReceitas() {
        listaReceitas.getItems().clear();

        Jogador jogador = EstadoJogo.getInstance().getJogadorAtual();

        if (jogador == null || jogador.getIdJogador() <= 0) {
            listaReceitas.getItems().add("Nenhum jogador carregado.");
            return;
        }

        List<JogadorFormula> receitas = EstadoJogo.getInstance()
                .getInventario()
                .listarReceitasAprendidas(jogador.getIdJogador());

        if (receitas.isEmpty()) {
            listaReceitas.getItems().add("Nenhuma receita obtida.");
            return;
        }

        for (JogadorFormula receita : receitas) {
            if (receita.getFormula() != null) {
                listaReceitas.getItems().add(
                        receita.getFormula().getNomePocao()
                                + " | Sequência "
                                + receita.getFormula().getNivelSequencia()
                );
            }
        }
    }

    private void voltarParaTelaAnterior() {
        try {
            String telaAnterior = EstadoJogo.getInstance().getTelaAnteriorInventario();

            Object controller = App.setRoot(telaAnterior);

            if (controller instanceof RestroomController) {
                RestroomController restroomController = (RestroomController) controller;
                restroomController.startGame(App.getStage().getScene());
            }

            if (controller instanceof StreetsController) {
                StreetsController streetsController = (StreetsController) controller;
                streetsController.startGame(App.getStage().getScene());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
