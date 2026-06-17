package com.cls.projetoluacarmesim;

import com.cls.projetoluacarmesim.combate.HabilidadeCatalogo;
import com.cls.projetoluacarmesim.combate.HabilidadeCombate;
import com.cls.projetoluacarmesim.model.Jogador;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;

public class HabilidadesController {

    @FXML
    private BorderPane rootHabilidades;

    @FXML
    private Label labelTitulo;

    @FXML
    private Label labelResumo;

    @FXML
    private ListView<String> listaHabilidades;

    @FXML
    private Label textoDescricao;

    @FXML
    private Button botaoVoltar;

    private final Map<String, HabilidadeCombate> habilidadesPorLinha = new LinkedHashMap<>();

    @FXML
    private void initialize() {
        carregarHabilidades();
        configurarSelecao();
        configurarAtalhos();

        Platform.runLater(() -> {
            if (rootHabilidades != null) {
                rootHabilidades.requestFocus();
            }
            if (listaHabilidades != null) {
                listaHabilidades.requestFocus();
            }
        });
    }

    private void carregarHabilidades() {
        habilidadesPorLinha.clear();
        listaHabilidades.getItems().clear();

        Jogador jogador = EstadoJogo.getInstance().getJogadorAtual();

        if (jogador == null) {
            labelTitulo.setText("Habilidades de Caminho");
            labelResumo.setText("Nenhum jogador carregado.");
            listaHabilidades.getItems().add("Nenhuma habilidade disponível.");
            textoDescricao.setText("Volte ao menu principal e comece o jogo para sincronizar o jogador.");
            return;
        }

        String caminho = HabilidadeCatalogo.normalizarCaminho(jogador.getCaminhoAtual());
        int sequencia = jogador.getSequenciaAtual();

        if (caminho == null || caminho.isBlank() || sequencia > 9) {
            labelTitulo.setText("Habilidades de Caminho");
            labelResumo.setText("Você ainda não despertou um Caminho.");
            listaHabilidades.getItems().add("Beba uma poção de Sequência 9 para desbloquear habilidades.");
            textoDescricao.setText("As habilidades aparecem aqui conforme sua progressão alquímica. Cada Caminho ganha quatro habilidades base e evoluções nas sequências seguintes.");
            return;
        }

        labelTitulo.setText(HabilidadeCatalogo.nomeCaminhoParaExibicao(caminho));
        labelResumo.setText("Sequência atual: " + sequencia + " | Habilidades desbloqueadas abaixo.");

        List<HabilidadeCombate> habilidades = HabilidadeCatalogo.listarPorProgressao(caminho, sequencia);

        if (habilidades.isEmpty()) {
            listaHabilidades.getItems().add("Nenhuma habilidade disponível.");
            textoDescricao.setText("Não encontrei habilidades cadastradas para essa progressão.");
            return;
        }

        for (HabilidadeCombate habilidade : habilidades) {
            String linha = habilidade.getTextoMenu();
            habilidadesPorLinha.put(linha, habilidade);
            listaHabilidades.getItems().add(linha);
        }

        listaHabilidades.getSelectionModel().selectFirst();
        atualizarDescricao(listaHabilidades.getSelectionModel().getSelectedItem());
    }

    private void configurarSelecao() {
        listaHabilidades.getSelectionModel().selectedItemProperty().addListener((obs, antigo, novo) -> {
            atualizarDescricao(novo);
        });
    }

    private void atualizarDescricao(String linhaSelecionada) {
        HabilidadeCombate habilidade = habilidadesPorLinha.get(linhaSelecionada);

        if (habilidade == null) {
            return;
        }

        String tipo = habilidade.isSuprema() ? "Habilidade Suprema" : "Habilidade";
        textoDescricao.setText(
                tipo
                        + "\nNome: " + habilidade.getNome()
                        + "\nCaminho: " + habilidade.getCaminho()
                        + "\nCusto: " + habilidade.getCustoSanidade() + " de Sanidade"
                        + "\n\nEfeito: " + habilidade.getDescricaoCurta()
        );
    }

    private void configurarAtalhos() {
        if (rootHabilidades == null) {
            return;
        }

        rootHabilidades.setFocusTraversable(true);
        rootHabilidades.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.ESCAPE || e.getCode() == KeyCode.TAB) {
                try {
                    voltarParaTelaAnterior();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                e.consume();
            }
        });
    }

    @FXML
    private void voltarParaTelaAnterior() throws IOException {
        String telaAnterior = EstadoJogo.getInstance().getTelaAnteriorHabilidades();

        if (telaAnterior == null || telaAnterior.isBlank()) {
            telaAnterior = "streets";
        }

        Object controller = App.setRoot(telaAnterior);

        if (controller instanceof RestroomController) {
            ((RestroomController) controller).startGame(App.getStage().getScene());
        }

        if (controller instanceof StreetsController) {
            ((StreetsController) controller).startGame(App.getStage().getScene());
        }
    }
}
