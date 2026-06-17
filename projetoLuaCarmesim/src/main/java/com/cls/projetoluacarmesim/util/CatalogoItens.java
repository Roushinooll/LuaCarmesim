package com.cls.projetoluacarmesim.util;

import com.cls.projetoluacarmesim.enums.TipoItem;
import com.cls.projetoluacarmesim.model.ItemEspecial;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public final class CatalogoItens {

    public enum Raridade {
        COMUM,
        INCOMUM,
        RARO,
        EPICO,
        LENDARIO,
        MITICO
    }

    public static final class EntradaItem {
        private final String nome;
        private final String descricao;
        private final Raridade raridade;
        private final int precoBase;

        public EntradaItem(String nome, String descricao, Raridade raridade, int precoBase) {
            this.nome = nome;
            this.descricao = descricao;
            this.raridade = raridade;
            this.precoBase = precoBase;
        }

        public String getNome() {
            return nome;
        }

        public String getDescricao() {
            return descricao;
        }

        public Raridade getRaridade() {
            return raridade;
        }

        public int getPrecoBase() {
            return precoBase;
        }

        public ItemEspecial criarItem() {
            return new ItemEspecial(
                    0,
                    nome,
                    TipoItem.INGREDIENTE,
                    descricao + " | Raridade: " + nomeBonitoRaridade(raridade),
                    false
            );
        }
    }

    private static final Random RANDOM = new Random();
    private static final List<EntradaItem> ITENS = new ArrayList<>();
    private static final Map<String, EntradaItem> POR_NOME = new HashMap<>();

    static {
        adicionar("Erva de Névoa Prateada", "Ingrediente comum do Caminho do Vidente.", Raridade.COMUM, 4);
        adicionar("Fragmento de Vidro Manchado", "Vidro ritualístico encontrado em becos e janelas quebradas.", Raridade.COMUM, 4);
        adicionar("Sangue Coagulado", "Fluido comum usado em fórmulas violentas.", Raridade.COMUM, 4);
        adicionar("Faca Enferrujada", "Lâmina degradada aproveitada em alquimia criminosa.", Raridade.COMUM, 4);
        adicionar("Garra de Cão Mutado", "Garra comum de fera urbana.", Raridade.COMUM, 4);
        adicionar("Ponta de Flecha Enferrujada", "Resíduo bélico usado em poções de caça.", Raridade.COMUM, 4);
        adicionar("Corda de Violino Rompida", "Corda comum imersa em ecos antigos.", Raridade.COMUM, 4);
        adicionar("Página de Canção Antiga", "Página comum de cânticos esquecidos.", Raridade.COMUM, 4);

        adicionar("Olho Seco de Corvo Urbano", "Parte incomum usada para ampliar percepção espiritual.", Raridade.INCOMUM, 8);
        adicionar("Orvalho da Meia-Noite", "Líquido incomum condensado sob a Lua Carmesim.", Raridade.INCOMUM, 8);
        adicionar("Dente de Rato Gigante", "Dente incomum retirado de praga urbana.", Raridade.INCOMUM, 8);
        adicionar("Moeda Roubada de Cadáver", "Objeto incomum marcado por violência e pilhagem.", Raridade.INCOMUM, 8);
        adicionar("Olho de Coruja Noturna", "Olho incomum usado em rastreios e caçadas.", Raridade.INCOMUM, 8);
        adicionar("Sangue de Presa", "Sangue incomum de criatura acuada.", Raridade.INCOMUM, 8);
        adicionar("Gota de Mel Dourado", "Substância incomum usada em ritos sonoros.", Raridade.INCOMUM, 8);
        adicionar("Pedaço de Sino Quebrado", "Fragmento incomum com ressonância espiritual.", Raridade.INCOMUM, 8);

        adicionar("Máscara Rachada de Artista de Rua", "Artefato raro ligado a disfarce e caos.", Raridade.RARO, 16);
        adicionar("Pena Negra Queimada", "Pena rara marcada por hostilidade absoluta.", Raridade.RARO, 16);
        adicionar("Lâmina Marcada por Assassinato", "Lâmina rara com memória de abate.", Raridade.RARO, 16);
        adicionar("Língua de Bandido Insultador", "Ingrediente raro usado em provocações místicas.", Raridade.RARO, 16);
        adicionar("Óleo Carmesim", "Óleo raro de combustão instável.", Raridade.RARO, 16);
        adicionar("Vela de Cera Sagrada", "Vela rara usada em ritos de purificação.", Raridade.RARO, 16);
        adicionar("Fragmento de Vitral Iluminado", "Vidro raro com brilho solar aprisionado.", Raridade.RARO, 16);

        adicionar("Carta de Tarô Queimada", "Relíquia épica carregada de presságios partidos.", Raridade.EPICO, 28);
        adicionar("Lágrima de Espelho Vivo", "Substância épica extraída de reflexos instáveis.", Raridade.EPICO, 28);
        adicionar("Diário de Assassinato Incompleto", "Registro épico de impulsos homicidas.", Raridade.EPICO, 28);
        adicionar("Sangue Negro", "Fluido épico de corrupção profunda.", Raridade.EPICO, 28);
        adicionar("Glândula de Fogo Interno", "Órgão épico que pulsa calor impossível.", Raridade.EPICO, 28);
        adicionar("Olho de Espião Morto", "Olho épico ainda atento após a morte.", Raridade.EPICO, 28);
        adicionar("Núcleo de Luz Engarrafada", "Luz épica contida em invólucro ritualístico.", Raridade.EPICO, 28);
        adicionar("Selo de Cera Mística", "Selo épico usado em decretos arcanos.", Raridade.EPICO, 28);

        adicionar("Pele Preservada de Doppelgänger", "Material lendário de mimetismo perfeito.", Raridade.LENDARIO, 45);
        adicionar("Coração de Marionete Humana", "Coração lendário de vontade aprisionada.", Raridade.LENDARIO, 45);
        adicionar("Chifre de Demônio Urbano", "Fragmento lendário de corrupção demoníaca.", Raridade.LENDARIO, 45);
        adicionar("Coração Pulsante de Desejo", "Órgão lendário de paixão corrompida.", Raridade.LENDARIO, 45);
        adicionar("Mapa Vivo da Cidade", "Mapa lendário que se reescreve sozinho.", Raridade.LENDARIO, 45);
        adicionar("Foice Ritualística Banhada em Sangue", "Arma lendária de execução ritual.", Raridade.LENDARIO, 45);
        adicionar("Contrato Assinado com Sangue", "Contrato lendário com força de dogma místico.", Raridade.LENDARIO, 45);
        adicionar("Coração de Santo Falso", "Relíquia lendária de luz corrompida.", Raridade.LENDARIO, 45);

        adicionar("Fio de Prata Lunar", "Substância mítica ligada aos fios da Lua Carmesim.", Raridade.MITICO, 75);
        adicionar("Fragmento de Tentação Carmesim", "Fragmento mítico de desejo absoluto.", Raridade.MITICO, 75);
        adicionar("Fragmento da Sombra da Morte", "Fragmento mítico capaz de encerrar ciclos.", Raridade.MITICO, 75);
        adicionar("Fragmento da Primeira Chama Solar", "Fragmento mítico de purificação terminal.", Raridade.MITICO, 75);
    }

    private CatalogoItens() {
    }

    private static void adicionar(String nome, String descricao, Raridade raridade, int precoBase) {
        EntradaItem entrada = new EntradaItem(nome, descricao, raridade, precoBase);
        ITENS.add(entrada);
        POR_NOME.put(nome.toLowerCase(), entrada);
    }

    public static EntradaItem buscarPorNome(String nome) {
        if (nome == null) {
            return null;
        }
        return POR_NOME.get(nome.toLowerCase());
    }

    public static String nomeBonitoRaridade(Raridade raridade) {
        switch (raridade) {
            case COMUM:
                return "Comum";
            case INCOMUM:
                return "Incomum";
            case RARO:
                return "Raro";
            case EPICO:
                return "Épico";
            case LENDARIO:
                return "Lendário";
            case MITICO:
                return "Mítico";
            default:
                return "Comum";
        }
    }

    public static EntradaItem sortearItemDeChao() {
        double sorteio = RANDOM.nextDouble();
        Raridade raridade = sorteio < 0.75 ? Raridade.COMUM : Raridade.INCOMUM;
        return sortearPorRaridadeMaxima(raridade);
    }

    public static EntradaItem sortearDropBandido() {
        double sorteio = RANDOM.nextDouble();
        Raridade raridade = sorteio < 0.82 ? Raridade.COMUM : Raridade.INCOMUM;
        return sortearPorRaridadeMaxima(raridade);
    }

    public static EntradaItem sortearDropBeyonder(int sequencia) {
        Raridade raridadeMaxima = raridadeMaximaPorSequencia(sequencia);
        double sorteio = RANDOM.nextDouble();
        Raridade escolhida;

        switch (raridadeMaxima) {
            case RARO:
                escolhida = sorteio < 0.45 ? Raridade.INCOMUM : Raridade.RARO;
                break;
            case EPICO:
                escolhida = sorteio < 0.30 ? Raridade.RARO : Raridade.EPICO;
                break;
            case LENDARIO:
                if (sorteio < 0.22) {
                    escolhida = Raridade.RARO;
                } else if (sorteio < 0.72) {
                    escolhida = Raridade.EPICO;
                } else {
                    escolhida = Raridade.LENDARIO;
                }
                break;
            case MITICO:
                if (sorteio < 0.15) {
                    escolhida = Raridade.EPICO;
                } else if (sorteio < 0.72) {
                    escolhida = Raridade.LENDARIO;
                } else {
                    escolhida = Raridade.MITICO;
                }
                break;
            default:
                escolhida = Raridade.INCOMUM;
                break;
        }

        return sortearPorRaridadeExata(escolhida);
    }

    public static Raridade raridadeMaximaPorSequencia(int sequencia) {
        if (sequencia <= 5) {
            return Raridade.MITICO;
        }
        if (sequencia == 6) {
            return Raridade.LENDARIO;
        }
        if (sequencia == 7) {
            return Raridade.EPICO;
        }
        return Raridade.RARO;
    }

    public static Raridade raridadeMaximaParaMercador(int numeroRua) {
        if (numeroRua >= 50) {
            return Raridade.MITICO;
        }
        if (numeroRua >= 40) {
            return Raridade.LENDARIO;
        }
        if (numeroRua >= 30) {
            return Raridade.EPICO;
        }
        if (numeroRua >= 20) {
            return Raridade.RARO;
        }
        return Raridade.INCOMUM;
    }

    public static List<EntradaItem> listarParaMercador(int numeroRua, int quantidade) {
        Raridade max = raridadeMaximaParaMercador(numeroRua);
        List<EntradaItem> disponiveis = new ArrayList<>();

        for (EntradaItem item : ITENS) {
            if (item.getRaridade().ordinal() <= max.ordinal()) {
                disponiveis.add(item);
            }
        }

        Collections.shuffle(disponiveis, RANDOM);
        if (disponiveis.size() <= quantidade) {
            return disponiveis;
        }
        return new ArrayList<>(disponiveis.subList(0, quantidade));
    }

    public static EntradaItem sortearPorNomeOuBasico(String nome) {
        EntradaItem entrada = buscarPorNome(nome);
        if (entrada != null) {
            return entrada;
        }
        return sortearItemDeChao();
    }

    private static EntradaItem sortearPorRaridadeMaxima(Raridade maxima) {
        List<EntradaItem> opcoes = new ArrayList<>();

        for (EntradaItem item : ITENS) {
            if (item.getRaridade().ordinal() <= maxima.ordinal()) {
                opcoes.add(item);
            }
        }

        if (opcoes.isEmpty()) {
            return ITENS.get(0);
        }

        return opcoes.get(RANDOM.nextInt(opcoes.size()));
    }

    private static EntradaItem sortearPorRaridadeExata(Raridade raridade) {
        List<EntradaItem> opcoes = new ArrayList<>();

        for (EntradaItem item : ITENS) {
            if (item.getRaridade() == raridade) {
                opcoes.add(item);
            }
        }

        if (opcoes.isEmpty()) {
            return sortearPorRaridadeMaxima(raridade);
        }

        return opcoes.get(RANDOM.nextInt(opcoes.size()));
    }
}
