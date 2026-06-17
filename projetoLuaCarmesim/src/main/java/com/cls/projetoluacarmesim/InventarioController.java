package com.cls.projetoluacarmesim;

import com.cls.projetoluacarmesim.dao.ReceitaDAO;
import com.cls.projetoluacarmesim.util.PocaoService;
import com.cls.projetoluacarmesim.model.ItemEspecial;
import com.cls.projetoluacarmesim.enums.TipoItem;
import com.cls.projetoluacarmesim.model.Jogador;
import com.cls.projetoluacarmesim.model.JogadorFormula;
import com.cls.projetoluacarmesim.model.IngredienteFormula;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
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

    private final PocaoService pocaoService = new PocaoService();

    @FXML
    public void initialize() {
        carregarItens();
        carregarReceitas();

        rootInventario.setFocusTraversable(true);

        rootInventario.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.I) {
                voltarParaTelaAnterior();
                e.consume();
                return;
            }

            if (e.getCode() == KeyCode.ENTER) {
                usarItemSelecionado();
                e.consume();
            }
        });

        Platform.runLater(() -> {
            rootInventario.requestFocus();
            if (listaItens != null) {
                listaItens.setFocusTraversable(true);
            }
            if (listaReceitas != null) {
                listaReceitas.setFocusTraversable(false);
            }
        });
    }

    private void carregarItens() {
        listaItens.getItems().clear();

        int moedas = EstadoJogo.getInstance().getMoedasOuro();
        if (moedas > 0) {
            listaItens.getItems().add("Moeda de Ouro - MOEDA x" + moedas);
        }

        Map<String, Integer> itensAgrupados = EstadoJogo.getInstance()
                .getInventario()
                .getItensAgrupadosParaTela();

        if (itensAgrupados.isEmpty()) {
            if (moedas <= 0) {
                listaItens.getItems().add("Nenhum item obtido.");
            }
            return;
        }

        for (Map.Entry<String, Integer> entrada : itensAgrupados.entrySet()) {
            String nomeItem = entrada.getKey();
            int quantidade = entrada.getValue();

            if (quantidade > 1) {
                listaItens.getItems().add(nomeItem + " x" + quantidade);
            } else {
                listaItens.getItems().add(nomeItem);
            }
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

        ReceitaDAO receitaDAO = new ReceitaDAO();

        for (JogadorFormula receita : receitas) {
            if (receita.getFormula() != null) {

                String caminho = receitaDAO.getCaminhoPorNome(
                        receita.getFormula().getNomePocao()
                );

                listaReceitas.getItems().add(
                        receita.getFormula().getNomePocao()
                                + " | Caminho: "
                                + caminho
                                + " | Sequência "
                                + receita.getFormula().getNivelSequencia()
                );

                for (IngredienteFormula ingrediente : receita.getFormula().getIngredientes()) {
                    listaReceitas.getItems().add(
                            "   - "
                                    + ingrediente.getQuantidade()
                                    + "x "
                                    + ingrediente.getNomeIngrediente()
                    );
                }
            }
        }
    }

    private void usarItemSelecionado() {
        String linhaSelecionada = listaItens.getSelectionModel().getSelectedItem();

        if (linhaSelecionada == null || linhaSelecionada.isBlank()) {
            mostrarAviso("Item", "Selecione um item na lista.");
            return;
        }

        String nomeItem = extrairNomeItemDaLinha(linhaSelecionada);

        if (nomeItem == null) {
            mostrarAviso("Item", "Esse item não pode ser usado.");
            return;
        }

        if (nomeItem.equalsIgnoreCase("Moeda de Ouro")) {
            mostrarAviso("Moeda de Ouro", "Você possui " + EstadoJogo.getInstance().getMoedasOuro() + " moedas de ouro nesta run.");
            return;
        }

        if (nomeItem.equalsIgnoreCase(StreetsController.NOME_ESPELHO_FINAL)) {
            usarEspelhoFinal();
            return;
        }

        if (!nomeItem.startsWith("Poção de ")) {
            mostrarAviso("Item", "Esse item ainda não possui interação especial.");
            return;
        }

        ItemEspecial pocao = EstadoJogo.getInstance()
                .getInventario()
                .buscarPrimeiroItem(nomeItem, TipoItem.POCAO);

        if (pocao == null) {
            mostrarAviso("Poção", "Essa poção não foi encontrada no inventário.");
            return;
        }

        Jogador jogador = EstadoJogo.getInstance().getJogadorAtual();

        try {
            PocaoService.ResultadoPocao resultado = pocaoService.tentarBeberPocao(
                    jogador,
                    pocao,
                    EstadoJogo.getInstance().getInventario()
            );

            if (resultado.isSucesso()) {
                carregarItens();
                carregarReceitas();
            }

            mostrarAviso("Poção", resultado.getMensagem());

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAviso(
                    "Erro no banco",
                    "Não foi possível beber a poção.\n\n" + e.getMessage()
            );
        }
    }


    private void usarEspelhoFinal() {
        boolean removeu = EstadoJogo.getInstance()
                .getInventario()
                .removerPrimeiroItem(StreetsController.NOME_ESPELHO_FINAL, TipoItem.RELIQUIO);

        if (!removeu) {
            mostrarAviso("Espelho", "O espelho não foi encontrado no inventário.");
            return;
        }

        carregarItens();
        mostrarAviso(
                "Espelho da Lua Carmesim",
                "O espelho abre uma sala impossível. Você será levado para a Final Room."
        );

        try {
            Object controller = App.setRoot("Final_room");

            if (controller instanceof Final_roomController) {
                Final_roomController finalRoomController = (Final_roomController) controller;
                finalRoomController.startGame(App.getStage().getScene());
            }
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAviso("Erro", "Não foi possível abrir a Final Room.");
        }
    }

    private String extrairNomeItemDaLinha(String linha) {
        if (linha == null) {
            return null;
        }

        String texto = linha.trim();

        if (texto.equalsIgnoreCase("Nenhum item obtido.")) {
            return null;
        }

        int indiceTipo = texto.indexOf(" - ");

        if (indiceTipo >= 0) {
            return texto.substring(0, indiceTipo).trim();
        }

        int indiceQuantidade = texto.lastIndexOf(" x");

        if (indiceQuantidade >= 0) {
            return texto.substring(0, indiceQuantidade).trim();
        }

        return texto;
    }

    private void mostrarAviso(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void voltarParaTelaAnterior() {
        try {
            String telaAnterior = EstadoJogo.getInstance().getTelaAnteriorInventario();

            Object controller = App.setRoot(telaAnterior);

            if (controller instanceof RestroomController) {
                RestroomController restroomController = (RestroomController) controller;
                restroomController.startGame(App.getStage().getScene());
            }

            if (controller instanceof StreetsController) {
                StreetsController streetsController = (StreetsController) controller;
                streetsController.startGame(App.getStage().getScene());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
