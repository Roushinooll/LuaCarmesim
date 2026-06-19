package com.cls.projetoluacarmesim;

import com.cls.projetoluacarmesim.model.Boss;
import com.cls.projetoluacarmesim.util.Input;
import com.cls.projetoluacarmesim.util.Personagem;
import com.cls.projetoluacarmesim.util.SpriteInimigoFactory;
import java.io.IOException;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.geometry.Bounds;
import javafx.geometry.BoundingBox;
import javafx.scene.shape.Rectangle;

public class Final_roomController {

    @FXML private Pane rootFinal;
    @FXML private Pane camadaCenario;
    @FXML private Pane camadaObjetos;
    @FXML private ImageView personagemView;
    @FXML private Label textoInfo;
    @FXML private Label textoInteracao;

    private static final double DISTANCIA_INICIAR_COMBATE = 52;
    private static final double HITBOX_JOGADOR_LARGURA = 34;
    private static final double HITBOX_JOGADOR_ALTURA = 56;
    private static final double HITBOX_BOSS_LARGURA = 38;
    private static final double HITBOX_BOSS_ALTURA = 58;

    private Personagem personagem;
    private Input input;
    private AnimationTimer loop;
    private Boss boss;
    private ImageView bossView;
    private boolean combateAberto;

    public void startGame(Scene scene) {
        Parent root = scene.getRoot();

        input = new Input(root);
        personagem = new Personagem(personagemView);

        personagemView.setFitWidth(Personagem.TAMANHO_VISUAL);
        personagemView.setFitHeight(Personagem.TAMANHO_VISUAL);
        personagemView.setPreserveRatio(true);

        configurarControles(root);
        prepararSalaFinal();

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

                if (combateAberto) {
                    return;
                }

                personagem.update(delta, input);
                limitarMovimento();
                verificarProximidadeBoss();
            }
        };

        loop.start();
    }

    private void configurarControles(Parent root) {
        root.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.I || e.getCode() == KeyCode.TAB) {
                textoInteracao.setText("A Final Room sela o caminho. Derrote o inimigo para sair.");
                e.consume();
                return;
            }
        });
    }

    private void prepararSalaFinal() {
        combateAberto = false;

        camadaCenario.getChildren().clear();
        camadaObjetos.getChildren().clear();

        desenharCenario();
        criarBossEstatico();

        personagemView.setLayoutX(110);
        personagemView.setLayoutY(320);
        personagemView.setTranslateX(0);
        personagemView.setTranslateY(0);
        personagemView.toFront();

        textoInfo.setText("Final Room - O Reflexo da Lua Carmesim");
        textoInteracao.setText("Aproxime-se do inimigo estático para iniciar o combate final.");
    }

    private void desenharCenario() {
        ImageView fundoImagem = criarImagemFundo("/image/fundos/boss_room.jpg");

        if (fundoImagem != null) {
            camadaCenario.getChildren().add(fundoImagem);
            return;
        }

        Rectangle fundo = new Rectangle(0, 0, 1280, 720);
        fundo.setFill(Color.rgb(5, 4, 8));

        Rectangle piso = new Rectangle(0, 260, 1280, 220);
        piso.setFill(Color.rgb(28, 24, 34));

        Rectangle brilhoLua = new Rectangle(520, 80, 240, 90);
        brilhoLua.setArcWidth(45);
        brilhoLua.setArcHeight(45);
        brilhoLua.setFill(Color.rgb(80, 15, 32, 0.65));
        brilhoLua.setStroke(Color.rgb(180, 40, 65));
        brilhoLua.setStrokeWidth(3);

        Rectangle espelhoCentral = new Rectangle(580, 135, 120, 250);
        espelhoCentral.setArcWidth(20);
        espelhoCentral.setArcHeight(20);
        espelhoCentral.setFill(Color.rgb(12, 12, 20, 0.9));
        espelhoCentral.setStroke(Color.rgb(160, 20, 45));
        espelhoCentral.setStrokeWidth(4);

        camadaCenario.getChildren().addAll(fundo, piso, brilhoLua, espelhoCentral);
    }

    private ImageView criarImagemFundo(String caminhoImagem) {
        java.net.URL recurso = getClass().getResource(caminhoImagem);

        if (recurso == null) {
            System.out.println("Imagem de fundo não encontrada: " + caminhoImagem);
            return null;
        }

        ImageView fundo = new ImageView(new Image(recurso.toExternalForm()));
        fundo.setFitWidth(1280);
        fundo.setFitHeight(720);
        fundo.setPreserveRatio(false);
        fundo.setSmooth(false);
        fundo.setMouseTransparent(true);

        return fundo;
    }

    private void criarBossEstatico() {
        boss = new Boss();

        bossView = SpriteInimigoFactory.criar(boss);
        bossView.setLayoutX(605);
        bossView.setLayoutY(330);
        SpriteInimigoFactory.aplicarDirecao(bossView, 0, 1);
        SpriteInimigoFactory.pararAnimacao(bossView);
        bossView.setFitWidth(Personagem.TAMANHO_VISUAL);
        bossView.setFitHeight(Personagem.TAMANHO_VISUAL);

        camadaObjetos.getChildren().add(bossView);
    }

    private void verificarProximidadeBoss() {
        if (boss == null || bossView == null || !boss.estaVivo()) {
            return;
        }

        double distancia = calcularDistanciaDoPersonagem(bossView);

        if (distancia <= DISTANCIA_INICIAR_COMBATE
                || hitboxPersonagem().intersects(hitboxBoss())) {
            iniciarCombateBoss();
        }
    }

    private void iniciarCombateBoss() {
        combateAberto = true;
        limparInputMovimento();
        textoInteracao.setText("O Carcereiro da Lua desperta.");

        try {
            CombateController.abrirCombate(
                    boss,
                    this::finalizarBossComVitoria,
                    this::finalizarBossComFuga
            );
        } catch (IOException e) {
            e.printStackTrace();
            combateAberto = false;
            textoInteracao.setText("Erro ao iniciar o combate final.");
        }
    }

    private void finalizarBossComVitoria() {
        combateAberto = false;

        if (loop != null) {
            loop.stop();
        }

        if (bossView != null) {
            camadaObjetos.getChildren().remove(bossView);
        }

        EstadoJogo.getInstance().setBossFinalDerrotado(true);
        mostrarMensagemVitoriaFinal();
        voltarParaQuartoDepoisDoBoss();
    }

    private void finalizarBossComFuga() {
        combateAberto = false;
        textoInteracao.setText("Você recua, mas o Carcereiro permanece imóvel no centro da sala.");
        afastarJogadorDoBoss();
        Platform.runLater(() -> rootFinal.getScene().getRoot().requestFocus());
    }

    private void mostrarMensagemVitoriaFinal() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Boss derrotado");
        alert.setHeaderText("O Carcereiro da Lua caiu");
        alert.setContentText(
                "O espelho racha e a Final Room perde a forma.\n\n"
                        + "Por enquanto, você retornará ao quarto."
        );
        alert.showAndWait();
    }

    private void voltarParaQuartoDepoisDoBoss() {
        try {
            EstadoJogo.getInstance().resetarRua();
            Object controller = App.setRoot("restroom");

            if (controller instanceof RestroomController) {
                RestroomController restroomController = (RestroomController) controller;
                restroomController.startGame(App.getStage().getScene());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void afastarJogadorDoBoss() {
        personagemView.setTranslateX(0);
        personagemView.setTranslateY(0);
        personagemView.setLayoutX(110);
        personagemView.setLayoutY(320);
    }

    private double calcularDistanciaDoPersonagem(Node alvo) {
        Bounds hitboxAlvo = alvo == bossView ? hitboxBoss() : alvo.getBoundsInParent();
        Bounds hitboxJogador = hitboxPersonagem();

        double centroAlvoX = centroX(hitboxAlvo);
        double centroAlvoY = centroY(hitboxAlvo);

        double centroPersonagemX = centroX(hitboxJogador);
        double centroPersonagemY = centroY(hitboxJogador);

        double diferencaX = centroPersonagemX - centroAlvoX;
        double diferencaY = centroPersonagemY - centroAlvoY;

        return Math.sqrt(diferencaX * diferencaX + diferencaY * diferencaY);
    }

    private Bounds hitboxPersonagem() {
        return hitboxCentral(personagemView, HITBOX_JOGADOR_LARGURA, HITBOX_JOGADOR_ALTURA, 18, true);
    }

    private Bounds hitboxBoss() {
        return hitboxCentral(bossView, HITBOX_BOSS_LARGURA, HITBOX_BOSS_ALTURA, 24, false);
    }

    private Bounds hitboxCentral(ImageView view, double largura, double altura, double deslocamentoY, boolean usarTranslateY) {
        double larguraVisual = view.getFitWidth() > 0 ? view.getFitWidth() : view.getBoundsInParent().getWidth();
        double alturaVisual = view.getFitHeight() > 0 ? view.getFitHeight() : view.getBoundsInParent().getHeight();

        double centroX = view.getLayoutX() + view.getTranslateX() + larguraVisual / 2;
        double centroY = view.getLayoutY() + (usarTranslateY ? view.getTranslateY() : 0) + alturaVisual / 2 + deslocamentoY;

        return new BoundingBox(
                centroX - largura / 2,
                centroY - altura / 2,
                largura,
                altura
        );
    }

    private double centroX(Node node) {
        return centroX(node.getBoundsInParent());
    }

    private double centroY(Node node) {
        return centroY(node.getBoundsInParent());
    }

    private double centroX(Bounds bounds) {
        return bounds.getMinX() + bounds.getWidth() / 2;
    }

    private double centroY(Bounds bounds) {
        return bounds.getMinY() + bounds.getHeight() / 2;
    }

    private void limitarMovimento() {
        double x = personagemView.getLayoutX() + personagemView.getTranslateX();
        double y = personagemView.getLayoutY() + personagemView.getTranslateY();

        if (x < 40) {
            personagemView.setTranslateX(40 - personagemView.getLayoutX());
        }

        if (x > 1160) {
            personagemView.setTranslateX(1160 - personagemView.getLayoutX());
        }

        if (y < 260) {
            personagemView.setTranslateY(260 - personagemView.getLayoutY());
        }

        if (y > 420) {
            personagemView.setTranslateY(420 - personagemView.getLayoutY());
        }
    }

    private void limparInputMovimento() {
        if (input == null) {
            return;
        }

        input.up = false;
        input.down = false;
        input.left = false;
        input.right = false;
        input.interact = false;
    }
}
