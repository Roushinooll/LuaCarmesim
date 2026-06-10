package com.cls.projetoluacarmesim;

import com.cls.projetoluacarmesim.enums.TipoItem;
import com.cls.projetoluacarmesim.enums.TipoRua;
import com.cls.projetoluacarmesim.model.ItemEspecial;
import com.cls.projetoluacarmesim.util.GeradorRua;
import com.cls.projetoluacarmesim.util.Input;
import com.cls.projetoluacarmesim.util.Personagem;
import com.cls.projetoluacarmesim.dao.RankingDAO;
import com.cls.projetoluacarmesim.dao.ReceitaDAO;
import com.cls.projetoluacarmesim.model.Jogador;
import com.cls.projetoluacarmesim.model.FormulaPocao;
import com.cls.projetoluacarmesim.model.IngredienteFormula;
import com.cls.projetoluacarmesim.model.JogadorFormula;

import java.sql.SQLException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
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

    @FXML
    private Pane menuRua;

    @FXML
    private Button botaoVoltarRestroom;

    private Personagem personagem;
    private Input input;
    private AnimationTimer loop;

    private final GeradorRua geradorRua = new GeradorRua();

    private int numeroRua = 1;
    private TipoRua tipoRuaAtual;

    private final List<Rectangle> inimigos = new ArrayList<>();
    private final List<Rectangle> itens = new ArrayList<>();
    private final List<Rectangle> receitas = new ArrayList<>();
    private final Map<Rectangle, Integer> idsReceitas = new HashMap<>();
    private final List<String[]> ingredientesColetaveisDaRua = new ArrayList<>();

    private static final double DISTANCIA_MINIMA_INIMIGOS = 70;
    private boolean coletandoItem = false;
    private boolean menuAberto = false;
    private long versaoRua = 0;

    @FXML
    private void initialize() {
        if (menuRua != null) {
            menuRua.setVisible(false);
            menuRua.setManaged(false);
        }
    }

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

                if (!menuAberto) {
                    personagem.update(delta, input);

                    limitarMovimento();
                    verificarColetaItem();
                    verificarColetaReceita();
                    verificarContatoInimigo();
                    verificarFimDaRua();
                }
            }
        };

        loop.start();
    }

    private void configurarControles(Parent root) {
        root.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.TAB) {
                if (menuAberto) {
                    fecharMenuRua();
                } else {
                    abrirMenuRua();
                }
                e.consume();
                return;
            }

            if (menuAberto) {
                if (e.getCode() == KeyCode.ESCAPE) {
                    fecharMenuRua();
                }
                e.consume();
                return;
            }

            if (e.getCode() == KeyCode.I) {
                abrirInventario();
                e.consume();
            }
        });
    }

    private void abrirMenuRua() {
        menuAberto = true;
        limparInputMovimento();

        if (menuRua != null) {
            menuRua.setManaged(true);
            menuRua.setVisible(true);
            menuRua.toFront();
        }

        textoInteracao.setText("Menu aberto.");

        Platform.runLater(() -> {
            if (botaoVoltarRestroom != null) {
                botaoVoltarRestroom.requestFocus();
            } else if (menuRua != null) {
                menuRua.requestFocus();
            }
        });
    }

    @FXML
    private void fecharMenuRua() {
        menuAberto = false;
        limparInputMovimento();

        if (menuRua != null) {
            menuRua.setVisible(false);
            menuRua.setManaged(false);
        }

        textoInteracao.setText("");

        Platform.runLater(() -> rootRua.getScene().getRoot().requestFocus());
    }

    @FXML
    private void voltarParaRestroomPeloMenu() {
        try {
            menuAberto = false;
            limparInputMovimento();

            if (loop != null) {
                loop.stop();
            }

            novaVersaoRua();
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

    private void abrirInventario() {
        try {
            if (loop != null) {
                loop.stop();
            }

            EstadoJogo estado = EstadoJogo.getInstance();
            estado.setTelaAnteriorInventario("streets");
            salvarRuaAtualNoEstado();
            novaVersaoRua();

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
                capturarPosicoes(itens),
                capturarPosicoes(receitas),
                capturarIdsReceitas()
        );
    }

    private List<Integer> capturarIdsReceitas() {
        List<Integer> ids = new ArrayList<>();

        for (Rectangle receita : receitas) {
            Integer idReceita = idsReceitas.get(receita);

            if (idReceita != null) {
                ids.add(idReceita);
            }
        }

        return ids;
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
        receitas.clear();
        idsReceitas.clear();
        ingredientesColetaveisDaRua.clear();

        tipoRuaAtual = geradorRua.sortearTipoRua();

        desenharRua(tipoRuaAtual);
        long versaoAtual = novaVersaoRua();

        gerarInimigos();
        gerarItens();
        gerarReceitaEmSegundoPlano(versaoAtual, numeroRua, tipoRuaAtual);
        prepararItensColetaveisDaRuaEmSegundoPlano(versaoAtual);

        textoInfo.setText("Rua " + numeroRua + " - " + nomeBonitoRua(tipoRuaAtual) + " | I - Inventário | TAB - Menu");
        textoInteracao.setText("");

        personagemView.setLayoutX(70);
        personagemView.setLayoutY(300);
        personagemView.setTranslateX(0);
        personagemView.setTranslateY(0);
        personagemView.toFront();

        salvarRuaAtualNoEstado();
        atualizarRankingDaRunEmSegundoPlano(numeroRua);
    }

    private void restaurarRuaSalva(EstadoJogo estado) {

        camadaCenario.getChildren().clear();
        camadaObjetos.getChildren().clear();
        inimigos.clear();
        itens.clear();
        receitas.clear();
        idsReceitas.clear();
        ingredientesColetaveisDaRua.clear();

        long versaoAtual = novaVersaoRua();

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

        List<double[]> posicoesReceitas = estado.getPosicoesReceitasRua();
        List<Integer> idsReceitasSalvas = estado.getIdsReceitasRua();

        for (int i = 0; i < posicoesReceitas.size(); i++) {
            if (i >= idsReceitasSalvas.size()) {
                break;
            }

            double[] posicao = posicoesReceitas.get(i);
            int idReceita = idsReceitasSalvas.get(i);

            Rectangle receita = criarRetanguloReceita();
            receita.setLayoutX(posicao[0]);
            receita.setLayoutY(posicao[1]);

            receitas.add(receita);
            idsReceitas.put(receita, idReceita);
            camadaObjetos.getChildren().add(receita);
        }

        textoInfo.setText("Rua " + numeroRua + " - " + nomeBonitoRua(tipoRuaAtual) + " | I - Inventário | TAB - Menu");
        textoInteracao.setText("");

        personagemView.setLayoutX(70);
        personagemView.setLayoutY(300);
        personagemView.setTranslateX(estado.getRuaPersonagemTranslateX());
        personagemView.setTranslateY(estado.getRuaPersonagemTranslateY());
        personagemView.toFront();

        prepararItensColetaveisDaRuaEmSegundoPlano(versaoAtual);
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

    private Rectangle criarRetanguloReceita() {
        Rectangle receita = new Rectangle(34, 26);
        receita.setFill(Color.rgb(125, 80, 180));
        receita.setArcWidth(8);
        receita.setArcHeight(8);
        return receita;
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


    private void gerarReceitaEmSegundoPlano(long versaoEsperada, int ruaReferencia, TipoRua tipoReferencia) {

        Jogador jogador = EstadoJogo.getInstance().getJogadorAtual();

        if (jogador == null || jogador.getIdJogador() <= 0) {
            return;
        }

        if (Math.random() > calcularChanceReceita()) {
            return;
        }

        executarEmSegundoPlano("lua-carmesim-receita-rua", () -> {
            FormulaPocao formula = null;

            try {
                ReceitaDAO receitaDAO = new ReceitaDAO();
                formula = sortearReceitaDisponivel(
                        receitaDAO,
                        jogador.getIdJogador()
                );
            } catch (SQLException e) {
                System.out.println("Erro ao gerar receita: " + e.getMessage());
            }

            final FormulaPocao formulaSorteada = formula;

            if (formulaSorteada == null) {
                return;
            }

            Platform.runLater(() -> {
                if (!ruaAindaAtual(versaoEsperada, ruaReferencia, tipoReferencia)) {
                    return;
                }

                Rectangle receita = criarRetanguloReceita();
                double[] posicao = sortearPosicaoValidaNaRua();

                receita.setLayoutX(posicao[0]);
                receita.setLayoutY(posicao[1]);

                receitas.add(receita);
                idsReceitas.put(receita, formulaSorteada.getIdFormula());
                camadaObjetos.getChildren().add(receita);

                salvarRuaAtualNoEstado();
            });
        });
    }

    private double calcularChanceReceita() {
        if (numeroRua <= 3) {
            return 0.20;
        }

        if (numeroRua <= 6) {
            return 0.25;
        }

        if (numeroRua <= 10) {
            return 0.30;
        }

        if (numeroRua <= 14) {
            return 0.35;
        }

        return 0.65;
    }

    private FormulaPocao sortearReceitaDisponivel(ReceitaDAO receitaDAO, int idJogador) throws SQLException {

        /*
         * Agora qualquer sequência pode aparecer em qualquer rua.
         * A raridade fica no peso do sorteio: sequência 9 é mais comum,
         * enquanto sequência 5 é a mais rara.
         *
         * Mesmo assim, o ReceitaDAO continua bloqueando receitas que pulam
         * sequência. Exemplo: Palhaço só pode aparecer se o jogador já tiver
         * aprendido Vidente.
         */
        for (int tentativa = 0; tentativa < 10; tentativa++) {
            int nivelSequencia = sortearNivelReceitaPorRaridade();

            FormulaPocao formula = receitaDAO.sortearNaoAprendidaPorNivel(
                    idJogador,
                    nivelSequencia
            );

            if (formula != null) {
                return formula;
            }
        }

        /*
         * Fallback:
         * Se o sorteio cair várias vezes em níveis bloqueados ou já aprendidos,
         * tenta encontrar qualquer receita disponível, da sequência mais comum
         * para a mais rara.
         */
        int[] niveis = {9, 8, 7, 6, 5};

        for (int nivel : niveis) {
            FormulaPocao formula = receitaDAO.sortearNaoAprendidaPorNivel(
                    idJogador,
                    nivel
            );

            if (formula != null) {
                return formula;
            }
        }

        return null;
    }

    private int sortearNivelReceitaPorRaridade() {
        double sorteio = Math.random();

        if (sorteio < 0.55) {
            return 9;
        }

        if (sorteio < 0.78) {
            return 8;
        }

        if (sorteio < 0.91) {
            return 7;
        }

        if (sorteio < 0.98) {
            return 6;
        }

        return 5;
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

            ItemEspecial item = sortearItemColetavel();

            EstadoJogo.getInstance().getInventario().adicionarItem(item);

            textoInteracao.setText("Você coletou: " + item.getNomeItem() + ".");

            Platform.runLater(() -> coletandoItem = false);
        }
    }


    private ItemEspecial sortearItemColetavel() {
        if (!ingredientesColetaveisDaRua.isEmpty()) {
            int indice = (int) (Math.random() * ingredientesColetaveisDaRua.size());
            String[] ingrediente = ingredientesColetaveisDaRua.get(indice);

            return new ItemEspecial(
                    0,
                    ingrediente[0],
                    TipoItem.INGREDIENTE,
                    ingrediente[1],
                    false
            );
        }

        return sortearIngredienteBasico();
    }

    private void prepararItensColetaveisDaRuaEmSegundoPlano(long versaoEsperada) {
        ingredientesColetaveisDaRua.clear();

        Jogador jogador = EstadoJogo.getInstance().getJogadorAtual();

        if (jogador == null || jogador.getIdJogador() <= 0) {
            return;
        }

        int ruaReferencia = numeroRua;
        TipoRua tipoReferencia = tipoRuaAtual;

        executarEmSegundoPlano("lua-carmesim-ingredientes-rua", () -> {
            List<String[]> ingredientesCarregados = buscarIngredientesColetaveisDoBanco(jogador.getIdJogador());

            Platform.runLater(() -> {
                if (!ruaAindaAtual(versaoEsperada, ruaReferencia, tipoReferencia)) {
                    return;
                }

                ingredientesColetaveisDaRua.clear();
                ingredientesColetaveisDaRua.addAll(ingredientesCarregados);
            });
        });
    }

    private List<String[]> buscarIngredientesColetaveisDoBanco(int idJogador) {
        List<String[]> ingredientesCarregados = new ArrayList<>();
        List<JogadorFormula> receitasAprendidas = EstadoJogo.getInstance()
                .getInventario()
                .listarReceitasAprendidas(idJogador);

        Map<String, String> ingredientesUnicos = new HashMap<>();

        for (JogadorFormula jogadorFormula : receitasAprendidas) {
            if (jogadorFormula.getFormula() == null) {
                continue;
            }

            for (IngredienteFormula ingrediente : jogadorFormula.getFormula().getIngredientes()) {
                if (ingrediente.getNomeIngrediente() == null || ingrediente.getNomeIngrediente().isBlank()) {
                    continue;
                }

                ingredientesUnicos.putIfAbsent(
                        ingrediente.getNomeIngrediente(),
                        "Ingrediente de alquimia: " + ingrediente.getTipoIngrediente()
                );
            }
        }

        for (Map.Entry<String, String> ingrediente : ingredientesUnicos.entrySet()) {
            ingredientesCarregados.add(new String[]{ingrediente.getKey(), ingrediente.getValue()});
        }

        return ingredientesCarregados;
    }

    private ItemEspecial sortearIngredienteBasico() {
        String[][] ingredientesBasicos = {
            {"Erva de Névoa Prateada", "Ingrediente básico para poções do caminho Vidente."},
            {"Fragmento de Vidro Manchado", "Mineral usado em fórmulas de percepção oculta."},
            {"Olho Seco de Corvo Urbano", "Parte de monstro usada em rituais de visão."},
            {"Orvalho da Meia-Noite", "Erva rara coletada sob a Lua Carmesim."},
            {"Sangue Coagulado", "Fluido biológico usado em poções violentas."},
            {"Garra de Cão Mutado", "Parte de monstro usada em poções de caça."},
            {"Corda de Violino Rompida", "Item místico usado em poções de som e oratória."},
            {"Página de Canção Antiga", "Item ritualístico usado em fórmulas do caminho Bardo."}
        };

        int indice = (int) (Math.random() * ingredientesBasicos.length);

        return new ItemEspecial(
                0,
                ingredientesBasicos[indice][0],
                TipoItem.INGREDIENTE,
                ingredientesBasicos[indice][1],
                false
        );
    }

    private void verificarColetaReceita() {

        Rectangle receitaColetada = null;

        for (Rectangle receita : receitas) {
            if (personagemView.getBoundsInParent().intersects(receita.getBoundsInParent())) {
                receitaColetada = receita;
                break;
            }
        }

        if (receitaColetada == null) {
            return;
        }

        Integer idReceita = idsReceitas.get(receitaColetada);

        if (idReceita == null) {
            return;
        }

        Jogador jogador = EstadoJogo.getInstance().getJogadorAtual();

        if (jogador == null || jogador.getIdJogador() <= 0) {
            return;
        }

        receitas.remove(receitaColetada);
        idsReceitas.remove(receitaColetada);
        camadaObjetos.getChildren().remove(receitaColetada);
        salvarRuaAtualNoEstado();

        textoInteracao.setText("Você encontrou uma receita. Sincronizando...");

        aprenderReceitaEmSegundoPlano(
                jogador.getIdJogador(),
                idReceita,
                versaoRua,
                numeroRua,
                tipoRuaAtual
        );
    }

    private void aprenderReceitaEmSegundoPlano(
            int idJogador,
            int idReceita,
            long versaoEsperada,
            int ruaReferencia,
            TipoRua tipoReferencia
    ) {
        executarEmSegundoPlano("lua-carmesim-aprender-receita", () -> {
            FormulaPocao formula = null;
            String caminho = "Desconhecido";
            String erro = null;

            try {
                ReceitaDAO receitaDAO = new ReceitaDAO();

                if (!receitaDAO.jaAprendeu(idJogador, idReceita)) {
                    receitaDAO.marcarComoAprendida(idJogador, idReceita);
                }

                formula = receitaDAO.buscarPorId(idReceita);

                if (formula != null) {
                    caminho = receitaDAO.getCaminhoPorNome(formula.getNomePocao());
                }
            } catch (SQLException e) {
                erro = e.getMessage();
                System.out.println("Erro ao aprender receita: " + e.getMessage());
            }

            final FormulaPocao formulaAprendida = formula;
            final String caminhoFormula = caminho;
            final String erroFinal = erro;

            Platform.runLater(() -> {
                if (!ruaAindaAtual(versaoEsperada, ruaReferencia, tipoReferencia)) {
                    return;
                }

                if (erroFinal != null) {
                    textoInteracao.setText("Erro ao aprender receita.");
                    return;
                }

                if (formulaAprendida != null) {
                    textoInteracao.setText(
                            "Receita aprendida: "
                                    + formulaAprendida.getNomePocao()
                                    + " | Caminho: "
                                    + caminhoFormula
                                    + " | Sequência "
                                    + formulaAprendida.getNivelSequencia()
                    );
                } else {
                    textoInteracao.setText("Você aprendeu uma nova receita.");
                }

                prepararItensColetaveisDaRuaEmSegundoPlano(versaoEsperada);
            });
        });
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
    
    private void atualizarRankingDaRunEmSegundoPlano(int ruaExplorada) {

        Jogador jogador = EstadoJogo.getInstance().getJogadorAtual();

        if (jogador == null || jogador.getIdJogador() <= 0) {
            return;
        }

        executarEmSegundoPlano("lua-carmesim-ranking-rua", () -> {
            try {
                RankingDAO rankingDAO = new RankingDAO();
                rankingDAO.registrarRuaExplorada(jogador.getIdJogador(), ruaExplorada);

            } catch (SQLException e) {
                System.out.println("Erro ao atualizar ranking: " + e.getMessage());
            }
        });
    }

    private long novaVersaoRua() {
        versaoRua++;
        return versaoRua;
    }

    private boolean ruaAindaAtual(long versaoEsperada, int ruaReferencia, TipoRua tipoReferencia) {
        return versaoRua == versaoEsperada
                && numeroRua == ruaReferencia
                && tipoRuaAtual == tipoReferencia
                && rootRua != null
                && rootRua.getScene() != null;
    }

    private void executarEmSegundoPlano(String nomeThread, Runnable acao) {
        Thread thread = new Thread(acao, nomeThread);
        thread.setDaemon(true);
        thread.start();
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
