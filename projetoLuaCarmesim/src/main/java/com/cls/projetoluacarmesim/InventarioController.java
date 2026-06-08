package com.cls.projetoluacarmesim;

import com.cls.projetoluacarmesim.model.ItemEspecial;
import com.cls.projetoluacarmesim.model.Jogador;
import com.cls.projetoluacarmesim.model.JogadorFormula;

import java.io.IOException;
import java.util.List;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
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

    private boolean controleConfigurado = false;

    @FXML
    public void initialize() {
        carregarItens();
        carregarReceitas();

        rootInventario.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null && !controleConfigurado) {
                configurarControles(newScene);
                controleConfigurado = true;
            }
        });

        Platform.runLater(() -> {
            rootInventario.setFocusTraversable(true);
            rootInventario.requestFocus();
        });
    }

    private void configurarControles(Scene scene) {
        scene.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                voltarParaRestroom();
                e.consume();
            }
        });
    }

    private void carregarItens() {
        listaItens.getItems().clear();

        List<ItemEspecial> itens = EstadoJogo.getInstance()
                .getInventario()
                .getItens();

        if (itens.isEmpty()) {
            listaItens.getItems().add("Nenhum item obtido.");
            return;
        }

        for (ItemEspecial item : itens) {
            listaItens.getItems().add(
                    item.getNomeItem() + " - " + item.getTipoItem()
            );
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

    private void voltarParaRestroom() {
        try {
            Object controller = App.setRoot("restroom");

            if (controller instanceof RestroomController) {
                RestroomController restroomController = (RestroomController) controller;
                restroomController.startGame(App.getStage().getScene());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}