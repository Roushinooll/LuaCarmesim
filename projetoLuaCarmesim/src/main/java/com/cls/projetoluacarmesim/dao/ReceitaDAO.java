package com.cls.projetoluacarmesim.dao;

import com.cls.projetoluacarmesim.enums.TipoIngrediente;
import com.cls.projetoluacarmesim.model.FormulaPocao;
import com.cls.projetoluacarmesim.model.IngredienteFormula;
import com.cls.projetoluacarmesim.model.JogadorFormula;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReceitaDAO {

    // -------------------------------------------------------
    // FÓRMULAS
    // -------------------------------------------------------

    public FormulaPocao buscarPorId(int idFormula) throws SQLException {
        String sql = "SELECT * FROM formula_pocao WHERE id_formula = ?";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConexaoBanco.getConexao();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, idFormula);
            rs = ps.executeQuery();

            if (rs.next()) {
                FormulaPocao formula = mapearFormula(rs);
                formula.setIngredientes(listarIngredientes(idFormula));
                return formula;
            }
            return null;
        } finally {
            ConexaoBanco.fechar(rs, ps, conn);
        }
    }

    public List<FormulaPocao> listarTodas() throws SQLException {
        String sql = "SELECT * FROM formula_pocao ORDER BY nivel_sequencia DESC";
        List<FormulaPocao> lista = new ArrayList<>();

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConexaoBanco.getConexao();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                FormulaPocao f = mapearFormula(rs);
                f.setIngredientes(listarIngredientes(f.getIdFormula()));
                lista.add(f);
            }
            return lista;
        } finally {
            ConexaoBanco.fechar(rs, ps, conn);
        }
    }

    public List<FormulaPocao> buscarPorNivel(int nivelSequencia) throws SQLException {
        String sql = "SELECT * FROM formula_pocao WHERE nivel_sequencia = ?";
        List<FormulaPocao> lista = new ArrayList<>();

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConexaoBanco.getConexao();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, nivelSequencia);
            rs = ps.executeQuery();

            while (rs.next()) {
                FormulaPocao f = mapearFormula(rs);
                f.setIngredientes(listarIngredientes(f.getIdFormula()));
                lista.add(f);
            }
            return lista;
        } finally {
            ConexaoBanco.fechar(rs, ps, conn);
        }
    }

    // -------------------------------------------------------
    // INGREDIENTES
    // -------------------------------------------------------

    public List<IngredienteFormula> listarIngredientes(int idFormula) throws SQLException {
        String sql = "SELECT * FROM ingrediente_formula WHERE id_formula = ?";
        List<IngredienteFormula> lista = new ArrayList<>();

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConexaoBanco.getConexao();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, idFormula);
            rs = ps.executeQuery();

            while (rs.next()) lista.add(mapearIngrediente(rs));
            return lista;
        } finally {
            ConexaoBanco.fechar(rs, ps, conn);
        }
    }

    // -------------------------------------------------------
    // JOGADOR x FÓRMULA (fórmulas aprendidas)
    // -------------------------------------------------------

    public void marcarComoAprendida(int idJogador, int idFormula) throws SQLException {
        String sql = "INSERT INTO jogador_formula (id_jogador, id_formula) VALUES (?, ?) "
                   + "ON CONFLICT DO NOTHING";

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = ConexaoBanco.getConexao();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, idJogador);
            ps.setInt(2, idFormula);
            ps.executeUpdate();
        } finally {
            ConexaoBanco.fechar(ps, conn);
        }
    }

    public List<JogadorFormula> listarAprendidasPorJogador(int idJogador) throws SQLException {
        String sql = "SELECT jf.*, fp.nome_pocao, fp.nivel_sequencia, "
                   + "fp.efeito_principal, fp.descricao "
                   + "FROM jogador_formula jf "
                   + "JOIN formula_pocao fp ON jf.id_formula = fp.id_formula "
                   + "WHERE jf.id_jogador = ? "
                   + "ORDER BY fp.nivel_sequencia DESC";

        List<JogadorFormula> lista = new ArrayList<>();

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConexaoBanco.getConexao();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, idJogador);
            rs = ps.executeQuery();

            while (rs.next()) {
                JogadorFormula jf = new JogadorFormula(
                    rs.getInt("id_jogador"),
                    rs.getInt("id_formula"),
                    rs.getTimestamp("aprendida_em").toLocalDateTime()
                );

                FormulaPocao fp = new FormulaPocao(
                    rs.getInt("id_formula"),
                    rs.getString("nome_pocao"),
                    rs.getInt("nivel_sequencia"),
                    rs.getString("efeito_principal"),
                    rs.getString("descricao")
                );
                jf.setFormula(fp);
                lista.add(jf);
            }
            return lista;
        } finally {
            ConexaoBanco.fechar(rs, ps, conn);
        }
    }

    public boolean jaAprendeu(int idJogador, int idFormula) throws SQLException {
        String sql = "SELECT 1 FROM jogador_formula WHERE id_jogador = ? AND id_formula = ?";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConexaoBanco.getConexao();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, idJogador);
            ps.setInt(2, idFormula);
            rs = ps.executeQuery();
            return rs.next();
        } finally {
            ConexaoBanco.fechar(rs, ps, conn);
        }
    }

    // -------------------------------------------------------
    // MAPEAMENTO
    // -------------------------------------------------------

    private FormulaPocao mapearFormula(ResultSet rs) throws SQLException {
        return new FormulaPocao(
            rs.getInt("id_formula"),
            rs.getString("nome_pocao"),
            rs.getInt("nivel_sequencia"),
            rs.getString("efeito_principal"),
            rs.getString("descricao")
        );
    }

    private IngredienteFormula mapearIngrediente(ResultSet rs) throws SQLException {
        String tipoStr = rs.getString("tipo_ingrediente").toUpperCase();
        TipoIngrediente tipo = TipoIngrediente.valueOf(tipoStr);

        return new IngredienteFormula(
            rs.getInt("id_ingrediente"),
            rs.getInt("id_formula"),
            rs.getString("nome_ingrediente"),
            tipo,
            rs.getInt("quantidade")
        );
    }
}
