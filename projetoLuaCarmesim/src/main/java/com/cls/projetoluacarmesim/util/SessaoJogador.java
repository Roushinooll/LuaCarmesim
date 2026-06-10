package com.cls.projetoluacarmesim.util;

import com.cls.projetoluacarmesim.EstadoJogo;
import com.cls.projetoluacarmesim.dao.JogadorDAO;
import com.cls.projetoluacarmesim.dao.RankingDAO;
import com.cls.projetoluacarmesim.dao.ReceitaDAO;
import com.cls.projetoluacarmesim.model.FormulaPocao;
import com.cls.projetoluacarmesim.model.Jogador;
import com.cls.projetoluacarmesim.model.Ranking;

import java.sql.SQLException;

public final class SessaoJogador {

    private SessaoJogador() {
    }

    public static Jogador sincronizarJogador(String nome) throws SQLException {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do jogador não pode ficar vazio.");
        }

        String nomeLimpo = nome.trim();

        JogadorDAO jogadorDAO = new JogadorDAO();
        RankingDAO rankingDAO = new RankingDAO();

        Jogador jogador = jogadorDAO.buscarPorNome(nomeLimpo);

        if (jogador == null) {
            jogador = new Jogador(nomeLimpo);
            jogador = jogadorDAO.criar(jogador);
        }

        if (rankingDAO.buscarPorJogador(jogador.getIdJogador()) == null) {
            rankingDAO.criar(new Ranking(jogador.getIdJogador()));
        }

        EstadoJogo.getInstance().setJogadorAtual(jogador);
        aprenderReceitaInicial(jogador);

        return jogador;
    }

    public static void aprenderReceitaInicial(Jogador jogador) {
        if (jogador == null || jogador.getIdJogador() <= 0) {
            return;
        }

        try {
            ReceitaDAO receitaDAO = new ReceitaDAO();
            FormulaPocao receitaInicial = receitaDAO.buscarPorNome("Vidente");

            if (receitaInicial != null) {
                EstadoJogo.getInstance().getInventario().aprenderReceita(
                        jogador.getIdJogador(),
                        receitaInicial.getIdFormula()
                );
                return;
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar receita inicial: " + e.getMessage());
        }

        EstadoJogo.getInstance().getInventario().aprenderReceita(
                jogador.getIdJogador(),
                1
        );
    }
}
