package com.cls.projetoluacarmesim.util;

import com.cls.projetoluacarmesim.dao.ReceitaDAO;
import com.cls.projetoluacarmesim.enums.TipoItem;
import com.cls.projetoluacarmesim.model.IngredienteFormula;
import com.cls.projetoluacarmesim.model.FormulaPocao;
import com.cls.projetoluacarmesim.model.ItemEspecial;
import com.cls.projetoluacarmesim.model.JogadorFormula;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Iterator;

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

    public int contarItem(String nomeItem) {
        int quantidade = 0;

        for (ItemEspecial item : itens) {
            if (item.getNomeItem().equalsIgnoreCase(nomeItem)) {
                quantidade++;
            }
        }

        return quantidade;
    }

    public boolean possuiQuantidade(String nomeItem, int quantidadeNecessaria) {
        return contarItem(nomeItem) >= quantidadeNecessaria;
    }

    public List<String> listarIngredientesFaltantes(FormulaPocao formula) {
        List<String> faltantes = new ArrayList<>();

        if (formula == null) {
            faltantes.add("Receita inválida");
            return faltantes;
        }

        if (formula.getIngredientes() == null) {
            return faltantes;
        }

        for (IngredienteFormula ingrediente : formula.getIngredientes()) {
            int possui = contarItem(ingrediente.getNomeIngrediente());
            int precisa = ingrediente.getQuantidade();

            if (possui < precisa) {
                faltantes.add(ingrediente.getNomeIngrediente() + " (" + possui + "/" + precisa + ")");
            }
        }

        return faltantes;
    }

    public boolean possuiIngredientes(FormulaPocao formula) {
        return formula != null && listarIngredientesFaltantes(formula).isEmpty();
    }

    public boolean consumirIngredientes(FormulaPocao formula) {
        if (formula == null || !possuiIngredientes(formula)) {
            return false;
        }

        for (IngredienteFormula ingrediente : formula.getIngredientes()) {
            removerQuantidadePorNome(
                    ingrediente.getNomeIngrediente(),
                    ingrediente.getQuantidade()
            );
        }

        return true;
    }

    private void removerQuantidadePorNome(String nomeItem, int quantidade) {
        int removidos = 0;
        Iterator<ItemEspecial> iterator = itens.iterator();

        while (iterator.hasNext() && removidos < quantidade) {
            ItemEspecial item = iterator.next();

            if (item.getNomeItem().equalsIgnoreCase(nomeItem)) {
                iterator.remove();
                removidos++;
            }
        }
    }

    public ItemEspecial criarPocao(FormulaPocao formula) {
        if (formula == null) {
            return null;
        }

        ItemEspecial pocao = new ItemEspecial(
                0,
                "Poção de " + formula.getNomePocao(),
                TipoItem.POCAO,
                formula.getEfeitoPrincipal(),
                false
        );

        adicionarItem(pocao);
        return pocao;
    }

    public ItemEspecial buscarPrimeiroItem(String nomeItem, TipoItem tipoItem) {
        for (ItemEspecial item : itens) {
            if (item.getNomeItem().equalsIgnoreCase(nomeItem)
                    && item.getTipoItem() == tipoItem) {
                return item;
            }
        }

        return null;
    }

    public boolean removerPrimeiroItem(String nomeItem, TipoItem tipoItem) {
        Iterator<ItemEspecial> iterator = itens.iterator();

        while (iterator.hasNext()) {
            ItemEspecial item = iterator.next();

            if (item.getNomeItem().equalsIgnoreCase(nomeItem)
                    && item.getTipoItem() == tipoItem) {
                iterator.remove();
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