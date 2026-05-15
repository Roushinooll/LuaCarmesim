package com.cls.projetoluacarmesim.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoBanco {

    private static final String URL      = "jdbc:postgresql://localhost:5432/lua_carmesim";
    private static final String USUARIO  = "postgres";
    private static final String SENHA    = "postgresql";

    private ConexaoBanco() {}

    public static Connection getConexao() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }

    public static void fechar(AutoCloseable... recursos) {
        for (AutoCloseable r : recursos) {
            if (r != null) {
                try { r.close(); }
                catch (Exception e) { System.err.println("Erro ao fechar recurso: " + e.getMessage()); }
            }
        }
    }
}
