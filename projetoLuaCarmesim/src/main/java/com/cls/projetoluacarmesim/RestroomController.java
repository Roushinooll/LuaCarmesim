package com.cls.projetoluacarmesim;

import com.cls.projetoluacarmesim.enums.TipoItem;
import com.cls.projetoluacarmesim.model.ItemEspecial;
import com.cls.projetoluacarmesim.util.Input;
import com.cls.projetoluacarmesim.util.ObjetoInterativo;
import com.cls.projetoluacarmesim.util.Personagem;
import com.cls.projetoluacarmesim.util.SessaoJogador;
import com.cls.projetoluacarmesim.dao.ReceitaDAO;
import com.cls.projetoluacarmesim.model.FormulaPocao;
import com.cls.projetoluacarmesim.model.IngredienteFormula;
import com.cls.projetoluacarmesim.model.Jogador;
import com.cls.projetoluacarmesim.model.JogadorFormula;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.sql.SQLException;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

public class RestroomController {

    @FXML
    private ImageView fundoRestroom;

    @FXML
    private ImageView personagemView;

    @FXML
    private Rectangle mesa;

    @FXML
    private Rectangle porta;

    @FXML
    private Rectangle caldeirao;

    @FXML
    private Label textoInteracao;

    @FXML
    private Pane caixaDialogo;

    @FXML
    private Label textoDialogo;

    @FXML
    private Label textoInstrucaoDialogo;

    @FXML
    private Pane painelCaldeirao;

    @FXML
    private ListView<String> listaReceitasCaldeirao;

    @FXML
    private Label textoCaldeirao;

    @FXML
    private Button botaoCraftar;

    private Personagem personagem;
    private Input input;
    private AnimationTimer loop;

    private boolean pegouArma = false;
    private boolean podeInteragir = true;
    private boolean dialogoAberto = false;
    private boolean inventarioAberto = false;
    private boolean caldeiraoAberto = false;

    private String[] dialogosAtuais = new String[0];
    private int indiceDialogoAtual = 0;
    private Runnable acaoAoFinalDialogo;
    private boolean dialogoPermiteCancelar = true;

    private static final double LARGURA_CENARIO_QUARTO = 1280.0;
    private static final double ALTURA_CENARIO_QUARTO = 720.0;

    private final List<ObjetoInterativo> objetosInterativos = new ArrayList<>();
    private final Map<String, FormulaPocao> receitasCaldeirao = new LinkedHashMap<>();

    @FXML
    private void initialize() {
        if (painelCaldeirao != null) {
            painelCaldeirao.setVisible(false);
        }

        if (listaReceitasCaldeirao != null) {
            listaReceitasCaldeirao.setFocusTraversable(true);
            listaReceitasCaldeirao.getSelectionModel().selectedItemProperty().addListener((obs, antigo, novo) -> {
                atualizarTextoReceitaSelecionada(novo);
            });
        }

        if (caixaDialogo != null) {
            caixaDialogo.setOnMouseClicked(e -> avancarDialogoRestroom());
        }
    }

    public void startGame(Scene scene) {

        Parent root = scene.getRoot();

        input = new Input(root);
        personagem = new Personagem(personagemView);

        EstadoJogo estado = EstadoJogo.getInstance();
        pegouArma = estado.getInventario().possuiItem("Revólver Enferrujado");
        atualizarFundoRestroom();

        if (estado.isPosicaoPersonagemSalva()) {
            personagemView.setTranslateX(estado.getPersonagemTranslateX());
            personagemView.setTranslateY(estado.getPersonagemTranslateY());
            limitarPersonagemAoCenario();
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
                    limitarPersonagemAoCenario();
                    verificarInteracoes();
                }
            }
        };

        loop.start();
    }

    private void limitarPersonagemAoCenario() {
        if (personagemView == null) {
            return;
        }

        double larguraPersonagem = personagemView.getFitWidth() > 0
                ? personagemView.getFitWidth()
                : Personagem.TAMANHO_VISUAL;

        double alturaPersonagem = personagemView.getFitHeight() > 0
                ? personagemView.getFitHeight()
                : Personagem.TAMANHO_VISUAL;

        double minTranslateX = -personagemView.getLayoutX();
        double maxTranslateX = LARGURA_CENARIO_QUARTO - personagemView.getLayoutX() - larguraPersonagem;

        double minTranslateY = -personagemView.getLayoutY();
        double maxTranslateY = ALTURA_CENARIO_QUARTO - personagemView.getLayoutY() - alturaPersonagem;

        personagemView.setTranslateX(clamp(personagemView.getTranslateX(), minTranslateX, maxTranslateX));
        personagemView.setTranslateY(clamp(personagemView.getTranslateY(), minTranslateY, maxTranslateY));
    }

    private double clamp(double valor, double minimo, double maximo) {
        if (valor < minimo) {
            return minimo;
        }

        if (valor > maximo) {
            return maximo;
        }

        return valor;
    }

    private void atualizarFundoRestroom() {
        if (fundoRestroom == null) {
            return;
        }

        boolean possuiArma = pegouArma
                || EstadoJogo.getInstance().getInventario().possuiItem("Revólver Enferrujado");

        String caminho = possuiArma
                ? "/image/fundos/restroom_without.jpg"
                : "/image/fundos/restroom_gun.jpg";

        java.net.URL recurso = getClass().getResource(caminho);

        if (recurso == null) {
            System.out.println("Imagem da restroom não encontrada: " + caminho);
            return;
        }

        fundoRestroom.setImage(new Image(recurso.toExternalForm()));
        fundoRestroom.setFitWidth(1280);
        fundoRestroom.setFitHeight(720);
        fundoRestroom.setPreserveRatio(false);
        fundoRestroom.setSmooth(false);
        fundoRestroom.setMouseTransparent(true);
        fundoRestroom.toBack();
    }

    private void configurarControles(Parent root) {

        root.addEventFilter(KeyEvent.KEY_PRESSED, e -> {

            if (dialogoAberto) {

                if (caldeiraoAberto) {
                    if (e.getCode() == KeyCode.ESCAPE) {
                        fecharCaldeirao();
                        e.consume();
                        return;
                    }

                    if (e.getCode() == KeyCode.ENTER) {
                        craftarReceitaSelecionada();
                        e.consume();
                        return;
                    }

                    if (e.getCode() == KeyCode.F || e.getCode() == KeyCode.I) {
                        input.interact = false;
                        e.consume();
                    }

                    return;
                }

                if (e.getCode() == KeyCode.ENTER || e.getCode() == KeyCode.SPACE) {
                    avancarDialogoRestroom();
                    e.consume();
                    return;
                }

                if (e.getCode() == KeyCode.ESCAPE && dialogoPermiteCancelar) {
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
                "F - Examinar mesa",
                this::interagirComMesa
        );

        ObjetoInterativo caldeiraoInterativo = new ObjetoInterativo(
                caldeirao,
                "F - Usar bancada de alquimia",
                this::interagirComCaldeirao
        );

        ObjetoInterativo portaInterativa = new ObjetoInterativo(
                porta,
                "F - Sair para a rua",
                this::tentarSairParaRua
        );

        objetosInterativos.add(mesaInterativa);
        objetosInterativos.add(caldeiraoInterativo);
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

    private void interagirComMesa() {

        EstadoJogo estado = EstadoJogo.getInstance();

        if (pegouArma || estado.getInventario().possuiItem("Revólver Enferrujado")) {
            textoInteracao.setText("Você já pegou a arma.");
            mesa.setStyle("-fx-fill: white;");
            return;
        }

        if (!estado.isDialogoInicialMesaVisto()) {
            estado.setDialogoInicialMesaVisto(true);
            abrirDialogoSequencial(
                    new String[]{
                            "Sobre a mesa, algo metálico repousa sob uma camada fina de poeira.",
                            "Um revólver antigo. Frio, pesado, silencioso demais para este quarto.",
                            "O cheiro de óleo velho e pólvora desperta uma sensação que não parece minha.",
                            "Meus dedos hesitam antes de tocar o cabo, como se esperassem uma lembrança voltar.",
                            "Se eu abrir aquela porta desarmado, talvez este novo corpo não dure muito tempo."
                    },
                    "ENTER / ESPAÇO - Continuar",
                    false,
                    this::pegarArma
            );
            return;
        }

        pegarArma();
    }

    private void pegarArma() {

        EstadoJogo estado = EstadoJogo.getInstance();

        if (pegouArma || estado.getInventario().possuiItem("Revólver Enferrujado")) {
            textoInteracao.setText("Você já pegou a arma.");
            mesa.setStyle("-fx-fill: white;");
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
            SessaoJogador.aprenderReceitaInicial(estado.getJogadorAtual());
        }

        textoInteracao.setText("Você pegou: Revólver Enferrujado.");

        atualizarFundoRestroom();
        mesa.setStyle("-fx-fill: white;");
    }

    private void tentarSairParaRua() {

        boolean possuiArma = EstadoJogo.getInstance()
                .getInventario()
                .possuiItem("Revólver Enferrujado");

        if (!possuiArma) {
            abrirDialogoSequencial(
                    new String[]{"Tem certeza que não esqueceu nada?"},
                    "ENTER - Sair     ESC - Voltar",
                    true,
                    this::sairParaRua
            );
            return;
        }

        if (EstadoJogo.getInstance().getJogadorAtual() == null) {
            textoInteracao.setText("O jogador não foi sincronizado. Volte ao menu e clique em Jogar novamente.");
            return;
        }

        sairParaRua();
    }

    private void interagirComCaldeirao() {
        EstadoJogo estado = EstadoJogo.getInstance();

        if (!estado.isDialogoInicialCaldeiraoVisto()) {
            estado.setDialogoInicialCaldeiraoVisto(true);
            abrirDialogoSequencial(
                    new String[]{
                            "A bancada de alquimia está coberta por frascos, ervas secas e símbolos riscados às pressas.",
                            "Eu não lembro de ter estudado nada disso... mas meus olhos reconhecem padrões nas fórmulas.",
                            "Não são simples remédios. Estas poções parecem reescrever o corpo e a mente de quem as bebe.",
                            "Cada ingrediente tem um peso, cada receita parece um degrau de uma escada proibida.",
                            "Se eu reunir os materiais certos, talvez consiga transformar este medo em alguma forma de poder."
                    },
                    "ENTER / ESPAÇO - Continuar",
                    false,
                    this::abrirCaldeirao
            );
            return;
        }

        abrirCaldeirao();
    }

    private void abrirCaldeirao() {
        Jogador jogador = EstadoJogo.getInstance().getJogadorAtual();

        if (jogador == null || jogador.getIdJogador() <= 0) {
            textoInteracao.setText("Digite seu nome no menu antes de usar o caldeirão.");
            return;
        }

        dialogoAberto = true;
        caldeiraoAberto = true;
        podeInteragir = false;
        input.interact = false;
        textoInteracao.setText("");

        carregarReceitasNoCaldeirao(jogador);

        painelCaldeirao.setVisible(true);
        painelCaldeirao.toFront();

        Platform.runLater(() -> {
            painelCaldeirao.requestFocus();
            listaReceitasCaldeirao.requestFocus();
        });
    }

    private void carregarReceitasNoCaldeirao(Jogador jogador) {
        receitasCaldeirao.clear();
        listaReceitasCaldeirao.getItems().clear();

        List<JogadorFormula> receitas = EstadoJogo.getInstance()
                .getInventario()
                .listarReceitasAprendidas(jogador.getIdJogador());

        if (receitas.isEmpty()) {
            textoCaldeirao.setText("Você ainda não aprendeu nenhuma receita.");
            botaoCraftar.setDisable(true);
            return;
        }

        for (JogadorFormula jogadorFormula : receitas) {
            FormulaPocao formula = jogadorFormula.getFormula();

            if (formula == null) {
                continue;
            }

            boolean podeCriar = EstadoJogo.getInstance()
                    .getInventario()
                    .possuiIngredientes(formula);

            String status = podeCriar ? "PRONTA" : "FALTAM ITENS";
            String opcao = "[" + status + "] "
                    + formula.getNomePocao()
                    + " | Sequência "
                    + formula.getNivelSequencia();

            receitasCaldeirao.put(opcao, formula);
            listaReceitasCaldeirao.getItems().add(opcao);
        }

        if (receitasCaldeirao.isEmpty()) {
            textoCaldeirao.setText("Nenhuma receita válida foi encontrada.");
            botaoCraftar.setDisable(true);
            return;
        }

        botaoCraftar.setDisable(false);
        listaReceitasCaldeirao.getSelectionModel().selectFirst();
        atualizarTextoReceitaSelecionada(listaReceitasCaldeirao.getSelectionModel().getSelectedItem());
    }

    private void atualizarTextoReceitaSelecionada(String chaveReceita) {
        if (textoCaldeirao == null) {
            return;
        }

        if (chaveReceita == null) {
            textoCaldeirao.setText("Escolha uma receita.");
            return;
        }

        FormulaPocao formula = receitasCaldeirao.get(chaveReceita);

        if (formula == null) {
            textoCaldeirao.setText("Receita inválida.");
            return;
        }

        List<String> faltantes = EstadoJogo.getInstance()
                .getInventario()
                .listarIngredientesFaltantes(formula);

        String ingredientes = resumirIngredientes(formula);

        if (ingredientes.isBlank()) {
            ingredientes = "Sem ingredientes cadastrados.";
        }

        String texto = formula.getNomePocao()
                + "\nEfeito: "
                + formula.getEfeitoPrincipal()
                + "\nIngredientes: "
                + ingredientes;

        if (!faltantes.isEmpty()) {
            texto += "\nFaltando: " + String.join(", ", faltantes);
        } else {
            texto += "\nStatus: pronta para criar.";
        }

        textoCaldeirao.setText(texto);
    }

    @FXML
    private void craftarReceitaSelecionada() {
        if (!caldeiraoAberto) {
            return;
        }

        String selecionada = listaReceitasCaldeirao.getSelectionModel().getSelectedItem();

        if (selecionada == null) {
            textoCaldeirao.setText("Escolha uma receita primeiro.");
            return;
        }

        FormulaPocao formulaEscolhida = receitasCaldeirao.get(selecionada);

        if (formulaEscolhida == null) {
            textoCaldeirao.setText("Receita inválida.");
            return;
        }

        if (criarPocaoNoCaldeirao(formulaEscolhida)) {
            Jogador jogador = EstadoJogo.getInstance().getJogadorAtual();
            carregarReceitasNoCaldeirao(jogador);
        }
    }

    @FXML
    private void fecharCaldeirao() {
        caldeiraoAberto = false;
        dialogoAberto = false;
        podeInteragir = true;
        input.interact = false;

        painelCaldeirao.setVisible(false);
        textoInteracao.setText("");

        Platform.runLater(() -> painelCaldeirao.getScene().getRoot().requestFocus());
    }

    private boolean criarPocaoNoCaldeirao(FormulaPocao formula) {
        List<String> faltantes = EstadoJogo.getInstance()
                .getInventario()
                .listarIngredientesFaltantes(formula);

        if (!faltantes.isEmpty()) {
            textoCaldeirao.setText(
                    "Ingredientes insuficientes.\nFalta: " + String.join(", ", faltantes)
            );
            textoInteracao.setText("Faltam ingredientes para essa poção.");
            return false;
        }

        boolean consumiu = EstadoJogo.getInstance()
                .getInventario()
                .consumirIngredientes(formula);

        if (!consumiu) {
            textoCaldeirao.setText("Não foi possível consumir os ingredientes.");
            textoInteracao.setText("Não foi possível consumir os ingredientes.");
            return false;
        }

        ItemEspecial pocao = EstadoJogo.getInstance()
                .getInventario()
                .criarPocao(formula);

        if (pocao == null) {
            textoCaldeirao.setText("Não foi possível criar a poção.");
            textoInteracao.setText("Não foi possível criar a poção.");
            return false;
        }

        textoInteracao.setText("Você criou: " + pocao.getNomeItem() + ".");
        textoCaldeirao.setText(
                pocao.getNomeItem()
                        + " foi adicionada ao inventário.\nEfeito: "
                        + pocao.getEfeito()
        );
        return true;
    }

    private String resumirIngredientes(FormulaPocao formula) {
        List<String> partes = new ArrayList<>();

        if (formula == null || formula.getIngredientes() == null) {
            return "";
        }

        for (IngredienteFormula ingrediente : formula.getIngredientes()) {
            partes.add(ingrediente.getQuantidade() + "x " + ingrediente.getNomeIngrediente());
        }

        return String.join(", ", partes);
    }

    private void abrirDialogoSequencial(
            String[] dialogos,
            String textoInstrucao,
            boolean permiteCancelar,
            Runnable acaoFinal
    ) {
        dialogoAberto = true;
        podeInteragir = false;
        input.interact = false;

        dialogosAtuais = dialogos == null ? new String[0] : dialogos;
        indiceDialogoAtual = 0;
        acaoAoFinalDialogo = acaoFinal;
        dialogoPermiteCancelar = permiteCancelar;

        textoInteracao.setText("");

        if (textoInstrucaoDialogo != null) {
            textoInstrucaoDialogo.setText(textoInstrucao);
        }

        if (dialogosAtuais.length == 0) {
            finalizarDialogoRestroom();
            return;
        }

        textoDialogo.setText(dialogosAtuais[indiceDialogoAtual]);
        caixaDialogo.setVisible(true);
        caixaDialogo.toFront();

        Platform.runLater(() -> caixaDialogo.getScene().getRoot().requestFocus());
    }

    private void avancarDialogoRestroom() {
        if (!dialogoAberto || caldeiraoAberto) {
            return;
        }

        indiceDialogoAtual++;

        if (indiceDialogoAtual >= dialogosAtuais.length) {
            finalizarDialogoRestroom();
            return;
        }

        textoDialogo.setText(dialogosAtuais[indiceDialogoAtual]);
    }

    private void finalizarDialogoRestroom() {
        Runnable acao = acaoAoFinalDialogo;

        fecharDialogo();

        if (acao != null) {
            acao.run();
        }
    }

    private void fecharDialogo() {
        dialogoAberto = false;
        podeInteragir = true;
        input.interact = false;

        dialogosAtuais = new String[0];
        indiceDialogoAtual = 0;
        acaoAoFinalDialogo = null;
        dialogoPermiteCancelar = true;

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

            EstadoJogo.getInstance().resetarRua();

            StreetsController controller = (StreetsController) App.setRoot("streets");
            controller.startGame(App.getStage().getScene());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
