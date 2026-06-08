package com.cls.projetoluacarmesim.util;

import com.cls.projetoluacarmesim.dao.ReceitaDAO;
import com.cls.projetoluacarmesim.model.FormulaPocao;
import com.cls.projetoluacarmesim.model.ItemEspecial;
import com.cls.projetoluacarmesim.model.JogadorFormula;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class InventarioManager {

    private final List<ItemEspecial> itens = new ArrayList<>();

    private final ReceitaDAO receitaDAO = new ReceitaDAO();

    public void adicionarItem(ItemEspecial item) {
        if (item == null) {
            return;
        }

        itens.add(item);
    }

    public void removerItem(ItemEspecial item) {
        itens.remove(item);
    }

    public List<ItemEspecial> getItens() {
        return itens;
    }

    public Map<String, Integer> getItensAgrupadosParaTela() {
        Map<String, Integer> agrupados = new LinkedHashMap<>();

        for (ItemEspecial item : itens) {
            String chave = item.getNomeItem() + " - " + item.getTipoItem();

            if (agrupados.containsKey(chave)) {
                agrupados.put(chave, agrupados.get(chave) + 1);
            } else {
                agrupados.put(chave, 1);
            }
        }

        return agrupados;
    }

    public boolean possuiItem(String nomeItem) {
        for (ItemEspecial item : itens) {
            if (item.getNomeItem().equalsIgnoreCase(nomeItem)) {
                return true;
            }
        }

        return false;
    }

    public void limparItensDaRun() {
        itens.clear();
    }

    public void aprenderReceita(int idJogador, int idFormula) {
        try {
            receitaDAO.marcarComoAprendida(idJogador, idFormula);
        } catch (SQLException e) {
            System.out.println("Erro ao aprender receita: " + e.getMessage());
        }
    }

    public List<JogadorFormula> listarReceitasAprendidas(int idJogador) {
        try {
            return receitaDAO.listarAprendidasPorJogador(idJogador);
        } catch (SQLException e) {
            System.out.println("Erro ao listar receitas aprendidas: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public FormulaPocao buscarReceitaPorId(int idFormula) {
        try {
            return receitaDAO.buscarPorId(idFormula);
        } catch (SQLException e) {
            System.out.println("Erro ao buscar receita: " + e.getMessage());
            return null;
        }
    }

    public boolean jaAprendeuReceita(int idJogador, int idFormula) {
        try {
            return receitaDAO.jaAprendeu(idJogador, idFormula);
        } catch (SQLException e) {
            System.out.println("Erro ao verificar receita: " + e.getMessage());
            return false;
        }
    }
}