package com.cls.projetoluacarmesim.util;

import com.cls.projetoluacarmesim.dao.JogadorDAO;
import com.cls.projetoluacarmesim.dao.ReceitaDAO;
import com.cls.projetoluacarmesim.enums.TipoItem;
import com.cls.projetoluacarmesim.model.FormulaPocao;
import com.cls.projetoluacarmesim.model.ItemEspecial;
import com.cls.projetoluacarmesim.model.Jogador;

import java.sql.SQLException;

public class PocaoService {

    private final ReceitaDAO receitaDAO = new ReceitaDAO();
    private final JogadorDAO jogadorDAO = new JogadorDAO();

    public ResultadoPocao tentarBeberPocao(
            Jogador jogador,
            ItemEspecial pocao,
            InventarioManager inventario
    ) throws SQLException {

        if (jogador == null || jogador.getIdJogador() <= 0) {
            return ResultadoPocao.erro("Nenhum jogador carregado.");
        }

        if (pocao == null || pocao.getTipoItem() != TipoItem.POCAO) {
            return ResultadoPocao.erro("Esse item não é uma poção.");
        }

        String nomeFormula = extrairNomeFormulaDaPocao(pocao.getNomeItem());

        if (nomeFormula == null || nomeFormula.isBlank()) {
            return ResultadoPocao.erro("Não foi possível identificar a fórmula dessa poção.");
        }

        FormulaPocao formula = receitaDAO.buscarPorNome(nomeFormula);

        if (formula == null) {
            return ResultadoPocao.erro("Receita da poção não encontrada no banco.");
        }

        String caminhoPocao = receitaDAO.getCaminhoPorNome(formula.getNomePocao());

        if (caminhoPocao == null || caminhoPocao.equals("Desconhecido")) {
            return ResultadoPocao.erro("Caminho da poção desconhecido.");
        }

        int sequenciaAtual = jogador.getSequenciaAtual();
        int sequenciaPocao = formula.getNivelSequencia();

        if (sequenciaAtual <= 5) {
            return ResultadoPocao.erro("Você já atingiu a menor sequência disponível no jogo.");
        }

        String caminhoAtual = jogador.getCaminhoAtual();

        int novaSequencia;
        String novoCaminho;

        if (sequenciaAtual == 10) {
            if (sequenciaPocao != 9) {
                return ResultadoPocao.erro(
                        "Sua primeira poção precisa ser de Sequência 9 para escolher um caminho."
                );
            }

            novaSequencia = 9;
            novoCaminho = caminhoPocao;

        } else {
            if (caminhoAtual == null || caminhoAtual.isBlank()) {
                return ResultadoPocao.erro(
                        "Seu jogador já tem sequência, mas não tem caminho salvo. Rode a migração do banco ou resete esse jogador."
                );
            }

            if (!caminhoAtual.equals(caminhoPocao)) {
                return ResultadoPocao.erro(
                        "Você já escolheu o caminho "
                                + caminhoAtual
                                + ". Não pode beber poções do caminho "
                                + caminhoPocao
                                + "."
                );
            }

            int sequenciaEsperada = sequenciaAtual - 1;

            if (sequenciaPocao != sequenciaEsperada) {
                return ResultadoPocao.erro(
                        "Ordem incorreta. Sua próxima poção precisa ser de Sequência "
                                + sequenciaEsperada
                                + "."
                );
            }

            novaSequencia = sequenciaEsperada;
            novoCaminho = caminhoAtual;
        }

        boolean removeu = inventario.removerPrimeiroItem(
                pocao.getNomeItem(),
                TipoItem.POCAO
        );

        if (!removeu) {
            return ResultadoPocao.erro("Não foi possível remover a poção do inventário.");
        }

        jogadorDAO.atualizarProgressaoPocao(
                jogador.getIdJogador(),
                novaSequencia,
                novoCaminho
        );

        jogador.setSequenciaAtual(novaSequencia);
        jogador.setCaminhoAtual(novoCaminho);

        return ResultadoPocao.sucesso(
                "Você bebeu "
                        + pocao.getNomeItem()
                        + ".\nCaminho: "
                        + novoCaminho
                        + "\nSequência atual: "
                        + novaSequencia
        );
    }

    private String extrairNomeFormulaDaPocao(String nomeItem) {
        if (nomeItem == null) {
            return null;
        }

        String prefixo = "Poção de ";

        if (!nomeItem.startsWith(prefixo)) {
            return null;
        }

        return nomeItem.substring(prefixo.length()).trim();
    }

    public static class ResultadoPocao {

        private final boolean sucesso;
        private final String mensagem;

        private ResultadoPocao(boolean sucesso, String mensagem) {
            this.sucesso = sucesso;
            this.mensagem = mensagem;
        }

        public static ResultadoPocao sucesso(String mensagem) {
            return new ResultadoPocao(true, mensagem);
        }

        public static ResultadoPocao erro(String mensagem) {
            return new ResultadoPocao(false, mensagem);
        }

        public boolean isSucesso() {
            return sucesso;
        }

        public String getMensagem() {
            return mensagem;
        }
    }
}
