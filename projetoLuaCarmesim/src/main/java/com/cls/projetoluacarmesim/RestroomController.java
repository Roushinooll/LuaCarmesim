package com.cls.projetoluacarmesim;

import com.cls.projetoluacarmesim.enums.TipoItem;
import com.cls.projetoluacarmesim.model.ItemEspecial;
import com.cls.projetoluacarmesim.util.Input;
import com.cls.projetoluacarmesim.util.ObjetoInterativo;
import com.cls.projetoluacarmesim.util.Personagem;
import com.cls.projetoluacarmesim.dao.JogadorDAO;
import com.cls.projetoluacarmesim.dao.RankingDAO;
import com.cls.projetoluacarmesim.model.Jogador;
import com.cls.projetoluacarmesim.model.Ranking;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;
import java.util.Optional;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;

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
    private boolean aguardandoNome = false;

    private final List<ObjetoInterativo> objetosInterativos = new ArrayList<>();

    public void startGame(Scene scene) {

        Parent root = scene.getRoot();

        input = new Input(root);
        personagem = new Personagem(personagemView);

        EstadoJogo estado = EstadoJogo.getInstance();

        if (estado.isPosicaoPersonagemSalva()) {
            personagemView.setTranslateX(estado.getPersonagemTranslateX());
            personagemView.setTranslateY(estado.getPersonagemTranslateY());
        }

        configurarControles(root);
        configurarObjetosInterativos();

        Platform.runLater(root::requestFocus);

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

    private void configurarControles(Parent root) {

        root.addEventFilter(KeyEvent.KEY_PRESSED, e -> {

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
                "",
                this::pegarArma
        );

        ObjetoInterativo portaInterativa = new ObjetoInterativo(
                porta,
                "",
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
            if (objetoAtual.getMensagem() != null && !objetoAtual.getMensagem().isBlank()) {
                textoInteracao.setText(objetoAtual.getMensagem());
            }

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

        EstadoJogo estado = EstadoJogo.getInstance();

        if (pegouArma || estado.getInventario().possuiItem("Revólver Enferrujado")) {
            textoInteracao.setText("Você já pegou a arma.");
            mesa.setStyle("-fx-fill: #2a2a2a;");
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

        estado.getInventario().adicionarItem(arma);

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

        if (!possuiArma) {
            abrirDialogo(
                    "Tem certeza que não esqueceu nada?\n"
            );
            return;
        }

        if (EstadoJogo.getInstance().getJogadorAtual() != null) {
            sairParaRua();
            return;
        }

        if (aguardandoNome) {
            return;
        }

        aguardandoNome = true;
        dialogoAberto = true;
        podeInteragir = false;
        input.interact = false;
        textoInteracao.setText("");

        Platform.runLater(() -> {

            boolean nomeConfirmado = garantirJogadorComNome();

            aguardandoNome = false;
            dialogoAberto = false;
            podeInteragir = true;
            input.interact = false;

            if (nomeConfirmado) {
                sairParaRua();
            }
        });
    }
    
    private boolean garantirJogadorComNome() {

        EstadoJogo estado = EstadoJogo.getInstance();

        if (estado.getJogadorAtual() != null) {
            return true;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nome do Jogador");
        dialog.setHeaderText("Antes de sair, digite o nome do seu personagem.");
        dialog.setContentText("Nome:");

        Optional<String> resultado = dialog.showAndWait();

        if (resultado.isEmpty()) {
            return false;
        }

        String nome = resultado.get().trim();

        if (nome.isEmpty()) {
            mostrarErro("Nome inválido", "Você precisa digitar um nome para continuar.");
            return false;
        }

        try {
            JogadorDAO jogadorDAO = new JogadorDAO();
            RankingDAO rankingDAO = new RankingDAO();

            Jogador jogador = jogadorDAO.buscarPorNome(nome);

            if (jogador == null) {
                jogador = new Jogador(nome);
                jogador = jogadorDAO.criar(jogador);
            }

            if (rankingDAO.buscarPorJogador(jogador.getIdJogador()) == null) {
                rankingDAO.criar(new Ranking(jogador.getIdJogador()));
            }

            estado.setJogadorAtual(jogador);

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarErro(
                    "Erro no banco de dados",
                    "Não foi possível salvar o jogador no banco.\n\n" + e.getMessage()
            );
            return false;
        }
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

            EstadoJogo.getInstance().salvarPosicaoPersonagem(
                    personagemView.getTranslateX(),
                    personagemView.getTranslateY()
            );

            input.interact = false;
            podeInteragir = false;

            EstadoJogo.getInstance().setTelaAnteriorInventario("restroom");

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

            StreetsController controller = (StreetsController) App.setRoot("streets");
            controller.startGame(App.getStage().getScene());

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

