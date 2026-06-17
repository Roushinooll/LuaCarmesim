package com.cls.projetoluacarmesim;

import com.cls.projetoluacarmesim.dao.ReceitaDAO;
import com.cls.projetoluacarmesim.model.FormulaPocao;
import com.cls.projetoluacarmesim.model.Jogador;
import com.cls.projetoluacarmesim.util.CatalogoItens;
import com.cls.projetoluacarmesim.util.CatalogoItens.EntradaItem;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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

public class MercadoController {

    @FXML private VBox rootMercado;
    @FXML private Label labelTitulo;
    @FXML private Label labelMoedas;
    @FXML private Label labelMensagem;
    @FXML private ListView<String> listaIngredientes;
    @FXML private ListView<String> listaReceitas;
    @FXML private Button botaoComprarIngrediente;
    @FXML private Button botaoComprarReceita;

    private final Map<String, EntradaItem> ofertasIngredientes = new HashMap<>();
    private final Map<String, FormulaPocao> ofertasReceitas = new HashMap<>();
    private final ReceitaDAO receitaDAO = new ReceitaDAO();

    @FXML
    public void initialize() {
        carregarMercado();

        rootMercado.setFocusTraversable(true);
        rootMercado.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.ESCAPE || e.getCode() == KeyCode.TAB) {
                voltarParaRua();
                e.consume();
            }
        });

        Platform.runLater(() -> {
            rootMercado.requestFocus();
            if (listaIngredientes != null) {
                listaIngredientes.setFocusTraversable(true);
            }
        });
    }

    private void carregarMercado() {
        int numeroRua = EstadoJogo.getInstance().getNumeroRuaAtual();

        if (labelTitulo != null) {
            labelTitulo.setText("Mercador da Rua " + numeroRua);
        }

        atualizarMoedas();
        carregarOfertasIngredientes(numeroRua);
        carregarOfertasReceitas(numeroRua);
    }

    private void carregarOfertasIngredientes(int numeroRua) {
        ofertasIngredientes.clear();
        listaIngredientes.getItems().clear();

        List<EntradaItem> itens = CatalogoItens.listarParaMercador(numeroRua, 7);

        for (EntradaItem item : itens) {
            int preco = precoIngrediente(item, numeroRua);
            String linha = item.getNome()
                    + " | "
                    + CatalogoItens.nomeBonitoRaridade(item.getRaridade())
                    + " | "
                    + preco
                    + " moedas";

            ofertasIngredientes.put(linha, item);
            listaIngredientes.getItems().add(linha);
        }

        if (listaIngredientes.getItems().isEmpty()) {
            listaIngredientes.getItems().add("Nenhum ingrediente disponível.");
        }
    }

    private void carregarOfertasReceitas(int numeroRua) {
        ofertasReceitas.clear();
        listaReceitas.getItems().clear();

        Jogador jogador = EstadoJogo.getInstance().getJogadorAtual();
        if (jogador == null || jogador.getIdJogador() <= 0) {
            listaReceitas.getItems().add("Nenhum jogador carregado.");
            return;
        }

        List<FormulaPocao> disponiveis = new ArrayList<>();

        try {
            for (int nivel : niveisReceitaPermitidos(numeroRua)) {
                disponiveis.addAll(receitaDAO.listarDisponiveisParaCompra(jogador.getIdJogador(), nivel));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            listaReceitas.getItems().add("Erro ao carregar receitas.");
            return;
        }

        Collections.shuffle(disponiveis);

        int limite = Math.min(4, disponiveis.size());
        for (int i = 0; i < limite; i++) {
            FormulaPocao formula = disponiveis.get(i);
            int preco = precoReceita(formula);
            String caminho = receitaDAO.getCaminhoPorNome(formula.getNomePocao());
            String linha = formula.getNomePocao()
                    + " | Caminho: "
                    + caminho
                    + " | Sequência "
                    + formula.getNivelSequencia()
                    + " | "
                    + preco
                    + " moedas";

            ofertasReceitas.put(linha, formula);
            listaReceitas.getItems().add(linha);
        }

        if (listaReceitas.getItems().isEmpty()) {
            listaReceitas.getItems().add("Nenhuma receita nova disponível agora.");
        }
    }

    private int[] niveisReceitaPermitidos(int numeroRua) {
        if (numeroRua >= 50) {
            return new int[]{9, 8, 7, 6, 5};
        }
        if (numeroRua >= 40) {
            return new int[]{9, 8, 7, 6};
        }
        if (numeroRua >= 30) {
            return new int[]{9, 8, 7};
        }
        if (numeroRua >= 20) {
            return new int[]{9, 8};
        }
        return new int[]{9};
    }

    private int precoIngrediente(EntradaItem item, int numeroRua) {
        int acrescimoProfundidade = Math.max(0, numeroRua / 20);
        return item.getPrecoBase() + acrescimoProfundidade;
    }

    private int precoReceita(FormulaPocao formula) {
        switch (formula.getNivelSequencia()) {
            case 9:
                return 18;
            case 8:
                return 28;
            case 7:
                return 42;
            case 6:
                return 60;
            case 5:
                return 85;
            default:
                return 20;
        }
    }

    @FXML
    private void comprarIngredienteSelecionado() {
        String linha = listaIngredientes.getSelectionModel().getSelectedItem();
        EntradaItem item = ofertasIngredientes.get(linha);

        if (item == null) {
            mostrarMensagem("Selecione um ingrediente válido.");
            return;
        }

        int preco = precoIngrediente(item, EstadoJogo.getInstance().getNumeroRuaAtual());
        if (!EstadoJogo.getInstance().gastarMoedasOuro(preco)) {
            mostrarMensagem("Moedas insuficientes para comprar esse ingrediente.");
            return;
        }

        EstadoJogo.getInstance().getInventario().adicionarItem(item.criarItem());
        atualizarMoedas();
        mostrarMensagem("Comprado: " + item.getNome() + ".");
    }

    @FXML
    private void comprarReceitaSelecionada() {
        String linha = listaReceitas.getSelectionModel().getSelectedItem();
        FormulaPocao formula = ofertasReceitas.get(linha);

        if (formula == null) {
            mostrarMensagem("Selecione uma receita válida.");
            return;
        }

        Jogador jogador = EstadoJogo.getInstance().getJogadorAtual();
        if (jogador == null || jogador.getIdJogador() <= 0) {
            mostrarMensagem("Nenhum jogador carregado.");
            return;
        }

        int preco = precoReceita(formula);
        if (!EstadoJogo.getInstance().gastarMoedasOuro(preco)) {
            mostrarMensagem("Moedas insuficientes para comprar essa receita.");
            return;
        }

        try {
            receitaDAO.marcarComoAprendida(jogador.getIdJogador(), formula.getIdFormula());
        } catch (SQLException e) {
            EstadoJogo.getInstance().adicionarMoedasOuro(preco);
            e.printStackTrace();
            mostrarMensagem("Erro ao aprender a receita.");
            atualizarMoedas();
            return;
        }

        ofertasReceitas.remove(linha);
        listaReceitas.getItems().remove(linha);
        if (listaReceitas.getItems().isEmpty()) {
            listaReceitas.getItems().add("Nenhuma receita nova disponível agora.");
        }

        atualizarMoedas();
        mostrarMensagem("Receita aprendida permanentemente: " + formula.getNomePocao() + ".");
    }

    private void atualizarMoedas() {
        if (labelMoedas != null) {
            labelMoedas.setText("Moedas de Ouro: " + EstadoJogo.getInstance().getMoedasOuro());
        }
    }

    private void mostrarMensagem(String mensagem) {
        if (labelMensagem != null) {
            labelMensagem.setText(mensagem);
            return;
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Mercador");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    @FXML
    private void voltarParaRua() {
        try {
            Object controller = App.setRoot("streets");
            if (controller instanceof StreetsController) {
                ((StreetsController) controller).startGame(App.getStage().getScene());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
