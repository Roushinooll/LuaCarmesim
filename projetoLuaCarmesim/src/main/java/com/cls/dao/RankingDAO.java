package com.cls.projetoluacarmesim.dao;

import com.cls.projetoluacarmesim.model.Ranking;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RankingDAO {

    public Ranking criar(Ranking ranking) throws SQLException {
        String sql = "INSERT INTO ranking (id_jogador, total_salas_zeradas, melhor_sequencia) "
                   + "VALUES (?, ?, ?) RETURNING id_ranking, data_recorde";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConexaoBanco.getConexao();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, ranking.getIdJogador());
            ps.setInt(2, ranking.getTotalSalasZeradas());
            ps.setInt(3, ranking.getMelhorSequencia());

            rs = ps.executeQuery();
            if (rs.next()) {
                ranking.setIdRanking(rs.getInt("id_ranking"));
                ranking.setDataRecorde(rs.getTimestamp("data_recorde").toLocalDateTime());
            }
            return ranking;
        } finally {
            ConexaoBanco.fechar(rs, ps, conn);
        }
    }

    public Ranking buscarPorJogador(int idJogador) throws SQLException {
        String sql = "SELECT * FROM ranking WHERE id_jogador = ?";

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

    public List<Ranking> listarTop10() throws SQLException {
        String sql = "SELECT * FROM ranking ORDER BY total_salas_zeradas DESC, melhor_sequencia ASC LIMIT 10";
        List<Ranking> lista = new ArrayList<>();

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConexaoBanco.getConexao();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) lista.add(mapear(rs));
            return lista;
        } finally {
            ConexaoBanco.fechar(rs, ps, conn);
        }
    }

    public void atualizarRecorde(int idJogador, int totalSalasZeradas, int melhorSequencia) throws SQLException {
        String sql = "UPDATE ranking SET total_salas_zeradas = ?, melhor_sequencia = ?, "
                   + "data_recorde = CURRENT_TIMESTAMP WHERE id_jogador = ?";

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = ConexaoBanco.getConexao();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, totalSalasZeradas);
            ps.setInt(2, melhorSequencia);
            ps.setInt(3, idJogador);
            ps.executeUpdate();
        } finally {
            ConexaoBanco.fechar(ps, conn);
        }
    }

    private Ranking mapear(ResultSet rs) throws SQLException {
        return new Ranking(
            rs.getInt("id_ranking"),
            rs.getInt("id_jogador"),
            rs.getInt("total_salas_zeradas"),
            rs.getInt("melhor_sequencia"),
            rs.getTimestamp("data_recorde").toLocalDateTime()
        );
    }
}
