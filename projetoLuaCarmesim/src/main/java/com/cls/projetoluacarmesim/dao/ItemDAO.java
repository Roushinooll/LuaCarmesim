package com.cls.projetoluacarmesim.dao;

import com.cls.projetoluacarmesim.enums.TipoItem;
import com.cls.projetoluacarmesim.model.ItemEspecial;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ItemDAO {

    public ItemEspecial adicionar(ItemEspecial item) throws SQLException {
        String sql = "INSERT INTO item_especial (id_jogador, nome_item, tipo_item, efeito, permanente) "
                   + "VALUES (?, ?, ?::tipo_item_enum, ?, ?) RETURNING id_item";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConexaoBanco.getConexao();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, item.getIdJogador());
            ps.setString(2, item.getNomeItem());
            ps.setString(3, item.getTipoItem().name().toLowerCase());
            ps.setString(4, item.getEfeito());
            ps.setBoolean(5, item.isPermanente());

            rs = ps.executeQuery();
            if (rs.next()) item.setIdItem(rs.getInt("id_item"));
            return item;
        } finally {
            ConexaoBanco.fechar(rs, ps, conn);
        }
    }

    public List<ItemEspecial> listarPorJogador(int idJogador) throws SQLException {
        String sql = "SELECT * FROM item_especial WHERE id_jogador = ?";
        List<ItemEspecial> lista = new ArrayList<>();

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConexaoBanco.getConexao();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, idJogador);
            rs = ps.executeQuery();

            while (rs.next()) lista.add(mapear(rs));
            return lista;
        } finally {
            ConexaoBanco.fechar(rs, ps, conn);
        }
    }

    public List<ItemEspecial> buscarPorTipo(int idJogador, TipoItem tipo) throws SQLException {
        String sql = "SELECT * FROM item_especial WHERE id_jogador = ? AND tipo_item = ?::tipo_item_enum";
        List<ItemEspecial> lista = new ArrayList<>();

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConexaoBanco.getConexao();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, idJogador);
            ps.setString(2, tipo.name().toLowerCase());
            rs = ps.executeQuery();

            while (rs.next()) lista.add(mapear(rs));
            return lista;
        } finally {
            ConexaoBanco.fechar(rs, ps, conn);
        }
    }

    public void remover(int idItem) throws SQLException {
        String sql = "DELETE FROM item_especial WHERE id_item = ?";

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = ConexaoBanco.getConexao();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, idItem);
            ps.executeUpdate();
        } finally {
            ConexaoBanco.fechar(ps, conn);
        }
    }

    public void removerItensDaRun(int idJogador) throws SQLException {
        String sql = "DELETE FROM item_especial WHERE id_jogador = ? AND permanente = false";

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = ConexaoBanco.getConexao();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, idJogador);
            ps.executeUpdate();
        } finally {
            ConexaoBanco.fechar(ps, conn);
        }
    }

    private ItemEspecial mapear(ResultSet rs) throws SQLException {
        String tipoStr = rs.getString("tipo_item").toUpperCase();
        TipoItem tipo = TipoItem.valueOf(tipoStr);

        return new ItemEspecial(
            rs.getInt("id_item"),
            rs.getInt("id_jogador"),
            rs.getString("nome_item"),
            tipo,
            rs.getString("efeito"),
            rs.getBoolean("permanente")
        );
    }
}
