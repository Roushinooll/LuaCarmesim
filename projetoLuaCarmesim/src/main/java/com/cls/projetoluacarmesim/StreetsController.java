package com.cls.projetoluacarmesim;

import com.cls.projetoluacarmesim.enums.TipoItem;
import com.cls.projetoluacarmesim.enums.TipoRua;
import com.cls.projetoluacarmesim.model.ItemEspecial;
import com.cls.projetoluacarmesim.util.GeradorRua;
import com.cls.projetoluacarmesim.util.Input;
import com.cls.projetoluacarmesim.util.Personagem;
import com.cls.projetoluacarmesim.dao.RankingDAO;
import com.cls.projetoluacarmesim.model.Jogador;

import java.sql.SQLException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class StreetsController {

    @FXML
    private Pane rootRua;

    @FXML
    private Pane camadaCenario;

    @FXML
    private Pane camadaObjetos;

    @FXML
    private ImageView personagemView;

    @FXML
    private Label textoInfo;

    @FXML
    private Label textoInteracao;

    private Personagem personagem;
    private Input input;
    private AnimationTimer loop;

    private final GeradorRua geradorRua = new GeradorRua();

    private int numeroRua = 1;
    private TipoRua tipoRuaAtual;

    private final List<Rectangle> inimigos = new ArrayList<>();
    private final List<Rectangle> itens = new ArrayList<>();

    private static final double DISTANCIA_MINIMA_INIMIGOS = 70;
    private boolean coletandoItem = false;

    public void startGame(Scene scene) {

        Parent root = scene.getRoot();

        input = new Input(root);
        personagem = new Personagem(personagemView);

        personagemView.setFitWidth(64);
        personagemView.setFitHeight(64);
        personagemView.setPreserveRatio(true);

        configurarControles(root);

        EstadoJogo estado = EstadoJogo.getInstance();

        if (estado.isRuaEmAndamento() && estado.getTipoRuaAtual() != null) {
            restaurarRuaSalva(estado);
        } else {
            gerarNovaRua();
        }

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

                personagem.update(delta, input);

                limitarMovimento();
                verificarColetaItem();
                verificarContatoInimigo();
                verificarFimDaRua();
            }
        };

        loop.start();
    }

    private void configurarControles(Parent root) {
        root.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.I) {
                abrirInventario();
                e.consume();
            }
        });
    }

    private void abrirInventario() {
        try {
            if (loop != null) {
                loop.stop();
            }

            EstadoJogo estado = EstadoJogo.getInstance();
            estado.setTelaAnteriorInventario("streets");
            salvarRuaAtualNoEstado();

            App.setRoot("inventario");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void salvarRuaAtualNoEstado() {
        EstadoJogo.getInstance().salvarEstadoRuaCompleto(
                numeroRua,
                tipoRuaAtual,
                personagemView.getTranslateX(),
                personagemView.getTranslateY(),
                capturarPosicoes(inimigos),
                capturarPosicoes(itens)
        );
    }

    private List<double[]> capturarPosicoes(List<Rectangle> objetos) {
        List<double[]> posicoes = new ArrayList<>();

        for (Rectangle objeto : objetos) {
            posicoes.add(new double[]{objeto.getLayoutX(), objeto.getLayoutY()});
        }

        return posicoes;
    }

    private void gerarNovaRua() {

        camadaCenario.getChildren().clear();
        camadaObjetos.getChildren().clear();
        inimigos.clear();
        itens.clear();

        tipoRuaAtual = geradorRua.sortearTipoRua();

        desenharRua(tipoRuaAtual);
        gerarInimigos();
        gerarItens();

        textoInfo.setText("Rua " + numeroRua + " - " + nomeBonitoRua(tipoRuaAtual) + " | I - Inventário");
        textoInteracao.setText("");

        personagemView.setLayoutX(70);
        personagemView.setLayoutY(300);
        personagemView.setTranslateX(0);
        personagemView.setTranslateY(0);
        personagemView.toFront();

        salvarRuaAtualNoEstado();
        atualizarRankingDaRun();
    }

    private void restaurarRuaSalva(EstadoJogo estado) {

        camadaCenario.getChildren().clear();
        camadaObjetos.getChildren().clear();
        inimigos.clear();
        itens.clear();

        numeroRua = estado.getNumeroRuaAtual();
        tipoRuaAtual = estado.getTipoRuaAtual();

        desenharRua(tipoRuaAtual);

        for (double[] posicao : estado.getPosicoesInimigosRua()) {
            Rectangle inimigo = criarRetanguloInimigo();
            inimigo.setLayoutX(posicao[0]);
            inimigo.setLayoutY(posicao[1]);

            inimigos.add(inimigo);
            camadaObjetos.getChildren().add(inimigo);
        }

        for (double[] posicao : estado.getPosicoesItensRua()) {
            Rectangle item = criarRetanguloItem();
            item.setLayoutX(posicao[0]);
            item.setLayoutY(posicao[1]);

            itens.add(item);
            camadaObjetos.getChildren().add(item);
        }

        textoInfo.setText("Rua " + numeroRua + " - " + nomeBonitoRua(tipoRuaAtual) + " | I - Inventário");
        textoInteracao.setText("");

        personagemView.setLayoutX(70);
        personagemView.setLayoutY(300);
        personagemView.setTranslateX(estado.getRuaPersonagemTranslateX());
        personagemView.setTranslateY(estado.getRuaPersonagemTranslateY());
        personagemView.toFront();
    }

    private void desenharRua(TipoRua tipoRua) {

        Rectangle fundo = new Rectangle(0, 0, 1280, 720);
        fundo.setFill(Color.rgb(12, 10, 14));
        camadaCenario.getChildren().add(fundo);

        switch (tipoRua) {

            case RUA_RETA:
                desenharRuaReta();
                break;

            case VIELA_ESTREITA:
                desenharVielaEstreita();
                break;

            case CRUZAMENTO:
                desenharCruzamento();
                break;
        }
    }

    private void desenharRuaReta() {

        Rectangle chao = new Rectangle(0, 230, 1280, 270);
        chao.setFill(Color.rgb(45, 45, 50));

        Rectangle paredeCima = new Rectangle(0, 0, 1280, 230);
        paredeCima.setFill(Color.rgb(20, 18, 24));

        Rectangle paredeBaixo = new Rectangle(0, 500, 1280, 220);
        paredeBaixo.setFill(Color.rgb(18, 16, 22));

        camadaCenario.getChildren().addAll(paredeCima, paredeBaixo, chao);
    }

    private void desenharVielaEstreita() {

        Rectangle chao = new Rectangle(0, 280, 1280, 160);
        chao.setFill(Color.rgb(42, 42, 48));

        Rectangle paredeCima = new Rectangle(0, 0, 1280, 280);
        paredeCima.setFill(Color.rgb(18, 14, 22));

        Rectangle paredeBaixo = new Rectangle(0, 440, 1280, 280);
        paredeBaixo.setFill(Color.rgb(16, 13, 20));

        camadaCenario.getChildren().addAll(paredeCima, paredeBaixo, chao);
    }

    private void desenharCruzamento() {

        Rectangle ruaHorizontal = new Rectangle(0, 250, 1280, 220);
        ruaHorizontal.setFill(Color.rgb(45, 45, 52));

        Rectangle ruaVertical = new Rectangle(520, 0, 240, 720);
        ruaVertical.setFill(Color.rgb(38, 38, 45));

        Rectangle canto1 = new Rectangle(0, 0, 520, 250);
        canto1.setFill(Color.rgb(17, 14, 21));

        Rectangle canto2 = new Rectangle(760, 0, 520, 250);
        canto2.setFill(Color.rgb(17, 14, 21));

        Rectangle canto3 = new Rectangle(0, 470, 520, 250);
        canto3.setFill(Color.rgb(15, 12, 19));

        Rectangle canto4 = new Rectangle(760, 470, 520, 250);
        canto4.setFill(Color.rgb(15, 12, 19));

        camadaCenario.getChildren().addAll(
                canto1, canto2, canto3, canto4,
                ruaHorizontal, ruaVertical
        );
    }

    private Rectangle criarRetanguloInimigo() {
        Rectangle inimigo = new Rectangle(45, 65);
        inimigo.setFill(Color.rgb(120, 20, 35));
        inimigo.setArcWidth(10);
        inimigo.setArcHeight(10);
        return inimigo;
    }

    private Rectangle criarRetanguloItem() {
        Rectangle item = new Rectangle(30, 30);
        item.setFill(Color.rgb(200, 160, 70));
        item.setArcWidth(20);
        item.setArcHeight(20);
        return item;
    }

    private void gerarInimigos() {

        int quantidade = geradorRua.sortearQuantidadeInimigos(numeroRua);

        for (int i = 0; i < quantidade; i++) {

            Rectangle inimigo = criarRetanguloInimigo();

            boolean posicaoValida = false;

            for (int tentativa = 0; tentativa < 80; tentativa++) {
                double[] posicao = sortearPosicaoValidaNaRua();

                inimigo.setLayoutX(posicao[0]);
                inimigo.setLayoutY(posicao[1]);

                if (estaLongeDosOutrosInimigos(inimigo)) {
                    posicaoValida = true;
                    break;
                }
            }

            if (!posicaoValida) {
                double[] posicao = sortearPosicaoValidaNaRua();
                inimigo.setLayoutX(posicao[0]);
                inimigo.setLayoutY(posicao[1]);
            }

            inimigos.add(inimigo);
            camadaObjetos.getChildren().add(inimigo);
        }
    }

    private void gerarItens() {

        int quantidade = geradorRua.sortearQuantidadeItens(numeroRua);

        for (int i = 0; i < quantidade; i++) {

            Rectangle item = criarRetanguloItem();

            double[] posicao = sortearPosicaoValidaNaRua();

            item.setLayoutX(posicao[0]);
            item.setLayoutY(posicao[1]);

            itens.add(item);
            camadaObjetos.getChildren().add(item);
        }
    }

    private double sortearYValidoNaRua() {

        switch (tipoRuaAtual) {

            case RUA_RETA:
                return geradorRua.sortearY(260, 430);

            case VIELA_ESTREITA:
                return geradorRua.sortearY(300, 390);

            case CRUZAMENTO:
                return geradorRua.sortearY(260, 430);

            default:
                return 320;
        }
    }

    private void verificarColetaItem() {

        if (coletandoItem) {
            return;
        }

        Rectangle itemColetado = null;

        for (Rectangle item : itens) {
            if (personagemView.getBoundsInParent().intersects(item.getBoundsInParent())) {
                itemColetado = item;
                break;
            }
        }

        if (itemColetado != null) {
            coletandoItem = true;

            itens.remove(itemColetado);
            camadaObjetos.getChildren().remove(itemColetado);

            ItemEspecial item = new ItemEspecial(
                    0,
                    "Fragmento Carmesim",
                    TipoItem.RELIQUIO,
                    "Um fragmento encontrado nas ruas escuras.",
                    false
            );

            EstadoJogo.getInstance().getInventario().adicionarItem(item);
            salvarRuaAtualNoEstado();

            textoInteracao.setText("Você coletou: Fragmento Carmesim.");

            Platform.runLater(() -> coletandoItem = false);
        }
    }

    private void verificarContatoInimigo() {

        for (Rectangle inimigo : inimigos) {
            if (personagemView.getBoundsInParent().intersects(inimigo.getBoundsInParent())) {
                textoInteracao.setText("Inimigo encontrado! Aqui depois entra o combate.");
                return;
            }
        }
    }

    private void verificarFimDaRua() {

        double xPersonagem = personagemView.getLayoutX() + personagemView.getTranslateX();
        double yPersonagem = personagemView.getLayoutY() + personagemView.getTranslateY();

        boolean saiuPelaDireita = xPersonagem >= 1180;

        boolean saiuPorCima = tipoRuaAtual == TipoRua.CRUZAMENTO
                && estaNaParteVerticalDoCruzamento()
                && yPersonagem <= 5;

        boolean saiuPorBaixo = tipoRuaAtual == TipoRua.CRUZAMENTO
                && estaNaParteVerticalDoCruzamento()
                && yPersonagem >= 580;

        if (saiuPelaDireita || saiuPorCima || saiuPorBaixo) {
            numeroRua++;
            gerarNovaRua();
        }
    }

    private boolean estaNaParteVerticalDoCruzamento() {
        double xPersonagem = personagemView.getLayoutX() + personagemView.getTranslateX();

        return xPersonagem >= 520 && xPersonagem <= 760;
    }
    
    private void limitarMovimento() {

        double x = personagemView.getLayoutX() + personagemView.getTranslateX();

        if (x < 0) {
            personagemView.setTranslateX(-personagemView.getLayoutX());
        }

        if (x > 1180) {
            personagemView.setTranslateX(1180 - personagemView.getLayoutX());
        }

        switch (tipoRuaAtual) {

            case RUA_RETA:
                limitarY(230, 440);
                break;

            case VIELA_ESTREITA:
                limitarY(270, 390);
                break;

            case CRUZAMENTO:
                limitarMovimentoCruzamento();
                break;
        }
    }
    
    private void limitarMovimentoCruzamento() {

        double x = personagemView.getLayoutX() + personagemView.getTranslateX();
        double y = personagemView.getLayoutY() + personagemView.getTranslateY();

        boolean dentroFaixaVertical = x >= 520 && x <= 760;
        boolean dentroFaixaHorizontal = y >= 230 && y <= 430;

        if (dentroFaixaVertical) {
            limitarY(0, 580);
        } else {
            limitarY(230, 430);
        }

        y = personagemView.getLayoutY() + personagemView.getTranslateY();

        if (!dentroFaixaHorizontal) {
            limitarX(520, 720);
        }
    }
    
    private void limitarX(double minimo, double maximo) {

        double x = personagemView.getLayoutX() + personagemView.getTranslateX();

        if (x < minimo) {
            personagemView.setTranslateX(minimo - personagemView.getLayoutX());
        }

        if (x > maximo) {
            personagemView.setTranslateX(maximo - personagemView.getLayoutX());
        }
    }

    private void limitarY(double minimo, double maximo) {

        double y = personagemView.getLayoutY() + personagemView.getTranslateY();

        if (y < minimo) {
            personagemView.setTranslateY(minimo - personagemView.getLayoutY());
        }

        if (y > maximo) {
            personagemView.setTranslateY(maximo - personagemView.getLayoutY());
        }
    }

    private String nomeBonitoRua(TipoRua tipoRua) {

        switch (tipoRua) {

            case RUA_RETA:
                return "Rua Reta";

            case VIELA_ESTREITA:
                return "Viela Estreita";

            case CRUZAMENTO:
                return "Cruzamento";

            default:
                return "Rua Desconhecida";
        }
    }

    private boolean estaLongeDosOutrosInimigos(Rectangle novoInimigo) {

        double centroNovoX = novoInimigo.getLayoutX() + novoInimigo.getWidth() / 2;
        double centroNovoY = novoInimigo.getLayoutY() + novoInimigo.getHeight() / 2;

        for (Rectangle inimigoExistente : inimigos) {
            double centroExistenteX = inimigoExistente.getLayoutX() + inimigoExistente.getWidth() / 2;
            double centroExistenteY = inimigoExistente.getLayoutY() + inimigoExistente.getHeight() / 2;

            double diferencaX = centroNovoX - centroExistenteX;
            double diferencaY = centroNovoY - centroExistenteY;

            double distancia = Math.sqrt(diferencaX * diferencaX + diferencaY * diferencaY);

            if (distancia < DISTANCIA_MINIMA_INIMIGOS) {
                return false;
            }
        }

        return true;
    }
    
    private void atualizarRankingDaRun() {

        Jogador jogador = EstadoJogo.getInstance().getJogadorAtual();

        if (jogador == null || jogador.getIdJogador() <= 0) {
            return;
        }

        try {
            RankingDAO rankingDAO = new RankingDAO();
            rankingDAO.registrarRuaExplorada(jogador.getIdJogador(), numeroRua);

        } catch (SQLException e) {
            System.out.println("Erro ao atualizar ranking: " + e.getMessage());
        }
    }
    
    private double[] sortearPosicaoValidaNaRua() {

        switch (tipoRuaAtual) {

            case RUA_RETA:
                return new double[]{
                        geradorRua.sortearX(300, 1120),
                        geradorRua.sortearY(260, 430)
                };

            case VIELA_ESTREITA:
                return new double[]{
                        geradorRua.sortearX(300, 1120),
                        geradorRua.sortearY(300, 390)
                };

            case CRUZAMENTO:
                boolean usarParteHorizontal = Math.random() < 0.6;

                if (usarParteHorizontal) {
                    return new double[]{
                            geradorRua.sortearX(300, 1120),
                            geradorRua.sortearY(260, 420)
                    };
                }

                return new double[]{
                        geradorRua.sortearX(540, 700),
                        geradorRua.sortearY(60, 620)
                };

            default:
                return new double[]{320, 320};
        }
    }
}
