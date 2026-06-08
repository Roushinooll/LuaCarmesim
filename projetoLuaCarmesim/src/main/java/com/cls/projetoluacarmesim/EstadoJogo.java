package com.cls.projetoluacarmesim;

import com.cls.projetoluacarmesim.model.Jogador;
import com.cls.projetoluacarmesim.util.InventarioManager;

public class EstadoJogo {

    private static EstadoJogo instancia;

    private Jogador jogadorAtual;
    private InventarioManager inventario;

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
}