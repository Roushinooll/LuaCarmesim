package com.cls.projetoluacarmesim.dao;

import com.cls.projetoluacarmesim.model.Jogador;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JogadorDAO {

    public Jogador criar(Jogador jogador) throws SQLException {
        String sql = "INSERT INTO jogador (nome, sequencia_atual, sanidade_maxima, sanidade_atual) "
                   + "VALUES (?, ?, ?, ?) RETURNING id_jogador, criado_em";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConexaoBanco.getConexao();
            ps = conn.prepareStatement(sql);
            ps.setString(1, jogador.getNome());
            ps.setInt(2, jogador.getSequenciaAtual());
            ps.setInt(3, jogador.getSanidadeMaxima());
            ps.setInt(4, jogador.getSanidadeAtual());

            rs = ps.executeQuery();
            if (rs.next()) {
                jogador.setIdJogador(rs.getInt("id_jogador"));
                jogador.setCriadoEm(rs.getTimestamp("criado_em").toLocalDateTime());
            }
            return jogador;
        } finally {
            ConexaoBanco.fechar(rs, ps, conn);
        }
    }

    public Jogador buscarPorId(int idJogador) throws SQLException {
        String sql = "SELECT * FROM jogador WHERE id_jogador = ?";

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

    public Jogador buscarPorNome(String nome) throws SQLException {
        String sql = "SELECT * FROM jogador WHERE nome = ?";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConexaoBanco.getConexao();
            ps = conn.prepareStatement(sql);
            ps.setString(1, nome);
            rs = ps.executeQuery();

            if (rs.next()) return mapear(rs);
            return null;
        } finally {
            ConexaoBanco.fechar(rs, ps, conn);
        }
    }

    public List<Jogador> listarTodos() throws SQLException {
        String sql = "SELECT * FROM jogador ORDER BY criado_em DESC";
        List<Jogador> lista = new ArrayList<>();

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

    public void atualizarSanidade(int idJogador, int sanidadeAtual) throws SQLException {
        String sql = "UPDATE jogador SET sanidade_atual = ? WHERE id_jogador = ?";

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = ConexaoBanco.getConexao();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, sanidadeAtual);
            ps.setInt(2, idJogador);
            ps.executeUpdate();
        } finally {
            ConexaoBanco.fechar(ps, conn);
        }
    }

    public void atualizarSequencia(int idJogador, int sequenciaAtual) throws SQLException {
        String sql = "UPDATE jogador SET sequencia_atual = ? WHERE id_jogador = ?";

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = ConexaoBanco.getConexao();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, sequenciaAtual);
            ps.setInt(2, idJogador);
            ps.executeUpdate();
        } finally {
            ConexaoBanco.fechar(ps, conn);
        }
    }

    public void atualizarProgressaoPocao(int idJogador, int sequenciaAtual, String caminhoAtual) throws SQLException {
        String sql = "UPDATE jogador SET sequencia_atual = ?, caminho_atual = ? WHERE id_jogador = ?";

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = ConexaoBanco.getConexao();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, sequenciaAtual);
            ps.setString(2, caminhoAtual);
            ps.setInt(3, idJogador);
            ps.executeUpdate();
        } finally {
            ConexaoBanco.fechar(ps, conn);
        }
    }

    public void deletar(int idJogador) throws SQLException {
        String sql = "DELETE FROM jogador WHERE id_jogador = ?";

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

    private Jogador mapear(ResultSet rs) throws SQLException {
        Jogador jogador = new Jogador(
                rs.getInt("id_jogador"),
                rs.getString("nome"),
                rs.getInt("sequencia_atual"),
                rs.getInt("sanidade_maxima"),
                rs.getInt("sanidade_atual"),
                rs.getTimestamp("criado_em").toLocalDateTime()
        );

        if (existeColuna(rs, "caminho_atual")) {
            jogador.setCaminhoAtual(rs.getString("caminho_atual"));
        }

        return jogador;
    }

    private boolean existeColuna(ResultSet rs, String nomeColuna) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        int quantidadeColunas = metaData.getColumnCount();

        for (int i = 1; i <= quantidadeColunas; i++) {
            if (metaData.getColumnName(i).equalsIgnoreCase(nomeColuna)) {
                return true;
            }
        }

        return false;
    }
}
