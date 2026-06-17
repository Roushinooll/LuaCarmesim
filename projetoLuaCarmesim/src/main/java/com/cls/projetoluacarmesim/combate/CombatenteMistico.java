package com.cls.projetoluacarmesim.combate;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class CombatenteMistico {

    private final String nome;
    private final boolean jogador;
    private String caminho;
    private int sequencia;
    private final int vidaMaxima;
    private int vidaAtual;
    private final int sanidadeMaxima;
    private int sanidadeAtual;
    private final int danoFisicoBase;
    private final int defesaBase;

    private final Map<TipoStatusCombate, Integer> status = new EnumMap<>(TipoStatusCombate.class);

    private int turnosDefesa;
    private int percentualReducaoDano;
    private int turnosReflexao;
    private int percentualReflexao;
    private int turnosEvasao;
    private int chanceEvasao;
    private int bonusProximoDano;
    private int chanceCriticoProximo;
    private int danoFogoProximoAtaque;
    private int turnosProtecaoMental;
    private int turnosReducaoCustoSanidade;
    private int modificadorDanoCausado;
    private int modificadorPrecisao;
    private int modificadorDefesa;

    public CombatenteMistico(String nome, boolean jogador, String caminho, int sequencia,
                             int vidaMaxima, int vidaAtual, int sanidadeMaxima, int sanidadeAtual,
                             int danoFisicoBase, int defesaBase) {
        this.nome = nome;
        this.jogador = jogador;
        this.caminho = caminho;
        this.sequencia = sequencia;
        this.vidaMaxima = Math.max(1, vidaMaxima);
        this.vidaAtual = Math.max(0, Math.min(vidaAtual, this.vidaMaxima));
        this.sanidadeMaxima = Math.max(1, sanidadeMaxima);
        this.sanidadeAtual = Math.max(0, Math.min(sanidadeAtual, this.sanidadeMaxima));
        this.danoFisicoBase = danoFisicoBase;
        this.defesaBase = defesaBase;
    }

    public String getNome() {
        return nome;
    }

    public boolean isJogador() {
        return jogador;
    }

    public String getCaminho() {
        return caminho;
    }

    public void setCaminho(String caminho) {
        this.caminho = caminho;
    }

    public int getSequencia() {
        return sequencia;
    }

    public void setSequencia(int sequencia) {
        this.sequencia = sequencia;
    }

    public int getVidaMaxima() {
        return vidaMaxima;
    }

    public int getVidaAtual() {
        return vidaAtual;
    }

    public void setVidaAtual(int vidaAtual) {
        this.vidaAtual = Math.max(0, Math.min(vidaAtual, vidaMaxima));
    }

    public int getSanidadeMaxima() {
        return sanidadeMaxima;
    }

    public int getSanidadeAtual() {
        return sanidadeAtual;
    }

    public void setSanidadeAtual(int sanidadeAtual) {
        this.sanidadeAtual = Math.max(0, Math.min(sanidadeAtual, sanidadeMaxima));
    }

    public int getDanoFisicoBase() {
        return danoFisicoBase;
    }

    public int getDefesaBase() {
        return defesaBase;
    }

    public boolean estaVivo() {
        return vidaAtual > 0;
    }

    public int curarVida(int quantidade) {
        int antes = vidaAtual;
        vidaAtual = Math.min(vidaMaxima, vidaAtual + Math.max(0, quantidade));
        return vidaAtual - antes;
    }

    public int recuperarSanidade(int quantidade) {
        int antes = sanidadeAtual;
        sanidadeAtual = Math.min(sanidadeMaxima, sanidadeAtual + Math.max(0, quantidade));
        return sanidadeAtual - antes;
    }

    public boolean gastarSanidade(int custo) {
        int custoFinal = custo;
        if (turnosReducaoCustoSanidade > 0) {
            custoFinal = Math.max(1, custo / 2);
        }

        if (sanidadeAtual < custoFinal) {
            return false;
        }

        sanidadeAtual -= custoFinal;
        return true;
    }

    public int calcularCustoFinal(int custo) {
        if (turnosReducaoCustoSanidade > 0) {
            return Math.max(1, custo / 2);
        }

        return custo;
    }

    public void adicionarStatus(TipoStatusCombate tipo, int turnos) {
        if (tipo == null || turnos <= 0) {
            return;
        }

        if (turnosProtecaoMental > 0 && ehMental(tipo)) {
            return;
        }

        int turnosAtuais = status.getOrDefault(tipo, 0);
        status.put(tipo, Math.max(turnosAtuais, turnos));
    }

    public void removerStatus(TipoStatusCombate tipo) {
        status.remove(tipo);
    }

    public boolean possuiStatus(TipoStatusCombate tipo) {
        return status.getOrDefault(tipo, 0) > 0;
    }

    public String textoStatus() {
        if (status.isEmpty()) {
            return "Sem máculas";
        }

        List<String> nomes = new ArrayList<>();
        for (Map.Entry<TipoStatusCombate, Integer> entry : status.entrySet()) {
            nomes.add(entry.getKey().getNomeExibicao() + "(" + entry.getValue() + ")");
        }

        return String.join(", ", nomes);
    }

    public int receberDano(int danoBase, boolean fisico, Random random) {
        int dano = Math.max(0, danoBase);

        if (possuiStatus(TipoStatusCombate.REVELADO)) {
            dano = (int) Math.round(dano * 1.35);
            removerStatus(TipoStatusCombate.REVELADO);
        }

        if (possuiStatus(TipoStatusCombate.MARCADO) && fisico) {
            dano = (int) Math.round(dano * 1.45);
        }

        if (possuiStatus(TipoStatusCombate.SENTENCIADO) && estaDebilitado()) {
            dano = (int) Math.round(dano * 1.75);
        }

        if (possuiStatus(TipoStatusCombate.FRAGILIZADO)) {
            dano = (int) Math.round(dano * 1.20);
        }

        if (turnosDefesa > 0) {
            dano = dano * Math.max(0, 100 - percentualReducaoDano) / 100;
        }

        int defesaFinal = Math.max(0, defesaBase + modificadorDefesa);
        int danoEfetivo = Math.max(1, dano - defesaFinal);
        vidaAtual = Math.max(0, vidaAtual - danoEfetivo);
        return danoEfetivo;
    }

    public int prepararDanoBase(int danoBase, boolean fisico, Random random) {
        int dano = Math.max(1, danoBase + bonusProximoDano);

        if (modificadorDanoCausado != 0) {
            dano = Math.max(1, dano * Math.max(10, 100 + modificadorDanoCausado) / 100);
        }

        if (possuiStatus(TipoStatusCombate.PROVOCADO)) {
            dano = (int) Math.round(dano * 1.25);
        }

        if (chanceCriticoProximo > 0 && random.nextInt(100) < chanceCriticoProximo) {
            dano = (int) Math.round(dano * 1.65);
        }

        if (danoFogoProximoAtaque > 0) {
            dano += danoFogoProximoAtaque;
        }

        bonusProximoDano = 0;
        chanceCriticoProximo = 0;
        danoFogoProximoAtaque = 0;
        return dano;
    }

    public boolean tentarEvadir(Random random) {
        if (turnosEvasao <= 0 || chanceEvasao <= 0) {
            return false;
        }

        return random.nextInt(100) < chanceEvasao;
    }

    public void configurarDefesa(int turnos, int percentualReducao) {
        this.turnosDefesa = Math.max(this.turnosDefesa, turnos + 1);
        this.percentualReducaoDano = Math.max(this.percentualReducaoDano, percentualReducao);
    }

    public void configurarReflexao(int turnos, int percentualReflexao) {
        this.turnosReflexao = Math.max(this.turnosReflexao, turnos + 1);
        this.percentualReflexao = Math.max(this.percentualReflexao, percentualReflexao);
    }

    public boolean possuiReflexao() {
        return turnosReflexao > 0 && percentualReflexao > 0;
    }

    public int calcularDanoRefletido(int danoRecebido) {
        if (!possuiReflexao()) {
            return 0;
        }

        return Math.max(1, danoRecebido * percentualReflexao / 100);
    }

    public void configurarEvasao(int turnos, int chance) {
        this.turnosEvasao = Math.max(this.turnosEvasao, turnos + 1);
        this.chanceEvasao = Math.max(this.chanceEvasao, chance);
    }

    public void adicionarBonusProximoDano(int bonus) {
        this.bonusProximoDano += bonus;
    }

    public void adicionarChanceCriticoProximo(int chance) {
        this.chanceCriticoProximo = Math.max(this.chanceCriticoProximo, chance);
    }

    public void adicionarDanoFogoProximoAtaque(int dano) {
        this.danoFogoProximoAtaque += dano;
    }

    public void protegerMente(int turnos) {
        this.turnosProtecaoMental = Math.max(this.turnosProtecaoMental, turnos + 1);
        removerStatus(TipoStatusCombate.AMEDRONTADO);
        removerStatus(TipoStatusCombate.CONFUSO);
        removerStatus(TipoStatusCombate.DESEJO_CORROMPIDO);
    }

    public void reduzirCustoSanidade(int turnos) {
        this.turnosReducaoCustoSanidade = Math.max(this.turnosReducaoCustoSanidade, turnos + 1);
    }

    public void modificarDanoCausado(int percentual) {
        this.modificadorDanoCausado += percentual;
    }

    public void modificarPrecisao(int percentual) {
        this.modificadorPrecisao += percentual;
    }

    public void modificarDefesa(int percentual) {
        this.modificadorDefesa += percentual;
    }

    public int getModificadorPrecisao() {
        return modificadorPrecisao;
    }

    public int aplicarSangramento() {
        if (!possuiStatus(TipoStatusCombate.SANGRAMENTO)) {
            return 0;
        }

        int dano = Math.max(4, vidaMaxima / 14);
        vidaAtual = Math.max(0, vidaAtual - dano);
        return dano;
    }

    public int aplicarDesejoCorrompido(Random random) {
        if (!possuiStatus(TipoStatusCombate.DESEJO_CORROMPIDO)) {
            return 0;
        }

        if (random.nextInt(100) >= 45) {
            return 0;
        }

        int dano = Math.max(6, vidaMaxima / 12);
        vidaAtual = Math.max(0, vidaAtual - dano);
        return dano;
    }

    public boolean devePerderTurno(Random random) {
        if (possuiStatus(TipoStatusCombate.IMOBILIZADO) && random.nextInt(100) < 45) {
            return true;
        }

        if (possuiStatus(TipoStatusCombate.ATORDOADO) && random.nextInt(100) < 40) {
            return true;
        }

        if (possuiStatus(TipoStatusCombate.AMEDRONTADO) && random.nextInt(100) < 30) {
            return true;
        }

        if (possuiStatus(TipoStatusCombate.CONFUSO) && random.nextInt(100) < 35) {
            return true;
        }

        if (possuiStatus(TipoStatusCombate.DESEJO_CORROMPIDO) && random.nextInt(100) < 35) {
            return true;
        }

        return false;
    }

    public boolean podeUsarHabilidadeEspecial() {
        return !possuiStatus(TipoStatusCombate.DECRETADO);
    }

    public boolean estaDebilitado() {
        return vidaAtual <= vidaMaxima * 35 / 100;
    }

    public void tickFimDoTurno() {
        tickStatus();
        if (turnosDefesa > 0) {
            turnosDefesa--;
            if (turnosDefesa == 0) {
                percentualReducaoDano = 0;
            }
        }
        if (turnosReflexao > 0) {
            turnosReflexao--;
            if (turnosReflexao == 0) {
                percentualReflexao = 0;
            }
        }
        if (turnosEvasao > 0) {
            turnosEvasao--;
            if (turnosEvasao == 0) {
                chanceEvasao = 0;
            }
        }
        if (turnosProtecaoMental > 0) {
            turnosProtecaoMental--;
        }
        if (turnosReducaoCustoSanidade > 0) {
            turnosReducaoCustoSanidade--;
        }
    }

    private void tickStatus() {
        List<TipoStatusCombate> expirados = new ArrayList<>();
        for (Map.Entry<TipoStatusCombate, Integer> entry : status.entrySet()) {
            int restante = entry.getValue() - 1;
            if (restante <= 0) {
                expirados.add(entry.getKey());
            } else {
                entry.setValue(restante);
            }
        }

        for (TipoStatusCombate tipo : expirados) {
            status.remove(tipo);
        }
    }

    private boolean ehMental(TipoStatusCombate tipo) {
        return tipo == TipoStatusCombate.AMEDRONTADO
                || tipo == TipoStatusCombate.CONFUSO
                || tipo == TipoStatusCombate.DESEJO_CORROMPIDO
                || tipo == TipoStatusCombate.DECRETADO;
    }
}
