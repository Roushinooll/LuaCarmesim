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
            "Um gosto metálico invade minha boca... sangue?",
            "Minha cabeça pulsa como se algo tivesse sido arrancado de dentro dela.",
            "Abro os olhos devagar, mas o quarto à minha volta não é familiar.",
            "O cheiro de pólvora ainda paira no ar, misturado ao frio da madrugada.",
            "Há marcas no chão. Há silêncio demais. Algo terrível aconteceu aqui.",
            "Minhas mãos tremem diante dos meus olhos.",
            "Elas parecem minhas... mas ao mesmo tempo, não são.",
            "Fragmentos de memória surgem e desaparecem antes que eu consiga segurá-los.",
            "Um ritual. Um pedido desesperado. Uma tentativa de mudar o próprio destino.",
            "Então veio a dor. O vazio. E depois... esta lua vermelha.",
            "Pela janela, a Lua Carmesim observa como se já soubesse a resposta.",
            "Eu deveria estar morto.",
            "Então por que ainda consigo pensar?",
            "Não lembro onde estou.",
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
            dialog.setTitle("Memória Fragmentada");
            dialog.setHeaderText("Um nome tenta emergir no fundo da minha mente.");
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