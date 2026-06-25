package com.cls.projetoluacarmesim.util;

import java.io.*;
import java.util.Properties;







public class GerenciadorConfigs {

    private static final String PASTA   = System.getProperty("user.home") + "/.luacarmesim";
    private static final String ARQUIVO = PASTA + "/configs.properties";

    
    public static final String KEY_MUSICA      = "volume.musica";
    public static final String KEY_SOM         = "volume.som";
    public static final String KEY_TELA_CHEIA  = "tela.cheia";
    public static final String KEY_RESOLUCAO   = "resolucao";

    
    private static final String DEFAULT_MUSICA     = "50";
    private static final String DEFAULT_SOM        = "70";
    private static final String DEFAULT_TELA_CHEIA = "false";
    private static final String DEFAULT_RESOLUCAO  = "1280 x 720";

    private static GerenciadorConfigs instancia;
    private final Properties props = new Properties();

    private GerenciadorConfigs() {
        carregar();
    }

    public static GerenciadorConfigs getInstance() {
        if (instancia == null) instancia = new GerenciadorConfigs();
        return instancia;
    }

    
    
    

    public double getMusica() {
        return Double.parseDouble(props.getProperty(KEY_MUSICA, DEFAULT_MUSICA));
    }

    public double getSom() {
        return Double.parseDouble(props.getProperty(KEY_SOM, DEFAULT_SOM));
    }

    public boolean isTelaCheia() {
        return Boolean.parseBoolean(props.getProperty(KEY_TELA_CHEIA, DEFAULT_TELA_CHEIA));
    }

    public String getResolucao() {
        return props.getProperty(KEY_RESOLUCAO, DEFAULT_RESOLUCAO);
    }

    
    
    

    public void setMusica(double valor) {
        props.setProperty(KEY_MUSICA, String.valueOf((int) valor));
    }

    public void setSom(double valor) {
        props.setProperty(KEY_SOM, String.valueOf((int) valor));
    }

    public void setTelaCheia(boolean valor) {
        props.setProperty(KEY_TELA_CHEIA, String.valueOf(valor));
    }

    public void setResolucao(String valor) {
        props.setProperty(KEY_RESOLUCAO, valor);
    }

    
    
    

    public void resetar() {
        props.setProperty(KEY_MUSICA,     DEFAULT_MUSICA);
        props.setProperty(KEY_SOM,        DEFAULT_SOM);
        props.setProperty(KEY_TELA_CHEIA, DEFAULT_TELA_CHEIA);
        props.setProperty(KEY_RESOLUCAO,  DEFAULT_RESOLUCAO);
    }

    
    
    

    public void salvar() {
        try {
            new File(PASTA).mkdirs();
            try (FileOutputStream fos = new FileOutputStream(ARQUIVO)) {
                props.store(fos, "Lua Carmesim — Configuracoes");
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar configurações: " + e.getMessage());
        }
    }

    private void carregar() {
        File arquivo = new File(ARQUIVO);
        if (arquivo.exists()) {
            try (FileInputStream fis = new FileInputStream(arquivo)) {
                props.load(fis);
            } catch (IOException e) {
                System.err.println("Erro ao carregar configurações: " + e.getMessage());
            }
        } else {
            
            resetar();
        }
    }
}
