package com.cls.projetoluacarmesim.dao;

import com.cls.projetoluacarmesim.model.ProgressoNarrativo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProgressoNarrativoDAO {

    public ProgressoNarrativo registrar(ProgressoNarrativo progresso) throws SQLException {
        String sql = "INSERT INTO progresso_narrativo (id_jogador, chave_evento) "
                   + "VALUES (?, ?) RETURNING id_progresso_narrativo, descoberto_em";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConexaoBanco.getConexao();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, progresso.getIdJogador());
            ps.setString(2, progresso.getChaveEvento());

            rs = ps.executeQuery();
            if (rs.next()) {
                progresso.setIdProgressoNarrativo(rs.getInt("id_progresso_narrativo"));
                progresso.setDescobertaEm(rs.getTimestamp("descoberto_em").toLocalDateTime());
            }
            return progresso;
        } finally {
            ConexaoBanco.fechar(rs, ps, conn);
        }
    }

    public boolean jaDescobriu(int idJogador, String chaveEvento) throws SQLException {
        String sql = "SELECT 1 FROM progresso_narrativo WHERE id_jogador = ? AND chave_evento = ?";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConexaoBanco.getConexao();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, idJogador);
            ps.setString(2, chaveEvento);
            rs = ps.executeQuery();
            return rs.next();
        } finally {
            ConexaoBanco.fechar(rs, ps, conn);
        }
    }

    public List<ProgressoNarrativo> listarPorJogador(int idJogador) throws SQLException {
        String sql = "SELECT * FROM progresso_narrativo WHERE id_jogador = ? ORDER BY descoberto_em ASC";
        List<ProgressoNarrativo> lista = new ArrayList<>();

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

    public void removerTodosPorJogador(int idJogador) throws SQLException {
        String sql = "DELETE FROM progresso_narrativo WHERE id_jogador = ?";

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

    private ProgressoNarrativo mapear(ResultSet rs) throws SQLException {
        return new ProgressoNarrativo(
            rs.getInt("id_progresso_narrativo"),
            rs.getInt("id_jogador"),
            rs.getString("chave_evento"),
            rs.getTimestamp("descoberto_em").toLocalDateTime()
        );
    }
}
