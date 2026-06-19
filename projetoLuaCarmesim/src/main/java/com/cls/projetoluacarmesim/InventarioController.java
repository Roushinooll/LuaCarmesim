package com.cls.projetoluacarmesim;

import com.cls.projetoluacarmesim.dao.ReceitaDAO;
import com.cls.projetoluacarmesim.enums.TipoItem;
import com.cls.projetoluacarmesim.model.IngredienteFormula;
import com.cls.projetoluacarmesim.model.ItemEspecial;
import com.cls.projetoluacarmesim.model.Jogador;
import com.cls.projetoluacarmesim.model.JogadorFormula;
import com.cls.projetoluacarmesim.util.PocaoService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

public class InventarioController {

    private enum AbaInventario {
        TUDO,
        INGREDIENTES,
        POCOES,
        RECEITAS
    }

    @FXML
    private VBox rootInventario;

    @FXML
    private Label labelMoedas;

    @FXML
    private Label labelTituloAba;

    @FXML
    private Label labelInstrucaoUso;

    @FXML
    private Button btnTudo;

    @FXML
    private Button btnIngredientes;

    @FXML
    private Button btnPocoes;

    @FXML
    private Button btnReceitas;

    @FXML
    private ListView<String> listaItens;

    private final PocaoService pocaoService = new PocaoService();

    private AbaInventario abaAtual = AbaInventario.TUDO;

    @FXML
    public void initialize() {
        abaAtual = AbaInventario.TUDO;
        carregarAbaAtual();

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
                return;
            }

            if (e.getCode() == KeyCode.DIGIT1 || e.getCode() == KeyCode.NUMPAD1) {
                abrirAba(AbaInventario.TUDO);
                e.consume();
                return;
            }

            if (e.getCode() == KeyCode.DIGIT2 || e.getCode() == KeyCode.NUMPAD2) {
                abrirAba(AbaInventario.INGREDIENTES);
                e.consume();
                return;
            }

            if (e.getCode() == KeyCode.DIGIT3 || e.getCode() == KeyCode.NUMPAD3) {
                abrirAba(AbaInventario.POCOES);
                e.consume();
                return;
            }

            if (e.getCode() == KeyCode.DIGIT4 || e.getCode() == KeyCode.NUMPAD4) {
                abrirAba(AbaInventario.RECEITAS);
                e.consume();
            }
        });

        Platform.runLater(() -> {
            rootInventario.requestFocus();
            if (listaItens != null) {
                listaItens.setFocusTraversable(true);
                listaItens.requestFocus();
            }
        });
    }

    @FXML
    private void mostrarTudo() {
        abrirAba(AbaInventario.TUDO);
    }

    @FXML
    private void mostrarIngredientes() {
        abrirAba(AbaInventario.INGREDIENTES);
    }

    @FXML
    private void mostrarPocoes() {
        abrirAba(AbaInventario.POCOES);
    }

    @FXML
    private void mostrarReceitas() {
        abrirAba(AbaInventario.RECEITAS);
    }

    private void abrirAba(AbaInventario aba) {
        abaAtual = aba;
        carregarAbaAtual();

        Platform.runLater(() -> {
            if (listaItens != null) {
                listaItens.requestFocus();
            }
        });
    }

    private void carregarAbaAtual() {
        atualizarMoedas();
        atualizarBotoesAbas();

        if (listaItens == null) {
            return;
        }

        listaItens.getItems().clear();

        switch (abaAtual) {
            case TUDO:
                carregarTudo();
                break;
            case INGREDIENTES:
                carregarItensPorTipo(TipoItem.INGREDIENTE, "Nenhum ingrediente obtido.");
                break;
            case POCOES:
                carregarItensPorTipo(TipoItem.POCAO, "Nenhuma poção feita.");
                break;
            case RECEITAS:
                carregarReceitas();
                break;
            default:
                carregarTudo();
                break;
        }

        if (!listaItens.getItems().isEmpty()) {
            listaItens.getSelectionModel().selectFirst();
        }
    }

    private void atualizarMoedas() {
        if (labelMoedas != null) {
            labelMoedas.setText("Moedas de Ouro: " + EstadoJogo.getInstance().getMoedasOuro());
        }
    }

    private void atualizarBotoesAbas() {
        atualizarBotaoAba(btnTudo, abaAtual == AbaInventario.TUDO);
        atualizarBotaoAba(btnIngredientes, abaAtual == AbaInventario.INGREDIENTES);
        atualizarBotaoAba(btnPocoes, abaAtual == AbaInventario.POCOES);
        atualizarBotaoAba(btnReceitas, abaAtual == AbaInventario.RECEITAS);

        if (labelTituloAba != null) {
            switch (abaAtual) {
                case TUDO:
                    labelTituloAba.setText("Tudo");
                    break;
                case INGREDIENTES:
                    labelTituloAba.setText("Ingredientes");
                    break;
                case POCOES:
                    labelTituloAba.setText("Poções feitas");
                    break;
                case RECEITAS:
                    labelTituloAba.setText("Receitas obtidas");
                    break;
                default:
                    labelTituloAba.setText("Inventário");
                    break;
            }
        }

        if (labelInstrucaoUso != null) {
            if (abaAtual == AbaInventario.RECEITAS) {
                labelInstrucaoUso.setText("Receitas são permanentes e servem como consulta para a alquimia.");
            } else if (abaAtual == AbaInventario.INGREDIENTES) {
                labelInstrucaoUso.setText("Ingredientes são usados na bancada de alquimia.");
            } else {
                labelInstrucaoUso.setText("ENTER - Usar item selecionado quando ele possuir interação.");
            }
        }
    }

    private void atualizarBotaoAba(Button botao, boolean ativo) {
        if (botao == null) {
            return;
        }

        if (ativo) {
            botao.setStyle(estiloBotaoAbaAtiva());
        } else {
            botao.setStyle(estiloBotaoAbaInativa());
        }
    }

    private String estiloBotaoAbaAtiva() {
        return "-fx-background-color: rgba(201, 107, 107, 0.95);"
                + " -fx-text-fill: #12080b;"
                + " -fx-font-size: 17px;"
                + " -fx-font-weight: bold;"
                + " -fx-background-radius: 10;"
                + " -fx-border-color: #f5e6d0;"
                + " -fx-border-radius: 10;"
                + " -fx-border-width: 1;"
                + " -fx-padding: 10 18 10 18;";
    }

    private String estiloBotaoAbaInativa() {
        return "-fx-background-color: rgba(245, 230, 208, 0.10);"
                + " -fx-text-fill: #f5e6d0;"
                + " -fx-font-size: 17px;"
                + " -fx-font-weight: bold;"
                + " -fx-background-radius: 10;"
                + " -fx-border-color: rgba(245, 230, 208, 0.28);"
                + " -fx-border-radius: 10;"
                + " -fx-border-width: 1;"
                + " -fx-padding: 10 18 10 18;";
    }

    private void carregarTudo() {
        int moedas = EstadoJogo.getInstance().getMoedasOuro();
        if (moedas > 0) {
            listaItens.getItems().add("Moeda de Ouro - MOEDA x" + moedas);
        }

        Map<String, Integer> itensAgrupados = agruparItensPorTipo(null);

        if (itensAgrupados.isEmpty()) {
            if (moedas <= 0) {
                listaItens.getItems().add("Nenhum item obtido.");
            }
            return;
        }

        adicionarItensAgrupadosNaLista(itensAgrupados);
    }

    private void carregarItensPorTipo(TipoItem tipo, String mensagemVazia) {
        Map<String, Integer> itensAgrupados = agruparItensPorTipo(tipo);

        if (itensAgrupados.isEmpty()) {
            listaItens.getItems().add(mensagemVazia);
            return;
        }

        adicionarItensAgrupadosNaLista(itensAgrupados);
    }

    private Map<String, Integer> agruparItensPorTipo(TipoItem tipo) {
        Map<String, Integer> agrupados = new LinkedHashMap<>();

        List<ItemEspecial> itens = EstadoJogo.getInstance()
                .getInventario()
                .getItens();

        for (ItemEspecial item : itens) {
            if (item == null || item.getTipoItem() == TipoItem.MOEDA) {
                continue;
            }

            if (tipo != null && item.getTipoItem() != tipo) {
                continue;
            }

            String chave = item.getNomeItem() + " - " + formatarTipoItem(item.getTipoItem());
            agrupados.put(chave, agrupados.getOrDefault(chave, 0) + 1);
        }

        return agrupados;
    }

    private void adicionarItensAgrupadosNaLista(Map<String, Integer> itensAgrupados) {
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

    private String formatarTipoItem(TipoItem tipoItem) {
        if (tipoItem == null) {
            return "ITEM";
        }

        switch (tipoItem) {
            case POCAO:
                return "POÇÃO";
            case INGREDIENTE:
                return "INGREDIENTE";
            case RELIQUIO:
                return "RELÍQUIA";
            case REVOLVER:
                return "REVÓLVER";
            case LAMINA:
                return "LÂMINA";
            case TALISMO:
                return "TALISMÃ";
            case MOEDA:
                return "MOEDA";
            default:
                return tipoItem.name();
        }
    }

    private void carregarReceitas() {
        Jogador jogador = EstadoJogo.getInstance().getJogadorAtual();

        if (jogador == null || jogador.getIdJogador() <= 0) {
            listaItens.getItems().add("Nenhum jogador carregado.");
            return;
        }

        List<JogadorFormula> receitas = EstadoJogo.getInstance()
                .getInventario()
                .listarReceitasAprendidas(jogador.getIdJogador());

        if (receitas.isEmpty()) {
            listaItens.getItems().add("Nenhuma receita obtida.");
            return;
        }

        ReceitaDAO receitaDAO = new ReceitaDAO();

        for (JogadorFormula receita : receitas) {
            if (receita.getFormula() != null) {

                String caminho = receitaDAO.getCaminhoPorNome(
                        receita.getFormula().getNomePocao()
                );

                listaItens.getItems().add(
                        "Receita: "
                                + receita.getFormula().getNomePocao()
                                + " | Caminho: "
                                + caminho
                                + " | Sequência "
                                + receita.getFormula().getNivelSequencia()
                );

                for (IngredienteFormula ingrediente : receita.getFormula().getIngredientes()) {
                    listaItens.getItems().add(
                            "   - "
                                    + ingrediente.getQuantidade()
                                    + "x "
                                    + ingrediente.getNomeIngrediente()
                    );
                }

                listaItens.getItems().add("");
            }
        }
    }

    private void usarItemSelecionado() {
        if (abaAtual == AbaInventario.RECEITAS) {
            mostrarAviso("Receitas", "Receitas são permanentes e servem para consulta ou criação de poções na bancada de alquimia.");
            return;
        }

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
            if (abaAtual == AbaInventario.INGREDIENTES) {
                mostrarAviso("Ingrediente", "Esse ingrediente será usado na bancada de alquimia.");
            } else {
                mostrarAviso("Item", "Esse item ainda não possui interação especial.");
            }
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
                carregarAbaAtual();
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

        carregarAbaAtual();
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

        if (texto.isBlank()
                || texto.startsWith("Nenhum")
                || texto.startsWith("Nenhuma")
                || texto.startsWith("Receita:")
                || texto.startsWith("-")) {
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
