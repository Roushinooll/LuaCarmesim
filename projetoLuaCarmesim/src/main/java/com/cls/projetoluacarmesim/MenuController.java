package com.cls.projetoluacarmesim;

import com.cls.projetoluacarmesim.util.SessaoJogador;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;

public class MenuController {

    @FXML
    private void jogar() throws IOException {

        if (!garantirJogadorSincronizado()) {
            return;
        }

        Object controller = App.setRoot("restroom");

        if (controller instanceof RestroomController) {
            RestroomController restroomController = (RestroomController) controller;
            restroomController.startGame(App.getStage().getScene());
        }
    }

    private boolean garantirJogadorSincronizado() {
        if (EstadoJogo.getInstance().getJogadorAtual() != null) {
            return true;
        }

        while (true) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Nome do Jogador");
            dialog.setHeaderText("Digite seu nome para sincronizar com o banco de dados.");
            dialog.setContentText("Nome:");

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

    private void mostrarErro(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
    
    @FXML
    private void opcoes() throws IOException {
        EstadoJogo.getInstance().setTelaAnteriorConfigs("menu");
        App.setRoot("configs");
    }
    
    @FXML
    private void ranking() throws IOException {
        App.setRoot("ranking");
    }
    
    @FXML
    private void creditos() throws IOException {
        App.setRoot("creditos");
    }
}
