package com.cls.projetoluacarmesim;

import com.cls.projetoluacarmesim.util.SessaoJogador;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.control.Label;

public class IntroController {

    @FXML
    private Pane rootIntro;

    @FXML
    private Label textoDialogo;

    @FXML
    private Label textoInstrucao;

    private final String[] dialogos = {
            "Sinto minha cabeça pesada.",
            "Minha visão ainda está meio turva.",
            "Não consigo lembrar o que estava fazendo.",
            "Ou onde sequer estou.",
            "Minha cabeça dói muito.",
            "Mal consigo lembrar quem sou."
    };

    private int indiceDialogo = 0;
    private boolean pedindoNome = false;

    @FXML
    private void initialize() {
        if (rootIntro != null) {
            rootIntro.setFocusTraversable(true);
            rootIntro.setOnMouseClicked(e -> avancarDialogo());
        }
    }

    public void startIntro() {
        indiceDialogo = 0;
        pedindoNome = false;
        mostrarDialogoAtual();

        Parent root = App.getScene().getRoot();
        root.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (pedindoNome) {
                return;
            }

            if (e.getCode() == KeyCode.ENTER || e.getCode() == KeyCode.SPACE) {
                avancarDialogo();
                e.consume();
            }
        });

        Platform.runLater(root::requestFocus);
    }

    private void avancarDialogo() {
        if (pedindoNome) {
            return;
        }

        indiceDialogo++;

        if (indiceDialogo >= dialogos.length) {
            abrirCampoNome();
            return;
        }

        mostrarDialogoAtual();
    }

    private void mostrarDialogoAtual() {
        if (textoDialogo != null) {
            textoDialogo.setText(dialogos[indiceDialogo]);
        }

        if (textoInstrucao != null) {
            textoInstrucao.setText("ENTER / ESPAÇO / CLIQUE - Continuar");
        }
    }

    private void abrirCampoNome() {
        pedindoNome = true;

        Platform.runLater(() -> {
            boolean sincronizado = pedirNomeAteSincronizar();

            if (!sincronizado) {
                voltarParaMenu();
                return;
            }

            irParaQuarto();
        });
    }

    private boolean pedirNomeAteSincronizar() {
        while (true) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Nome do Jogador");
            dialog.setHeaderText(null);
            dialog.setContentText("Meu nome é:");

            Optional<String> resultado = dialog.showAndWait();

            if (resultado.isEmpty()) {
                return false;
            }

            String nome = resultado.get().trim();

            if (nome.isEmpty()) {
                mostrarErro("Nome inválido", "Você precisa digitar um nome para começar.");
                continue;
            }

            try {
                SessaoJogador.sincronizarJogador(nome);
                return true;
            } catch (IllegalArgumentException e) {
                mostrarErro("Nome inválido", e.getMessage());
            } catch (SQLException e) {
                e.printStackTrace();
                mostrarErro(
                        "Erro no banco de dados",
                        "Não foi possível sincronizar o jogador. Verifique se o PostgreSQL está aberto e se o banco foi criado.\n\n"
                                + e.getMessage()
                );
                return false;
            }
        }
    }

    private void irParaQuarto() {
        try {
            Object controller = App.setRoot("restroom");

            if (controller instanceof RestroomController) {
                RestroomController restroomController = (RestroomController) controller;
                restroomController.startGame(App.getStage().getScene());
            }
        } catch (IOException e) {
            e.printStackTrace();
            mostrarErro("Erro ao iniciar", "Não foi possível abrir o quarto inicial.");
            voltarParaMenu();
        }
    }

    private void voltarParaMenu() {
        try {
            App.setRoot("menu");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void mostrarErro(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
