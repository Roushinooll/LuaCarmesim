package com.cls.projetoluacarmesim.combate;

import java.util.Random;

public class ExecutorHabilidades {

    private final Random random = new Random();

    public ResultadoHabilidade usar(HabilidadeCombate habilidade,
                                    CombatenteMistico usuario,
                                    CombatenteMistico alvo) {
        if (habilidade == null) {
            return ResultadoHabilidade.falha("Nenhuma habilidade selecionada.");
        }

        if (usuario == null || alvo == null) {
            return ResultadoHabilidade.falha("Combate místico não inicializado.");
        }

        if (!usuario.podeUsarHabilidadeEspecial()) {
            return ResultadoHabilidade.falha(usuario.getNome() + " está Decretado e não consegue usar habilidades especiais.");
        }

        int custoFinal = usuario.calcularCustoFinal(habilidade.getCustoSanidade());
        if (!usuario.gastarSanidade(habilidade.getCustoSanidade())) {
            return ResultadoHabilidade.falha(usuario.getNome() + " não tem Sanidade suficiente. Custo necessário: " + custoFinal + ".");
        }

        ResultadoHabilidade resultado = ResultadoHabilidade.sucesso();
        resultado.adicionarMensagem(usuario.getNome() + " usa " + habilidade.getNome() + ".");

        switch (habilidade.getNome()) {
            
            case "Presságio Cortante":
                dano(resultado, usuario, alvo, 16 + escala(usuario), true);
                if (chance(55)) {
                    aplicarStatus(resultado, alvo, TipoStatusCombate.REVELADO, 2);
                }
                break;

            case "Riso do Azar":
                dano(resultado, usuario, alvo, 22 + escala(usuario), true);
                if (alvo.possuiStatus(TipoStatusCombate.REVELADO) || chance(45)) {
                    danoDireto(resultado, alvo, 8 + escala(usuario) / 2, "O azar reverbera como trauma adicional");
                    alvo.modificarPrecisao(-20);
                    resultado.adicionarMensagem(alvo.getNome() + " tem a precisão comprometida.");
                }
                break;

            case "Olhar Espiritual":
                aplicarStatus(resultado, alvo, TipoStatusCombate.REVELADO, 2);
                resultado.adicionarMensagem("Leitura do alvo: Vida " + alvo.getVidaAtual() + "/" + alvo.getVidaMaxima()
                        + ", Sanidade " + alvo.getSanidadeAtual() + "/" + alvo.getSanidadeMaxima()
                        + ", Defesa " + alvo.getDefesaBase()
                        + ", " + HabilidadeCatalogo.nomeCaminhoParaExibicao(alvo.getCaminho())
                        + ", Máculas: " + alvo.textoStatus() + ".");
                break;

            case "Truque Revelador":
                aplicarStatus(resultado, alvo, TipoStatusCombate.REVELADO, 3);
                usuario.adicionarChanceCriticoProximo(60);
                usuario.adicionarBonusProximoDano(8 + escala(usuario));
                resultado.adicionarMensagem("A próxima investida de " + usuario.getNome() + " recebe letalidade crítica ampliada.");
                break;

            case "Desvio Premonitório":
                usuario.configurarEvasao(1, 70);
                resultado.adicionarMensagem(usuario.getNome() + " antecipa agressões por um turno.");
                break;

            case "Máscara da Fuga":
                usuario.configurarEvasao(1, 92);
                usuario.protegerMente(1);
                resultado.adicionarMensagem(usuario.getNome() + " distorce a própria presença e expurga máculas psíquicas elementares.");
                break;

            case "Sussurro do Destino":
                if (chance(50)) {
                    aplicarStatus(resultado, alvo, TipoStatusCombate.CONFUSO, 2);
                } else {
                    aplicarStatus(resultado, alvo, TipoStatusCombate.AMEDRONTADO, 2);
                }
                break;

            case "Fios da Vontade":
                aplicarStatus(resultado, alvo, TipoStatusCombate.CONFUSO, 2);
                if (chance(55)) {
                    aplicarStatus(resultado, alvo, TipoStatusCombate.AMEDRONTADO, 1);
                }
                if (chance(45)) {
                    int danoSi = 10 + escala(usuario);
                    alvo.receberDano(danoSi, false, random);
                    resultado.adicionarMensagem(alvo.getNome() + " é coagido e fere a própria carne, sofrendo " + danoSi + " de dano.");
                }
                break;

            case "Teatro da Marionete":
                if (alvo.estaDebilitado() || alvo.possuiStatus(TipoStatusCombate.AMEDRONTADO)) {
                    aplicarStatus(resultado, alvo, TipoStatusCombate.CONFUSO, 2);
                    aplicarStatus(resultado, alvo, TipoStatusCombate.IMOBILIZADO, 2);
                    resultado.adicionarMensagem(alvo.getNome() + " torna-se receptáculo temporário por dois turnos.");
                } else {
                    alvo.modificarDanoCausado(-35);
                    alvo.modificarDefesa(-20);
                    aplicarStatus(resultado, alvo, TipoStatusCombate.FRAGILIZADO, 2);
                    resultado.adicionarMensagem("Como o alvo ainda resiste, seus atributos combativos sofrem atrofia severa.");
                }
                aplicarRiscoAlucinacao(resultado, usuario, 25);
                break;

            
            case "Golpe Baixo":
                dano(resultado, usuario, alvo, 18 + escala(usuario), true);
                if (chance(55)) {
                    aplicarStatus(resultado, alvo, TipoStatusCombate.SANGRAMENTO, 3);
                }
                break;

            case "Investida Profana":
                dano(resultado, usuario, alvo, 28 + escala(usuario), true);
                aplicarStatus(resultado, alvo, TipoStatusCombate.SANGRAMENTO, 3);
                if (alvo.estaDebilitado()) {
                    danoDireto(resultado, alvo, 8 + escala(usuario), "O alvo já avariado sofre trauma adicional");
                }
                break;

            case "Intimidação Suja":
                aplicarStatus(resultado, alvo, TipoStatusCombate.AMEDRONTADO, 2);
                break;

            case "Marca do Abate":
                aplicarStatus(resultado, alvo, TipoStatusCombate.MARCADO, 3);
                if (alvo.possuiStatus(TipoStatusCombate.SANGRAMENTO)) {
                    danoDireto(resultado, alvo, 8 + escala(usuario), "A hemorragia marcada se agrava");
                }
                break;

            case "Corpo Acostumado à Dor":
                usuario.configurarDefesa(2, 35);
                resultado.adicionarMensagem(usuario.getNome() + " atenua o dano recebido por dois turnos.");
                break;

            case "Pele Demoníaca":
                usuario.configurarDefesa(2, 45);
                usuario.configurarReflexao(2, 30);
                resultado.adicionarMensagem(usuario.getNome() + " mitiga dano e refletirá parte das próximas agressões.");
                break;

            case "Instinto de Sobrevivência":
                if (usuario.estaDebilitado()) {
                    usuario.adicionarBonusProximoDano(16 + escala(usuario));
                    usuario.adicionarChanceCriticoProximo(55);
                    resultado.adicionarMensagem("A exaustão vital desperta potência ofensiva e letalidade crítica.");
                } else {
                    usuario.adicionarBonusProximoDano(6);
                    resultado.adicionarMensagem("O instinto desperta, mas ainda sem atingir o ápice da debilitação.");
                }
                break;

            case "Desejo de Matar":
                if (usuario.estaDebilitado()) {
                    usuario.modificarDanoCausado(45);
                    usuario.adicionarChanceCriticoProximo(75);
                    usuario.protegerMente(2);
                    resultado.adicionarMensagem("Sob trauma severo, " + usuario.getNome() + " recebe bônus massivo de letalidade e proteção mental.");
                } else {
                    usuario.adicionarBonusProximoDano(10 + escala(usuario));
                    usuario.protegerMente(1);
                    resultado.adicionarMensagem("O desejo assassino cresce, mas ficará mais forte em baixa vitalidade.");
                }
                break;

            case "Sermão da Tentação Carmesim":
                aplicarStatus(resultado, alvo, TipoStatusCombate.DESEJO_CORROMPIDO, 3);
                if (alvo.estaDebilitado() && chance(40)) {
                    aplicarStatus(resultado, alvo, TipoStatusCombate.IMOBILIZADO, 1);
                    resultado.adicionarMensagem(alvo.getNome() + " quase cessa a hostilidade diante da tentação carmesim.");
                }
                aplicarRiscoAlucinacao(resultado, usuario, 25);
                break;

            
            case "Disparo Preciso":
                usuario.adicionarChanceCriticoProximo(45);
                dano(resultado, usuario, alvo, 20 + escala(usuario), true);
                break;

            case "Tiro Humilhante":
                dano(resultado, usuario, alvo, 23 + escala(usuario), true);
                if (chance(55)) {
                    aplicarStatus(resultado, alvo, TipoStatusCombate.PROVOCADO, 2);
                } else {
                    alvo.modificarDefesa(-15);
                    resultado.adicionarMensagem(alvo.getNome() + " tem a defesa atrofida.");
                }
                break;

            case "Rastrear Fraqueza":
                usuario.adicionarChanceCriticoProximo(65);
                usuario.modificarPrecisao(30);
                aplicarStatus(resultado, alvo, TipoStatusCombate.REVELADO, 2);
                resultado.adicionarMensagem(usuario.getNome() + " maximiza precisão e chance crítica contra o alvo.");
                break;

            case "Mira Incendiária":
                usuario.adicionarChanceCriticoProximo(70);
                usuario.adicionarDanoFogoProximoAtaque(14 + escala(usuario));
                aplicarStatus(resultado, alvo, TipoStatusCombate.REVELADO, 2);
                resultado.adicionarMensagem("A próxima agressão recebe combustão persistente.");
                break;

            case "Armadilha Improvisada":
                dano(resultado, usuario, alvo, 13 + escala(usuario), true);
                if (chance(50)) {
                    aplicarStatus(resultado, alvo, TipoStatusCombate.IMOBILIZADO, 1);
                }
                break;

            case "Plano de Emboscada":
                dano(resultado, usuario, alvo, 22 + escala(usuario), true);
                if (chance(65)) {
                    aplicarStatus(resultado, alvo, TipoStatusCombate.IMOBILIZADO, 1);
                }
                aplicarStatus(resultado, alvo, TipoStatusCombate.FRAGILIZADO, 2);
                break;

            case "Grito de Caça":
                aplicarStatus(resultado, alvo, TipoStatusCombate.PROVOCADO, 2);
                alvo.modificarDefesa(-10);
                resultado.adicionarMensagem(alvo.getNome() + " fica mais agressivo, porém menos protegido.");
                break;

            case "Sentença do Predador":
                aplicarStatus(resultado, alvo, TipoStatusCombate.SENTENCIADO, 3);
                if (alvo.estaDebilitado()) {
                    danoDireto(resultado, alvo, 18 + escala(usuario), "A sentença encontra um alvo debilitado e rasga sua defesa");
                }
                break;

            case "Ceifa do Ciclo":
                int vidaAntes = alvo.getVidaAtual();
                int danoCeifa = alvo.estaDebilitado() || alvo.possuiStatus(TipoStatusCombate.SENTENCIADO)
                        ? 80 + escala(usuario) * 3
                        : 42 + escala(usuario) * 2;
                dano(resultado, usuario, alvo, danoCeifa, true);
                if (vidaAntes > 0 && !alvo.estaVivo()) {
                    int cura = usuario.curarVida(24 + escala(usuario));
                    resultado.adicionarMensagem("A Ceifa do Ciclo restaura " + cura + " de vida e prepara um novo impulso ofensivo.");
                }
                aplicarRiscoAlucinacao(resultado, usuario, 22);
                break;

            
            case "Nota Dilacerante":
                dano(resultado, usuario, alvo, 16 + escala(usuario), false);
                if (chance(45)) {
                    aplicarStatus(resultado, alvo, TipoStatusCombate.ATORDOADO, 2);
                }
                break;

            case "Nota Purificadora":
                int bonusCorrompido = alvo.getCaminho() != null ? 8 : 0;
                dano(resultado, usuario, alvo, 23 + escala(usuario) + bonusCorrompido, false);
                break;

            case "Canção Revigorante":
                int cura = usuario.curarVida(18 + escala(usuario));
                int sanidade = usuario.recuperarSanidade(10);
                resultado.adicionarMensagem(usuario.getNome() + " recupera " + cura + " de vida e " + sanidade + " de Sanidade.");
                break;

            case "Cântico Solar":
                cura = usuario.curarVida(28 + escala(usuario));
                limparMaculasElementares(usuario);
                resultado.adicionarMensagem(usuario.getNome() + " recupera " + cura + " de vida e elimina máculas elementares.");
                break;

            case "Verso Desmoralizante":
                alvo.modificarDanoCausado(-25);
                alvo.modificarPrecisao(-25);
                resultado.adicionarMensagem(alvo.getNome() + " perde potencial ofensivo e precisão.");
                break;

            case "Decreto Sagrado":
                alvo.modificarDanoCausado(-35);
                aplicarStatus(resultado, alvo, TipoStatusCombate.DECRETADO, 2);
                resultado.adicionarMensagem(alvo.getNome() + " tem aptidões especiais inibidas temporariamente.");
                break;

            case "Hino de Coragem":
                usuario.protegerMente(2);
                usuario.configurarDefesa(2, 20);
                resultado.adicionarMensagem(usuario.getNome() + " fortalece a salvaguarda psíquica contra pavor e delírio.");
                break;

            case "Juramento da Primeira Chama":
                usuario.protegerMente(3);
                usuario.configurarDefesa(2, 50);
                usuario.reduzirCustoSanidade(3);
                resultado.adicionarMensagem(usuario.getNome() + " maximiza defesa, convicção mental e reduz o consumo de Sanidade.");
                break;

            case "Sol Absoluto":
                dano(resultado, usuario, alvo, 62 + escala(usuario) * 2, false);
                cura = usuario.curarVida(22 + escala(usuario));
                resultado.adicionarMensagem("A radiação purificadora restaura " + cura + " de vida de " + usuario.getNome() + ".");
                aplicarRiscoAlucinacao(resultado, usuario, 28);
                break;

            default:
                resultado.adicionarMensagem("A habilidade ainda não possui efeito implementado.");
                break;
        }

        return resultado;
    }

    public int atacarFisicamente(CombatenteMistico atacante, CombatenteMistico alvo, int danoBase, boolean fisico) {
        if (alvo.tentarEvadir(random)) {
            return 0;
        }

        int danoPreparado = atacante.prepararDanoBase(danoBase, fisico, random);
        return alvo.receberDano(danoPreparado, fisico, random);
    }

    private void dano(ResultadoHabilidade resultado, CombatenteMistico usuario,
                      CombatenteMistico alvo, int danoBase, boolean fisico) {
        if (alvo.tentarEvadir(random)) {
            resultado.adicionarMensagem(alvo.getNome() + " evade a investida.");
            return;
        }

        int danoPreparado = usuario.prepararDanoBase(danoBase, fisico, random);
        int dano = alvo.receberDano(danoPreparado, fisico, random);
        resultado.adicionarMensagem(alvo.getNome() + " sofre " + dano + " de dano.");

        refletirSeNecessario(resultado, usuario, alvo, dano);
    }

    private void danoDireto(ResultadoHabilidade resultado, CombatenteMistico alvo, int dano, String prefixo) {
        int recebido = alvo.receberDano(dano, false, random);
        resultado.adicionarMensagem(prefixo + ": " + alvo.getNome() + " sofre " + recebido + " de dano.");
    }

    private void refletirSeNecessario(ResultadoHabilidade resultado, CombatenteMistico atacante,
                                      CombatenteMistico defensor, int danoRecebido) {
        if (!defensor.possuiReflexao() || !atacante.estaVivo()) {
            return;
        }

        int refletido = defensor.calcularDanoRefletido(danoRecebido);
        int recebido = atacante.receberDano(refletido, false, random);
        resultado.adicionarMensagem(defensor.getNome() + " reflete " + recebido + " de dano deletério.");
    }

    private void aplicarStatus(ResultadoHabilidade resultado, CombatenteMistico alvo,
                               TipoStatusCombate status, int turnos) {
        alvo.adicionarStatus(status, turnos);
        resultado.adicionarMensagem(alvo.getNome() + " recebe a mácula " + status.getNomeExibicao() + ".");
    }

    private void limparMaculasElementares(CombatenteMistico usuario) {
        usuario.removerStatus(TipoStatusCombate.SANGRAMENTO);
        usuario.removerStatus(TipoStatusCombate.AMEDRONTADO);
        usuario.removerStatus(TipoStatusCombate.CONFUSO);
        usuario.removerStatus(TipoStatusCombate.ATORDOADO);
        usuario.removerStatus(TipoStatusCombate.IMOBILIZADO);
    }

    private void aplicarRiscoAlucinacao(ResultadoHabilidade resultado, CombatenteMistico usuario, int chanceBase) {
        int chanceFinal = chanceBase;
        if (usuario.getSanidadeAtual() <= usuario.getSanidadeMaxima() * 25 / 100) {
            chanceFinal += 25;
        }

        if (chance(chanceFinal)) {
            usuario.adicionarStatus(TipoStatusCombate.CONFUSO, 2);
            resultado.adicionarMensagem("O custo místico é severo: " + usuario.getNome() + " sofre risco de Alucinação e fica Confuso.");
        }
    }

    private int escala(CombatenteMistico usuario) {
        if (usuario.getSequencia() <= 0 || usuario.getSequencia() > 9) {
            return 0;
        }

        return Math.max(0, 10 - usuario.getSequencia()) * 4;
    }

    private boolean chance(int percentual) {
        return random.nextInt(100) < percentual;
    }
}
