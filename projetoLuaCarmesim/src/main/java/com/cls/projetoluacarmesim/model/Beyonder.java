package com.cls.projetoluacarmesim.model;

import com.cls.projetoluacarmesim.enums.TipoInimigo;
import java.util.ArrayList;
import java.util.List;

/**
 * Beyonder — inimigo que tomou uma Poção de Sequência, assim como o jogador.
 * Além de armas e corpo a corpo, possui poderes místicos e sobrenaturais.
 * Mais resistente à persuasão e mais perigoso em combate.
 */
public class Beyonder extends Inimigo {

    private int sequencia;           // nível de poder (0 = mais alto, 10 = iniciante)
    private int sanidadeMaxima;
    private int sanidadeAtual;       // beyonders também sofrem de insanidade
    private List<String> poderesMisticos;   // lista de nomes de poderes disponíveis
    private List<String> poderesSOBRENATURAIS; // poderes mais raros/custosos
    private int custoSanidadePorPoder;      // sanidade gasta ao usar um poder

    private boolean formulaConhecida; // se o jogador pode extrair a receita da poção dele
    private String nomePocao;         // nome da poção/sequência que ele representa

    public Beyonder() {
        super();
        setTipoInimigo(TipoInimigo.BEYONDER);
        this.sequencia = 9;
        this.sanidadeMaxima = 80;
        this.sanidadeAtual = 80;
        this.poderesMisticos = new ArrayList<>();
        this.poderesSOBRENATURAIS = new ArrayList<>();
        this.custoSanidadePorPoder = 10;
        this.formulaConhecida = false;
        setPersuadivel(false);
        setResistenciaDialogo(80); // beyonders são muito mais difíceis de convencer
    }

    public Beyonder(int idInimigo, String nome, String descricao,
                    int vidaMaxima, int danoCorpoACorpo, int danoArma, int defesa,
                    int pontosDeAcao, String itemDropavel, String ingredienteDropavel,
                    int xpConhecimento, boolean persuadivel, int resistenciaDialogo,
                    int sequencia, int sanidadeMaxima, int custoSanidadePorPoder,
                    String nomePocao, boolean formulaConhecida) {
        super(idInimigo, nome, descricao, TipoInimigo.BEYONDER,
              vidaMaxima, danoCorpoACorpo, danoArma, defesa,
              pontosDeAcao, itemDropavel, ingredienteDropavel,
              xpConhecimento, persuadivel, resistenciaDialogo);
        this.sequencia = sequencia;
        this.sanidadeMaxima = sanidadeMaxima;
        this.sanidadeAtual = sanidadeMaxima;
        this.custoSanidadePorPoder = custoSanidadePorPoder;
        this.nomePocao = nomePocao;
        this.formulaConhecida = formulaConhecida;
        this.poderesMisticos = new ArrayList<>();
        this.poderesSOBRENATURAIS = new ArrayList<>();
    }

    /**
     * Dano do Beyonder combina ataque físico com poder místico.
     * Se sua sanidade estiver baixa, os poderes ficam instáveis e podem errar.
     */
    @Override
    public int calcularDanoAtaque() {
        int danoFisico = getDanoCorpoACorpo() + getDanoArma();

        // Poderes só são usados com sanidade suficiente
        if (!poderesMisticos.isEmpty() && sanidadeAtual >= custoSanidadePorPoder) {
            int bonusMistico = (10 - sequencia) * 5; // sequências menores = mais poder
            gastarSanidade(custoSanidadePorPoder);
            return danoFisico + bonusMistico;
        }

        return danoFisico;
    }

    /**
     * Beyonders raramente cedem ao diálogo.
     * Porém, em casos de sequências muito diferentes, podem revelar informações
     * ou até mesmo propor acordos antes de lutar.
     */
    @Override
    public String tentarPersuadir(int carismaJogador) {
        if (!isPersuadivel()) {
            return nome() + " observa você com frieza sobrenatural. \"Palavras não mudam o inevitável.\"";
        }
        if (carismaJogador >= getResistenciaDialogo()) {
            return nome() + " inclina a cabeça, reconhecendo seu poder. "
                    + "\"Você conhece as Sequências. Talvez valha a pena conversar.\" — ele baixa a guarda.";
        } else {
            return nome() + " sorri. \"Impressionante tentar. Mas você ainda não entende o que sou.\"";
        }
    }

    // Gastar sanidade ao usar poderes
    public void gastarSanidade(int custo) {
        this.sanidadeAtual = Math.max(0, this.sanidadeAtual - custo);
    }

    // Adicionar poderes
    public void adicionarPoderMistico(String poder) {
        this.poderesMisticos.add(poder);
    }

    public void adicionarPoderSobrenatural(String poder) {
        this.poderesSOBRENATURAIS.add(poder);
    }

    private String nome() {
        return getNome() != null ? getNome() : "O Beyonder";
    }

    // Getters e Setters específicos de Beyonder
    public int getSequencia() { return sequencia; }
    public void setSequencia(int sequencia) {
        if (sequencia < 0 || sequencia > 10)
            throw new IllegalArgumentException("Sequência deve estar entre 0 e 10.");
        this.sequencia = sequencia;
    }

    public int getSanidadeMaxima() { return sanidadeMaxima; }
    public void setSanidadeMaxima(int sanidadeMaxima) { this.sanidadeMaxima = sanidadeMaxima; }

    public int getSanidadeAtual() { return sanidadeAtual; }
    public void setSanidadeAtual(int sanidadeAtual) {
        if (sanidadeAtual < 0 || sanidadeAtual > sanidadeMaxima)
            throw new IllegalArgumentException("Sanidade fora dos limites permitidos.");
        this.sanidadeAtual = sanidadeAtual;
    }

    public List<String> getPoderesMisticos() { return poderesMisticos; }
    public void setPoderesMisticos(List<String> poderesMisticos) { this.poderesMisticos = poderesMisticos; }

    public List<String> getPoderesSobrenaturais() { return poderesSOBRENATURAIS; }
    public void setPoderesSobrenaturais(List<String> poderesSobrenaturais) { this.poderesSOBRENATURAIS = poderesSobrenaturais; }

    public int getCustoSanidadePorPoder() { return custoSanidadePorPoder; }
    public void setCustoSanidadePorPoder(int custoSanidadePorPoder) { this.custoSanidadePorPoder = custoSanidadePorPoder; }

    public boolean isFormulaConhecida() { return formulaConhecida; }
    public void setFormulaConhecida(boolean formulaConhecida) { this.formulaConhecida = formulaConhecida; }

    public String getNomePocao() { return nomePocao; }
    public void setNomePocao(String nomePocao) { this.nomePocao = nomePocao; }

    @Override
    public String toString() {
        return "Beyonder{id=" + getIdInimigo() + ", nome='" + getNome() + "', sequencia=" + sequencia
                + ", vida=" + getVidaAtual() + "/" + getVidaMaxima()
                + ", sanidade=" + sanidadeAtual + "/" + sanidadeMaxima
                + ", pocao='" + nomePocao + "'}";
    }
}
