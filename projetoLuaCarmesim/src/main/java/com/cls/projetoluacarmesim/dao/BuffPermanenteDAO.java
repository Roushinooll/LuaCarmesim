package com.cls.projetoluacarmesim.dao;

import com.cls.projetoluacarmesim.enums.TipoBuff;
import com.cls.projetoluacarmesim.model.BuffPermanente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BuffPermanenteDAO {

    public BuffPermanente adicionar(BuffPermanente buff) throws SQLException {
        String sql = "INSERT INTO buff_permanente (id_jogador, nome_buff, tipo, valor, descricao) "
                   + "VALUES (?, ?, ?::tipo_buff_enum, ?, ?) RETURNING id_buff";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConexaoBanco.getConexao();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, buff.getIdJogador());
            ps.setString(2, buff.getNomeBuff());
            ps.setString(3, buff.getTipo().name().toLowerCase());
            ps.setInt(4, buff.getValor());
            ps.setString(5, buff.getDescricao());

            rs = ps.executeQuery();
            if (rs.next()) buff.setIdBuff(rs.getInt("id_buff"));
            return buff;
        } finally {
            ConexaoBanco.fechar(rs, ps, conn);
        }
    }

    public List<BuffPermanente> listarPorJogador(int idJogador) throws SQLException {
        String sql = "SELECT * FROM buff_permanente WHERE id_jogador = ?";
        List<BuffPermanente> lista = new ArrayList<>();

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

    public List<BuffPermanente> buscarPorTipo(int idJogador, TipoBuff tipo) throws SQLException {
        String sql = "SELECT * FROM buff_permanente WHERE id_jogador = ? AND tipo = ?::tipo_buff_enum";
        List<BuffPermanente> lista = new ArrayList<>();

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

    public void remover(int idBuff) throws SQLException {
        String sql = "DELETE FROM buff_permanente WHERE id_buff = ?";

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = ConexaoBanco.getConexao();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, idBuff);
            ps.executeUpdate();
        } finally {
            ConexaoBanco.fechar(ps, conn);
        }
    }

    private BuffPermanente mapear(ResultSet rs) throws SQLException {
        String tipoStr = rs.getString("tipo").toUpperCase();
        TipoBuff tipo = TipoBuff.valueOf(tipoStr);

        return new BuffPermanente(
            rs.getInt("id_buff"),
            rs.getInt("id_jogador"),
            rs.getString("nome_buff"),
            tipo,
            rs.getInt("valor"),
            rs.getString("descricao")
        );
    }
}
