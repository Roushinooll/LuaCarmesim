package com.cls.projetoluacarmesim.combate;

import java.util.ArrayList;
import java.util.List;

public final class HabilidadeCatalogo {

    private HabilidadeCatalogo() {
    }

    public static List<HabilidadeCombate> listarPorProgressao(String caminho, int sequenciaAtual) {
        List<HabilidadeCombate> habilidades = new ArrayList<>();
        String caminhoNormalizado = normalizarCaminho(caminho);

        if (caminhoNormalizado == null || sequenciaAtual > 9) {
            return habilidades;
        }

        switch (caminhoNormalizado) {
            case "Vidente":
                adicionarVidente(habilidades, sequenciaAtual);
                break;
            case "Criminoso":
                adicionarCriminoso(habilidades, sequenciaAtual);
                break;
            case "Caçador":
                adicionarCacador(habilidades, sequenciaAtual);
                break;
            case "Bardo":
                adicionarBardo(habilidades, sequenciaAtual);
                break;
            default:
                break;
        }

        return habilidades;
    }

    public static HabilidadeCombate buscarPorNome(String caminho, int sequenciaAtual, String nomeMenuOuHabilidade) {
        if (nomeMenuOuHabilidade == null) {
            return null;
        }

        for (HabilidadeCombate habilidade : listarPorProgressao(caminho, sequenciaAtual)) {
            if (nomeMenuOuHabilidade.equals(habilidade.getNome())
                    || nomeMenuOuHabilidade.equals(habilidade.getTextoMenu())) {
                return habilidade;
            }
        }

        return null;
    }

    public static String normalizarCaminho(String caminho) {
        if (caminho == null || caminho.isBlank()) {
            return null;
        }

        String texto = caminho.trim()
                .replace("Caminho do ", "")
                .replace("Caminho da ", "")
                .replace("Caminho de ", "")
                .replace("Caminho ", "");

        if (texto.equalsIgnoreCase("vidente")) return "Vidente";
        if (texto.equalsIgnoreCase("criminoso")) return "Criminoso";
        if (texto.equalsIgnoreCase("caçador") || texto.equalsIgnoreCase("cacador")) return "Caçador";
        if (texto.equalsIgnoreCase("bardo")) return "Bardo";

        return texto;
    }

    public static String identificarCaminhoPorPocao(String nomePocao) {
        if (nomePocao == null) {
            return null;
        }

        switch (nomePocao) {
            case "Vidente":
            case "Palhaço":
            case "Mágico":
            case "Sem Rosto":
            case "Marionetista":
                return "Vidente";

            case "Criminoso":
            case "Anjo Sem Asas":
            case "Assassino em Série":
            case "Diabo":
            case "Apóstolo do Desejo":
                return "Criminoso";

            case "Caçador":
            case "Provocador":
            case "Piromaníaco":
            case "Conspirador":
            case "Ceifador":
                return "Caçador";

            case "Bardo":
            case "Suplicante da Luz":
            case "Sumo Sacerdote do Sol":
            case "Notário":
            case "Sacerdote da Luz":
                return "Bardo";

            default:
                return null;
        }
    }

    public static String nomeCaminhoParaExibicao(String caminho) {
        String normalizado = normalizarCaminho(caminho);
        return normalizado == null ? "Sem caminho" : "Caminho do " + normalizado;
    }

    private static void adicionarVidente(List<HabilidadeCombate> habilidades, int sequencia) {
        habilidades.add(new HabilidadeCombate(
                sequencia <= 8 ? "Riso do Azar" : "Presságio Cortante",
                "Vidente", 9, sequencia <= 8 ? 12 : 8, false,
                sequencia <= 8
                        ? "Dano moderado, trauma extra em alvo Revelado e redução de precisão."
                        : "Dano variável com chance de aplicar Revelado."
        ));

        habilidades.add(new HabilidadeCombate(
                sequencia <= 7 ? "Truque Revelador" : "Olhar Espiritual",
                "Vidente", 9, sequencia <= 7 ? 13 : 7, false,
                sequencia <= 7
                        ? "Revela o alvo e amplia a letalidade crítica da próxima investida."
                        : "Desvela vida, defesa, caminho e máculas do inimigo."
        ));

        habilidades.add(new HabilidadeCombate(
                sequencia <= 6 ? "Máscara da Fuga" : "Desvio Premonitório",
                "Vidente", 9, sequencia <= 6 ? 15 : 9, false,
                sequencia <= 6
                        ? "Evasão altíssima por um turno e limpeza de máculas psíquicas."
                        : "Aumenta drasticamente a evasão por um turno."
        ));

        habilidades.add(new HabilidadeCombate(
                sequencia <= 5 ? "Fios da Vontade" : "Sussurro do Destino",
                "Vidente", 9, sequencia <= 5 ? 18 : 10, false,
                sequencia <= 5
                        ? "Pode induzir inércia, confusão ou agressão contra si mesmo."
                        : "Chance de aplicar Confuso ou Amedrontado."
        ));

        if (sequencia <= 5) {
            habilidades.add(new HabilidadeCombate(
                    "Teatro da Marionete", "Vidente", 5, 42, true,
                    "Subjuga alvos debilitados ou Amedrontados por dois turnos; contra alvos fortes, atrofia seus atributos."
            ));
        }
    }

    private static void adicionarCriminoso(List<HabilidadeCombate> habilidades, int sequencia) {
        habilidades.add(new HabilidadeCombate(
                sequencia <= 8 ? "Investida Profana" : "Golpe Baixo",
                "Criminoso", 9, sequencia <= 8 ? 12 : 8, false,
                sequencia <= 8
                        ? "Dano alto, Sangramento melhorado e trauma extra contra alvo avariado."
                        : "Dano moderado com chance de aplicar Sangramento."
        ));

        habilidades.add(new HabilidadeCombate(
                sequencia <= 7 ? "Marca do Abate" : "Intimidação Suja",
                "Criminoso", 9, sequencia <= 7 ? 12 : 8, false,
                sequencia <= 7
                        ? "Aplica Marcado, ampliando danos físicos e hemorragias."
                        : "Alta chance de aplicar Amedrontado, principalmente em humanos."
        ));

        habilidades.add(new HabilidadeCombate(
                sequencia <= 6 ? "Pele Demoníaca" : "Corpo Acostumado à Dor",
                "Criminoso", 9, sequencia <= 6 ? 15 : 9, false,
                sequencia <= 6
                        ? "Reduz dano recebido e reflete parte da agressão."
                        : "Reduz dano recebido por dois turnos."
        ));

        habilidades.add(new HabilidadeCombate(
                sequencia <= 5 ? "Desejo de Matar" : "Instinto de Sobrevivência",
                "Criminoso", 9, sequencia <= 5 ? 18 : 10, false,
                sequencia <= 5
                        ? "Com baixa vida, concede bônus massivo de dano e proteção mental."
                        : "Com baixa vida, amplia dano e chance crítica."
        ));

        if (sequencia <= 5) {
            habilidades.add(new HabilidadeCombate(
                    "Sermão da Tentação Carmesim", "Criminoso", 5, 40, true,
                    "Aplica Desejo Corrompido, podendo gerar inércia, erro tático ou sofrimento psíquico."
            ));
        }
    }

    private static void adicionarCacador(List<HabilidadeCombate> habilidades, int sequencia) {
        habilidades.add(new HabilidadeCombate(
                sequencia <= 8 ? "Tiro Humilhante" : "Disparo Preciso",
                "Caçador", 9, sequencia <= 8 ? 11 : 8, false,
                sequencia <= 8
                        ? "Dano moderado, Provocado ou atrofia defensiva."
                        : "Dano moderado com chance crítica otimizada."
        ));

        habilidades.add(new HabilidadeCombate(
                sequencia <= 7 ? "Mira Incendiária" : "Rastrear Fraqueza",
                "Caçador", 9, sequencia <= 7 ? 12 : 7, false,
                sequencia <= 7
                        ? "Aumenta crítico e agrega dano ígneo à próxima agressão."
                        : "Maximiza precisão e crítico da próxima investida."
        ));

        habilidades.add(new HabilidadeCombate(
                sequencia <= 6 ? "Plano de Emboscada" : "Armadilha Improvisada",
                "Caçador", 9, sequencia <= 6 ? 15 : 9, false,
                sequencia <= 6
                        ? "Dano, chance de Imobilizado e Fragilizado."
                        : "Dano leve com chance de Imobilizado."
        ));

        habilidades.add(new HabilidadeCombate(
                sequencia <= 5 ? "Sentença do Predador" : "Grito de Caça",
                "Caçador", 9, sequencia <= 5 ? 17 : 9, false,
                sequencia <= 5
                        ? "Aplica Sentenciado; alvos debilitados ficam expostos à execução."
                        : "Aplica Provocado, aumentando ataque e reduzindo defesa do alvo."
        ));

        if (sequencia <= 5) {
            habilidades.add(new HabilidadeCombate(
                    "Ceifa do Ciclo", "Caçador", 5, 38, true,
                    "Dano extremo contra alvo exausto; se matar, restaura vida e concede novo turno."
            ));
        }
    }

    private static void adicionarBardo(List<HabilidadeCombate> habilidades, int sequencia) {
        habilidades.add(new HabilidadeCombate(
                sequencia <= 8 ? "Nota Purificadora" : "Nota Dilacerante",
                "Bardo", 9, sequencia <= 8 ? 11 : 8, false,
                sequencia <= 8
                        ? "Dano moderado com eficácia ampliada contra corrompidos."
                        : "Dano variável com chance de aplicar Atordoado."
        ));

        habilidades.add(new HabilidadeCombate(
                sequencia <= 7 ? "Cântico Solar" : "Canção Revigorante",
                "Bardo", 9, sequencia <= 7 ? 14 : 9, false,
                sequencia <= 7
                        ? "Cura moderada e remove máculas elementares."
                        : "Restaura vida e Sanidade."
        ));

        habilidades.add(new HabilidadeCombate(
                sequencia <= 6 ? "Decreto Sagrado" : "Verso Desmoralizante",
                "Bardo", 9, sequencia <= 6 ? 15 : 9, false,
                sequencia <= 6
                        ? "Reduz ataque e aplica Decretado, bloqueando habilidades especiais por um turno."
                        : "Reduz dano ou precisão do alvo."
        ));

        habilidades.add(new HabilidadeCombate(
                sequencia <= 5 ? "Juramento da Primeira Chama" : "Hino de Coragem",
                "Bardo", 9, sequencia <= 5 ? 18 : 10, false,
                sequencia <= 5
                        ? "Maximiza defesa, protege a mente e reduz custo de Sanidade."
                        : "Protege contra pavor e delírio."
        ));

        if (sequencia <= 5) {
            habilidades.add(new HabilidadeCombate(
                    "Sol Absoluto", "Bardo", 5, 44, true,
                    "Dano massivo de luz purificadora e restauração vital."
            ));
        }
    }
}
