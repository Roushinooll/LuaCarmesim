package com.cls.projetoluacarmesim;

import com.cls.projetoluacarmesim.enums.TipoRua;
import com.cls.projetoluacarmesim.model.Jogador;
import com.cls.projetoluacarmesim.util.InventarioManager;

import java.util.ArrayList;
import java.util.List;

public class EstadoJogo {

    private static EstadoJogo instancia;

    private Jogador jogadorAtual;
    private InventarioManager inventario;

    private double personagemTranslateX = 0;
    private double personagemTranslateY = 0;
    private boolean posicaoPersonagemSalva = false;

    private String telaAnteriorInventario = "restroom";

    private int numeroRuaAtual = 1;
    private TipoRua tipoRuaAtual;
    private double ruaPersonagemTranslateX = 0;
    private double ruaPersonagemTranslateY = 0;
    private boolean ruaEmAndamento = false;

    private final List<double[]> posicoesInimigosRua = new ArrayList<>();
    private final List<double[]> posicoesItensRua = new ArrayList<>();
    private final List<double[]> posicoesReceitasRua = new ArrayList<>();
    private final List<Integer> idsReceitasRua = new ArrayList<>();

    private EstadoJogo() {
        inventario = new InventarioManager();
    }

    public static EstadoJogo getInstance() {
        if (instancia == null) {
            instancia = new EstadoJogo();
        }

        return instancia;
    }

    public Jogador getJogadorAtual() {
        return jogadorAtual;
    }

    public void setJogadorAtual(Jogador jogadorAtual) {
        this.jogadorAtual = jogadorAtual;
    }

    public InventarioManager getInventario() {
        return inventario;
    }

    public void salvarPosicaoPersonagem(double x, double y) {
        this.personagemTranslateX = x;
        this.personagemTranslateY = y;
        this.posicaoPersonagemSalva = true;
    }

    public double getPersonagemTranslateX() {
        return personagemTranslateX;
    }

    public double getPersonagemTranslateY() {
        return personagemTranslateY;
    }

    public boolean isPosicaoPersonagemSalva() {
        return posicaoPersonagemSalva;
    }

    public void setTelaAnteriorInventario(String telaAnteriorInventario) {
        this.telaAnteriorInventario = telaAnteriorInventario;
    }

    public String getTelaAnteriorInventario() {
        return telaAnteriorInventario;
    }

    public void salvarEstadoRua(int numeroRua, double translateX, double translateY) {
        this.numeroRuaAtual = numeroRua;
        this.ruaPersonagemTranslateX = translateX;
        this.ruaPersonagemTranslateY = translateY;
        this.ruaEmAndamento = true;
    }

    public void salvarEstadoRuaCompleto(
            int numeroRua,
            TipoRua tipoRua,
            double translateX,
            double translateY,
            List<double[]> posicoesInimigos,
            List<double[]> posicoesItens,
            List<double[]> posicoesReceitas,
            List<Integer> idsReceitas
    ) {
        this.numeroRuaAtual = numeroRua;
        this.tipoRuaAtual = tipoRua;
        this.ruaPersonagemTranslateX = translateX;
        this.ruaPersonagemTranslateY = translateY;
        this.ruaEmAndamento = true;

        this.posicoesInimigosRua.clear();
        for (double[] posicao : posicoesInimigos) {
            this.posicoesInimigosRua.add(new double[]{posicao[0], posicao[1]});
        }

        this.posicoesItensRua.clear();
        for (double[] posicao : posicoesItens) {
            this.posicoesItensRua.add(new double[]{posicao[0], posicao[1]});
        }

        this.posicoesReceitasRua.clear();
        for (double[] posicao : posicoesReceitas) {
            this.posicoesReceitasRua.add(new double[]{posicao[0], posicao[1]});
        }

        this.idsReceitasRua.clear();
        this.idsReceitasRua.addAll(idsReceitas);
    }

    public int getNumeroRuaAtual() {
        return numeroRuaAtual;
    }

    public TipoRua getTipoRuaAtual() {
        return tipoRuaAtual;
    }

    public double getRuaPersonagemTranslateX() {
        return ruaPersonagemTranslateX;
    }

    public double getRuaPersonagemTranslateY() {
        return ruaPersonagemTranslateY;
    }

    public boolean isRuaEmAndamento() {
        return ruaEmAndamento;
    }


    public void resetarRua() {
        this.numeroRuaAtual = 1;
        this.tipoRuaAtual = null;
        this.ruaPersonagemTranslateX = 0;
        this.ruaPersonagemTranslateY = 0;
        this.ruaEmAndamento = false;
        this.posicoesInimigosRua.clear();
        this.posicoesItensRua.clear();
        this.posicoesReceitasRua.clear();
        this.idsReceitasRua.clear();
    }

    public List<double[]> getPosicoesInimigosRua() {
        List<double[]> copia = new ArrayList<>();

        for (double[] posicao : posicoesInimigosRua) {
            copia.add(new double[]{posicao[0], posicao[1]});
        }

        return copia;
    }

    public List<double[]> getPosicoesItensRua() {
        List<double[]> copia = new ArrayList<>();

        for (double[] posicao : posicoesItensRua) {
            copia.add(new double[]{posicao[0], posicao[1]});
        }

        return copia;
    }

    public List<double[]> getPosicoesReceitasRua() {
        List<double[]> copia = new ArrayList<>();

        for (double[] posicao : posicoesReceitasRua) {
            copia.add(new double[]{posicao[0], posicao[1]});
        }

        return copia;
    }

    public List<Integer> getIdsReceitasRua() {
        return new ArrayList<>(idsReceitasRua);
    }
}
