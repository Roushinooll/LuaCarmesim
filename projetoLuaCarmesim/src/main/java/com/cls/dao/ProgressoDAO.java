package com.cls.projetoluacarmesim.dao;

import com.cls.projetoluacarmesim.enums.StatusRun;
import com.cls.projetoluacarmesim.model.Progresso;

import java.sql.*;

public class ProgressoDAO {

    public Progresso criar(Progresso progresso) throws SQLException {
        String sql = "INSERT INTO progresso (id_jogador, sala_atual, andar_atual, status_run) "
                   + "VALUES (?, ?, ?, ?::status_run_enum) RETURNING id_progresso, atualizado_em";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConexaoBanco.getConexao();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, progresso.getIdJogador());
            ps.setInt(2, progresso.getSalaAtual());
            ps.setInt(3, progresso.getAndarAtual());
            ps.setString(4, progresso.getStatusRun().name().toLowerCase());

            rs = ps.executeQuery();
            if (rs.next()) {
                progresso.setIdProgresso(rs.getInt("id_progresso"));
                progresso.setAtualizadoEm(rs.getTimestamp("atualizado_em").toLocalDateTime());
            }
            return progresso;
        } finally {
            ConexaoBanco.fechar(rs, ps, conn);
        }
    }

    public Progresso buscarPorJogador(int idJogador) throws SQLException {
        String sql = "SELECT * FROM progresso WHERE id_jogador = ? ORDER BY atualizado_em DESC LIMIT 1";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConexaoBanco.getConexao();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, idJogador);
            rs = ps.executeQuery();

            if (rs.next()) return mapear(rs);
            return null;
        } finally {
            ConexaoBanco.fechar(rs, ps, conn);
        }
    }

    public void atualizarSala(int idJogador, int salaAtual, int andarAtual) throws SQLException {
        String sql = "UPDATE progresso SET sala_atual = ?, andar_atual = ? WHERE id_jogador = ?";

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = ConexaoBanco.getConexao();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, salaAtual);
            ps.setInt(2, andarAtual);
            ps.setInt(3, idJogador);
            ps.executeUpdate();
        } finally {
            ConexaoBanco.fechar(ps, conn);
        }
    }

    public void atualizarStatus(int idJogador, StatusRun status) throws SQLException {
        String sql = "UPDATE progresso SET status_run = ?::status_run_enum WHERE id_jogador = ?";

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = ConexaoBanco.getConexao();
            ps = conn.prepareStatement(sql);
            ps.setString(1, status.name().toLowerCase());
            ps.setInt(2, idJogador);
            ps.executeUpdate();
        } finally {
            ConexaoBanco.fechar(ps, conn);
        }
    }

    public void resetarRun(int idJogador) throws SQLException {
        String sql = "UPDATE progresso SET sala_atual = 1, andar_atual = 1, "
                   + "status_run = 'em_andamento'::status_run_enum WHERE id_jogador = ?";

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

    private Progresso mapear(ResultSet rs) throws SQLException {
        String statusStr = rs.getString("status_run").toUpperCase();
        StatusRun status = StatusRun.valueOf(statusStr);

        return new Progresso(
            rs.getInt("id_progresso"),
            rs.getInt("id_jogador"),
            rs.getInt("sala_atual"),
            rs.getInt("andar_atual"),
            status,
            rs.getTimestamp("atualizado_em").toLocalDateTime()
        );
    }
}
