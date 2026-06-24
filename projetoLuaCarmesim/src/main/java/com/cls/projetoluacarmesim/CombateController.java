package com.cls.projetoluacarmesim;

import com.cls.projetoluacarmesim.combate.CombatenteMistico;
import com.cls.projetoluacarmesim.combate.ExecutorHabilidades;
import com.cls.projetoluacarmesim.combate.HabilidadeCatalogo;
import com.cls.projetoluacarmesim.combate.HabilidadeCombate;
import com.cls.projetoluacarmesim.combate.ResultadoHabilidade;
import com.cls.projetoluacarmesim.model.Bandido;
import com.cls.projetoluacarmesim.model.Beyonder;
import com.cls.projetoluacarmesim.model.Boss;
import com.cls.projetoluacarmesim.model.Inimigo;
import com.cls.projetoluacarmesim.model.Jogador;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class CombateController {

    private static final int VIDA_MAXIMA_JOGADOR_TESTE = 120;
    private static final int SANIDADE_MAXIMA_JOGADOR_TESTE = 110;
    private static final int MUNICAO_MAXIMA_REVOLVER = 6;
    private static final int DANO_ATAQUE_FISICO = 14;
    private static final int DANO_POR_TIRO = 24;
    private static final String NOME_REVOLVER = "Revólver Enferrujado";

    @FXML private Label labelTitulo;
    @FXML private Label labelTurno;
    @FXML private Label labelJogador;
    @FXML private Label labelInimigo;
    @FXML private Label labelAcoes;
    @FXML private Label labelMunicao;
    @FXML private TextArea logCombate;
    @FXML private ImageView imagemFundoBatalha;
    @FXML private ImageView spriteJogadorBatalha;
    @FXML private ImageView spriteInimigoBatalha;
    @FXML private ProgressBar barraVidaJogador;
    @FXML private ProgressBar barraVidaInimigo;
    @FXML private ProgressBar barraSanidadeJogador;
    @FXML private ProgressBar barraSanidadeInimigo;

    @FXML private VBox menuAcoesPrincipais;
    @FXML private VBox menuHabilidadesCombate;

    @FXML private Button botaoAtacar;
    @FXML private Button botaoAtirar;
    @FXML private Button botaoDefender;
    @FXML private Button botaoConversar;
    @FXML private Button botaoAbrirHabilidades;
    @FXML private Button botaoVoltarAcoes;
    @FXML private Button botaoFugir;
    @FXML private VBox boxBotoesHabilidades;

    private Parent telaAnterior;
    private Inimigo inimigo;
    private Runnable aoVencer;
    private Runnable aoFugir;

    private int turnoAtual;
    private int vidaJogador;
    private int vidaMaximaJogador;
    private int municoesRevolver;
    private boolean possuiRevolver;
    private boolean acaoDisponivel;
    private boolean sequenciaTirosEmAndamento;
    private boolean combateFinalizado;

    private CombatenteMistico jogadorCombate;
    private CombatenteMistico inimigoCombate;
    private final ExecutorHabilidades executorHabilidades = new ExecutorHabilidades();
    private final Random random = new Random();
    private List<HabilidadeCombate> habilidadesJogador = new ArrayList<>();
    private List<HabilidadeCombate> habilidadesInimigo = new ArrayList<>();
    private final List<Button> botoesHabilidades = new ArrayList<>();

    public static void abrirCombate(Inimigo inimigo, Runnable aoVencer, Runnable aoFugir) throws IOException {
        Parent telaAnterior = App.getScene().getRoot();

        Object controller = App.setRoot("combate");

        if (controller instanceof CombateController) {
            CombateController combateController = (CombateController) controller;
            combateController.configurar(telaAnterior, inimigo, aoVencer, aoFugir);
        }
    }

    private void configurar(Parent telaAnterior, Inimigo inimigo, Runnable aoVencer, Runnable aoFugir) {
        this.telaAnterior = telaAnterior;
        this.inimigo = inimigo;
        this.aoVencer = aoVencer;
        this.aoFugir = aoFugir;

        this.turnoAtual = 1;
        this.vidaMaximaJogador = VIDA_MAXIMA_JOGADOR_TESTE;
        this.vidaJogador = vidaMaximaJogador;
        this.municoesRevolver = MUNICAO_MAXIMA_REVOLVER;
        this.possuiRevolver = EstadoJogo.getInstance().getInventario().possuiItem(NOME_REVOLVER);
        this.acaoDisponivel = true;
        this.sequenciaTirosEmAndamento = false;
        this.combateFinalizado = false;

        criarCombatentesMisticos();
        carregarHabilidadesJogador();
        carregarHabilidadesInimigo();

        labelTitulo.setText("Combate contra " + nomeInimigo());
        configurarVisualBatalha();

        adicionarLog("Um " + nomeInimigo() + " bloqueia seu caminho.");
        adicionarLog("Sistema atual: uma ação por turno, no estilo RPG de turno.");
        adicionarLog("Ataque físico, defesa, conversa e habilidade consomem a ação do turno.");
        adicionarLog("O revólver agora permite disparar o tambor inteiro na mesma rodada antes do inimigo agir.");

        if (!habilidadesJogador.isEmpty()) {
            adicionarLog("Você está no " + HabilidadeCatalogo.nomeCaminhoParaExibicao(jogadorCombate.getCaminho())
                    + ", Sequência " + jogadorCombate.getSequencia() + ". Habilidades disponíveis: "
                    + habilidadesJogador.size() + ".");
        } else {
            adicionarLog("Você ainda não possui habilidades de caminho. Beba uma poção de Sequência 9 para despertar poderes.");
        }

        if (!habilidadesInimigo.isEmpty()) {
            adicionarLog(nomeInimigo() + " também é um Beyonder de "
                    + HabilidadeCatalogo.nomeCaminhoParaExibicao(inimigoCombate.getCaminho())
                    + ", Sequência " + inimigoCombate.getSequencia() + ".");
        }

        if (possuiRevolver) {
            adicionarLog("Seu revólver foi recarregado para " + MUNICAO_MAXIMA_REVOLVER + " balas no início da luta.");
        } else {
            adicionarLog("Você não está com o revólver no inventário. Apenas ataques físicos estarão disponíveis.");
        }

        mostrarMenuAcoesPrincipais();
        atualizarTela();
    }

    private void criarCombatentesMisticos() {
        Jogador jogador = EstadoJogo.getInstance().getJogadorAtual();

        String caminhoJogador = jogador != null ? jogador.getCaminhoAtual() : null;
        int sequenciaJogador = jogador != null ? jogador.getSequenciaAtual() : 10;
        int sanidadeMaximaJogador = jogador != null ? jogador.getSanidadeMaxima() : SANIDADE_MAXIMA_JOGADOR_TESTE;
        int sanidadeAtualJogador = jogador != null ? jogador.getSanidadeAtual() : sanidadeMaximaJogador;

        this.jogadorCombate = new CombatenteMistico(
                "Você",
                true,
                caminhoJogador,
                sequenciaJogador,
                vidaMaximaJogador,
                vidaJogador,
                sanidadeMaximaJogador,
                sanidadeAtualJogador,
                DANO_ATAQUE_FISICO,
                0
        );

        String caminhoInimigo = null;
        int sequenciaInimigo = 10;
        int sanidadeMaximaInimigo = 1;
        int sanidadeAtualInimigo = 0;

        if (inimigo instanceof Boss) {
            Boss boss = (Boss) inimigo;
            boss.atualizarFasePorVida();
            sequenciaInimigo = 5;
            caminhoInimigo = boss.getCaminhoAtual();
            sanidadeMaximaInimigo = boss.getSanidadeMaxima();
            sanidadeAtualInimigo = boss.getSanidadeAtual();
        } else if (inimigo instanceof Beyonder) {
            Beyonder beyonder = (Beyonder) inimigo;
            sequenciaInimigo = beyonder.getSequencia();
            caminhoInimigo = HabilidadeCatalogo.identificarCaminhoPorPocao(beyonder.getNomePocao());
            sanidadeMaximaInimigo = beyonder.getSanidadeMaxima();
            sanidadeAtualInimigo = beyonder.getSanidadeAtual();
        }

        this.inimigoCombate = new CombatenteMistico(
                nomeInimigo(),
                false,
                caminhoInimigo,
                sequenciaInimigo,
                inimigo.getVidaMaxima(),
                inimigo.getVidaAtual(),
                sanidadeMaximaInimigo,
                sanidadeAtualInimigo,
                inimigo.getDanoCorpoACorpo() + inimigo.getDanoArma(),
                inimigo.getDefesa()
        );
    }


    private void configurarVisualBatalha() {
        if (imagemFundoBatalha != null) {
            imagemFundoBatalha.setImage(carregarImagem(
                    "/image/battle_sprites/background.jpg",
                    "/image/battle_sprites/background.png",
                    "/image/Battle Sprite/background.jpg",
                    "/image/Battle Sprite/background.png"
            ));
        }

        if (spriteJogadorBatalha != null) {
            spriteJogadorBatalha.setImage(carregarImagem(
                    "/image/battle_sprites/chars/klein_pose.png",
                    "/image/Battle Sprite/chars/klein_pose.png",
                    "/image/Battle Sprite/Klein.png",
                    "/image/battle_sprites/Klein.png",
                    "/image/sprites/KleinGeral/rotations/south.png",
                    "/image/sprites/KleinGeral/rotations/Lados.png"
            ));
            spriteJogadorBatalha.setFitWidth(235.0);
            spriteJogadorBatalha.setFitHeight(235.0);
            spriteJogadorBatalha.setLayoutX(220.0);
            spriteJogadorBatalha.setLayoutY(158.0);
            spriteJogadorBatalha.setScaleX(1);
            spriteJogadorBatalha.setSmooth(false);
        }

        if (spriteInimigoBatalha != null) {
            spriteInimigoBatalha.setImage(carregarImagem(caminhosSpriteInimigo()));
            spriteInimigoBatalha.setSmooth(false);

            if (inimigo instanceof Boss) {
                spriteInimigoBatalha.setScaleX(1);
                spriteInimigoBatalha.setFitWidth(300.0);
                spriteInimigoBatalha.setFitHeight(300.0);
                spriteInimigoBatalha.setLayoutX(875.0);
                spriteInimigoBatalha.setLayoutY(46.0);
            } else {
                spriteInimigoBatalha.setScaleX(-1);
                spriteInimigoBatalha.setFitWidth(176.0);
                spriteInimigoBatalha.setFitHeight(176.0);
                spriteInimigoBatalha.setLayoutX(962.0);
                spriteInimigoBatalha.setLayoutY(126.0);
            }
        }
    }

    private Image carregarImagem(String... caminhos) {
        if (caminhos == null) {
            return null;
        }

        for (String caminho : caminhos) {
            if (caminho == null || caminho.isBlank()) {
                continue;
            }

            try {
                var stream = getClass().getResourceAsStream(caminho);
                if (stream != null) {
                    return new Image(stream);
                }
            } catch (Exception ignored) {
            }
        }

        return null;
    }

    private String[] caminhosSpriteInimigo() {
        if (inimigo instanceof Boss) {
            return new String[] {
                    "/image/battle_sprites/chars/boss_pose.png",
                    "/image/Battle Sprite/chars/boss_pose.png",
                    "/image/Battle Sprite/Boss.png",
                    "/image/battle_sprites/Boss.png",
                    "/image/sprites/BossGeral/idle_sprite.png",
                    "/image/sprites/Beyonder2Geral/rotations/Lados.png",
                    "/image/sprites/Beyonder1Geral/rotations/Lados.png",
                    "/image/sprites/Bandido1Geral/rotations/Lados.png"
            };
        }

        if (inimigo instanceof Beyonder) {
            return new String[] {
                    Math.random() < 0.5
                            ? "/image/battle_sprites/chars/beyonder1_pose.png"
                            : "/image/battle_sprites/chars/beyonder2_pose.png",
                    "/image/Battle Sprite/chars/beyonder1_pose.png",
                    "/image/Battle Sprite/chars/beyonder2_pose.png",
                    "/image/sprites/Beyonder1Geral/rotations/Lados.png",
                    "/image/sprites/Beyonder2Geral/rotations/Lados.png",
                    "/image/sprites/Bandido1Geral/rotations/Lados.png"
            };
        }

        if (inimigo instanceof Bandido) {
            return new String[] {
                    Math.random() < 0.5
                            ? "/image/battle_sprites/chars/bandido1_pose.png"
                            : "/image/battle_sprites/chars/bandido2_pose.png",
                    "/image/Battle Sprite/chars/bandido1_pose.png",
                    "/image/Battle Sprite/chars/bandido2_pose.png",
                    "/image/sprites/Bandido1Geral/rotations/Lados.png",
                    "/image/sprites/Bandido2Geral/rotations/lados.png",
                    "/image/sprites/Bandido2Geral/rotations/Lados.png",
                    "/image/sprites/Beyonder1Geral/rotations/Lados.png"
            };
        }

        return new String[] {
                "/image/sprites/Bandido1Geral/rotations/Lados.png",
                "/image/sprites/Beyonder1Geral/rotations/Lados.png"
        };
    }

    private void carregarHabilidadesJogador() {
        habilidadesJogador = HabilidadeCatalogo.listarPorProgressao(
                jogadorCombate.getCaminho(),
                jogadorCombate.getSequencia()
        );

        montarBotoesHabilidades();
    }

    private void montarBotoesHabilidades() {
        botoesHabilidades.clear();

        if (boxBotoesHabilidades == null) {
            return;
        }

        boxBotoesHabilidades.getChildren().clear();

        for (HabilidadeCombate habilidade : habilidadesJogador) {
            Button botao = new Button(habilidade.getTextoMenu());
            botao.setPrefWidth(300.0);
            botao.setMinHeight(34.0);
            botao.setPrefHeight(38.0);
            botao.setWrapText(true);
            botao.setStyle("-fx-font-size: 13px; -fx-font-family: Georgia; -fx-alignment: center; -fx-text-alignment: center;");
            botao.setOnAction(event -> usarHabilidadeSelecionada(habilidade));

            botoesHabilidades.add(botao);
            boxBotoesHabilidades.getChildren().add(botao);
        }
    }

    private void carregarHabilidadesInimigo() {
        habilidadesInimigo = HabilidadeCatalogo.listarPorProgressao(
                inimigoCombate.getCaminho(),
                inimigoCombate.getSequencia()
        );
    }

    @FXML
    private void atacarFisico() {
        if (!podeAgir()) {
            return;
        }

        int dano = executorHabilidades.atacarFisicamente(
                jogadorCombate,
                inimigoCombate,
                DANO_ATAQUE_FISICO,
                true
        );

        acaoDisponivel = false;

        if (dano <= 0) {
            adicionarLog("Você tenta atacar fisicamente, mas " + nomeInimigo() + " evade a investida.");
        } else {
            adicionarLog("Você usa um ataque físico e causa " + dano + " de dano.");
            refletirDanoSeNecessario(inimigoCombate, jogadorCombate, dano);
        }

        sincronizarEstadoCombate();

        if (jogadorFoiDerrotado()) {
            finalizarPorMorte();
            return;
        }

        if (!inimigoCombate.estaVivo()) {
            finalizarPorVitoria();
            return;
        }

        jogadorCombate.tickFimDoTurno();
        iniciarTurnoInimigo();
    }

    @FXML
    private void atirarRevolver() {
        if (combateFinalizado) {
            return;
        }

        if (!acaoDisponivel && !sequenciaTirosEmAndamento) {
            adicionarLog("A ação deste turno já foi usada.");
            atualizarTela();
            return;
        }

        if (!possuiRevolver) {
            adicionarLog("Você precisa estar com o revólver para atirar.");
            atualizarTela();
            return;
        }

        if (municoesRevolver <= 0) {
            adicionarLog("O tambor está vazio.");
            encerrarSequenciaDeTiros();
            return;
        }

        if (!sequenciaTirosEmAndamento) {
            sequenciaTirosEmAndamento = true;
            acaoDisponivel = false;
            adicionarLog("Você inicia uma sequência de tiros. Pode continuar disparando até esvaziar o tambor.");
        }

        municoesRevolver--;

        int dano = executorHabilidades.atacarFisicamente(
                jogadorCombate,
                inimigoCombate,
                DANO_POR_TIRO,
                true
        );

        if (dano <= 0) {
            adicionarLog("Você dispara 1 bala do revólver, mas " + nomeInimigo() + " evade o tiro.");
        } else {
            adicionarLog("Você dispara 1 bala do revólver e causa " + dano + " de dano.");
            refletirDanoSeNecessario(inimigoCombate, jogadorCombate, dano);
        }

        sincronizarEstadoCombate();

        if (jogadorFoiDerrotado()) {
            sequenciaTirosEmAndamento = false;
            finalizarPorMorte();
            return;
        }

        if (!inimigoCombate.estaVivo()) {
            sequenciaTirosEmAndamento = false;
            finalizarPorVitoria();
            return;
        }

        if (municoesRevolver <= 0) {
            adicionarLog("O tambor esvaziou. A rodada de tiros termina.");
            encerrarSequenciaDeTiros();
            return;
        }

        adicionarLog("Balas restantes no tambor: " + municoesRevolver + ".");
        atualizarTela();
    }

    private void encerrarSequenciaDeTiros() {
        sequenciaTirosEmAndamento = false;
        acaoDisponivel = false;
        sincronizarEstadoCombate();

        if (jogadorFoiDerrotado()) {
            finalizarPorMorte();
            return;
        }

        if (!inimigoCombate.estaVivo()) {
            finalizarPorVitoria();
            return;
        }

        jogadorCombate.tickFimDoTurno();
        iniciarTurnoInimigo();
    }

    @FXML
    private void defender() {
        if (!podeAgir()) {
            return;
        }

        jogadorCombate.configurarDefesa(1, 60);
        acaoDisponivel = false;

        adicionarLog("Você assume postura defensiva. O próximo dano será reduzido.");
        sincronizarEstadoCombate();
        jogadorCombate.tickFimDoTurno();
        iniciarTurnoInimigo();
    }

    @FXML
    private void conversar() {
        if (!podeAgir()) {
            return;
        }

        int carismaTemporario = 45;
        String resultado = inimigo.tentarPersuadir(carismaTemporario);
        acaoDisponivel = false;

        adicionarLog(resultado);

        if (resultado.toLowerCase().contains("amedrontado")) {
            inimigoCombate.adicionarStatus(com.cls.projetoluacarmesim.combate.TipoStatusCombate.AMEDRONTADO, 2);
        }

        if (resultado.toLowerCase().contains("desistindo")) {
            finalizarPorVitoria();
            return;
        }

        jogadorCombate.tickFimDoTurno();
        iniciarTurnoInimigo();
    }

    @FXML
    private void abrirMenuHabilidades() {
        if (combateFinalizado) {
            return;
        }

        if (habilidadesJogador == null || habilidadesJogador.isEmpty()) {
            adicionarLog("Você ainda não possui habilidades de Caminho disponíveis.");
            atualizarTela();
            return;
        }

        if (jogadorCombate == null || !jogadorCombate.podeUsarHabilidadeEspecial()) {
            adicionarLog("Você não consegue usar habilidades agora.");
            atualizarTela();
            return;
        }

        mostrarMenuHabilidades();
        atualizarTela();
    }

    @FXML
    private void voltarMenuAcoes() {
        mostrarMenuAcoesPrincipais();
        atualizarTela();
    }

    private void usarHabilidadeSelecionada(HabilidadeCombate habilidade) {
        if (!podeAgir()) {
            return;
        }

        if (habilidade == null) {
            adicionarLog("Escolha uma habilidade antes de agir.");
            atualizarTela();
            return;
        }

        ResultadoHabilidade resultado = executorHabilidades.usar(habilidade, jogadorCombate, inimigoCombate);
        registrarResultado(resultado);

        if (!resultado.isSucesso()) {
            sincronizarEstadoCombate();
            atualizarTela();
            return;
        }

        acaoDisponivel = false;
        mostrarMenuAcoesPrincipais();
        sincronizarEstadoCombate();

        if (jogadorFoiDerrotado()) {
            finalizarPorMorte();
            return;
        }

        if (!inimigoCombate.estaVivo()) {
            finalizarPorVitoria();
            return;
        }

        jogadorCombate.tickFimDoTurno();
        iniciarTurnoInimigo();
    }

    @FXML
    private void fugir() {
        finalizarPorFuga();
    }

    private boolean podeAgir() {
        if (combateFinalizado) {
            return false;
        }

        if (!acaoDisponivel) {
            adicionarLog("A ação deste turno já foi usada.");
            return false;
        }

        return true;
    }

    private void iniciarTurnoInimigo() {
        sincronizarEstadoCombate();
        atualizarTela();

        if (combateFinalizado) {
            return;
        }

        adicionarLog("Turno do inimigo.");

        if (!processarInicioTurno(inimigoCombate)) {
            finalizarPorVitoria();
            return;
        }

        if (inimigoCombate.devePerderTurno(random)) {
            adicionarLog(nomeInimigo() + " perde a ação por causa das máculas ativas.");
        } else if (deveInimigoUsarHabilidade()) {
            HabilidadeCombate habilidade = escolherHabilidadeInimigo();
            ResultadoHabilidade resultado = executorHabilidades.usar(habilidade, inimigoCombate, jogadorCombate);
            registrarResultado(resultado);

            if (!resultado.isSucesso()) {
                ataqueBasicoInimigo();
            }
        } else {
            ataqueBasicoInimigo();
        }

        sincronizarEstadoCombate();

        if (jogadorFoiDerrotado()) {
            finalizarPorMorte();
            return;
        }

        inimigoCombate.tickFimDoTurno();
        iniciarTurnoJogador();
    }

    private void iniciarTurnoJogador() {
        turnoAtual++;
        acaoDisponivel = true;

        if (!processarInicioTurno(jogadorCombate)) {
            finalizarPorMorte();
            return;
        }

        if (jogadorCombate.devePerderTurno(random)) {
            adicionarLog("Você perde a ação por causa das máculas ativas.");
            acaoDisponivel = false;
            jogadorCombate.tickFimDoTurno();
            iniciarTurnoInimigo();
            return;
        }

        adicionarLog("Seu turno começou. Escolha uma ação.");
        sincronizarEstadoCombate();
        atualizarTela();
    }

    private void ataqueBasicoInimigo() {
        int danoBase = inimigo.calcularDanoAtaque();
        int dano = executorHabilidades.atacarFisicamente(
                inimigoCombate,
                jogadorCombate,
                danoBase,
                true
        );

        if (dano <= 0) {
            adicionarLog(nomeInimigo() + " ataca, mas você evade a investida.");
        } else {
            adicionarLog(nomeInimigo() + " ataca e causa " + dano + " de dano.");
            refletirDanoSeNecessario(jogadorCombate, inimigoCombate, dano);
        }
    }

    private boolean processarInicioTurno(CombatenteMistico combatente) {
        int danoSangramento = combatente.aplicarSangramento();
        if (danoSangramento > 0) {
            adicionarLog(combatente.getNome() + " sofre " + danoSangramento + " de dano por Sangramento.");
        }

        int danoDesejo = combatente.aplicarDesejoCorrompido(random);
        if (danoDesejo > 0) {
            adicionarLog(combatente.getNome() + " sofre " + danoDesejo + " de dano psíquico por Desejo Corrompido.");
        }

        sincronizarEstadoCombate();
        return combatente.estaVivo();
    }

    private boolean deveInimigoUsarHabilidade() {
        int chance = inimigo instanceof Boss ? 55 : 30;

        return !habilidadesInimigo.isEmpty()
                && inimigoCombate.podeUsarHabilidadeEspecial()
                && random.nextInt(100) < chance;
    }

    private HabilidadeCombate escolherHabilidadeInimigo() {
        List<HabilidadeCombate> disponiveis = new ArrayList<>();

        for (HabilidadeCombate habilidade : habilidadesInimigo) {
            if (inimigoCombate.getSanidadeAtual() >= inimigoCombate.calcularCustoFinal(habilidade.getCustoSanidade())) {
                disponiveis.add(habilidade);
            }
        }

        if (disponiveis.isEmpty()) {
            return null;
        }

        return disponiveis.get(random.nextInt(disponiveis.size()));
    }

    private void registrarResultado(ResultadoHabilidade resultado) {
        if (resultado == null) {
            return;
        }

        for (String mensagem : resultado.getMensagens()) {
            adicionarLog(mensagem);
        }
    }

    private void refletirDanoSeNecessario(CombatenteMistico defensor, CombatenteMistico atacante, int danoRecebido) {
        if (!defensor.possuiReflexao() || !atacante.estaVivo()) {
            return;
        }

        int danoRefletido = defensor.calcularDanoRefletido(danoRecebido);
        int danoEfetivo = atacante.receberDano(danoRefletido, false, random);
        adicionarLog(defensor.getNome() + " reflete " + danoEfetivo + " de dano deletério.");
        sincronizarEstadoCombate();
    }

    private void sincronizarEstadoCombate() {
        if (jogadorCombate != null) {
            vidaJogador = jogadorCombate.getVidaAtual();

            Jogador jogador = EstadoJogo.getInstance().getJogadorAtual();
            if (jogador != null) {
                jogador.setSanidadeAtual(jogadorCombate.getSanidadeAtual());
            }
        }

        if (inimigoCombate != null && inimigo != null) {
            inimigo.setVidaAtual(inimigoCombate.getVidaAtual());

            if (inimigo instanceof Beyonder) {
                ((Beyonder) inimigo).setSanidadeAtual(inimigoCombate.getSanidadeAtual());
            }

            atualizarFaseBossSeNecessario();
        }
    }


    private void atualizarFaseBossSeNecessario() {
        if (!(inimigo instanceof Boss) || inimigoCombate == null) {
            return;
        }

        Boss boss = (Boss) inimigo;
        boolean mudouFase = boss.atualizarFasePorVida();

        inimigoCombate.setCaminho(boss.getCaminhoAtual());
        inimigoCombate.setSequencia(5);

        if (mudouFase && boss.estaVivo()) {
            inimigoCombate.setSanidadeAtual(inimigoCombate.getSanidadeMaxima());
            boss.setSanidadeAtual(boss.getSanidadeMaxima());
            carregarHabilidadesInimigo();
            adicionarLog("O espelho estilhaça a forma do boss. Ele muda para " + boss.getNomeFaseAtual() + ".");
        }
    }

    private void finalizarPorVitoria() {
        combateFinalizado = true;
        sincronizarEstadoCombate();
        adicionarLog("Você venceu o combate.");
        desabilitarBotoes();

        voltarParaTelaAnterior();

        if (aoVencer != null) {
            aoVencer.run();
        }
    }

    private void finalizarPorMorte() {
        if (combateFinalizado) {
            return;
        }

        combateFinalizado = true;
        sincronizarEstadoCombate();
        adicionarLog("Sua vida chegou a 0. A run foi encerrada.");
        adicionarLog("Você perdeu os itens físicos, a poção tomada e retornará ao quarto.");
        desabilitarBotoes();

        EstadoJogo.getInstance().resetarAposMorte();
        mostrarAvisoMorte();
        voltarParaQuartoAposMorte();
    }

    private boolean jogadorFoiDerrotado() {
        return vidaJogador <= 0 || jogadorCombate == null || !jogadorCombate.estaVivo();
    }

    private void mostrarAvisoMorte() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Morte");
        alert.setHeaderText("Você morreu");
        alert.setContentText(
                "A Lua Carmesim reinicia o ciclo.\n\n"
                        + "Você voltou para o quarto.\n"
                        + "Todos os itens da run foram perdidos.\n"
                        + "A poção tomada foi removida, então seu Caminho e sua Sequência foram resetados."
        );
        alert.showAndWait();
    }

    private void voltarParaQuartoAposMorte() {
        try {
            Object controller = App.setRoot("restroom");

            if (controller instanceof RestroomController) {
                RestroomController restroomController = (RestroomController) controller;
                restroomController.startGame(App.getStage().getScene());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void finalizarPorFuga() {
        combateFinalizado = true;
        sincronizarEstadoCombate();
        adicionarLog("Você saiu do combate.");
        desabilitarBotoes();

        voltarParaTelaAnterior();

        if (aoFugir != null) {
            aoFugir.run();
        }
    }

    private void voltarParaTelaAnterior() {
        if (telaAnterior != null && App.getScene() != null) {
            App.getScene().setRoot(telaAnterior);
        }
    }

    private void mostrarMenuAcoesPrincipais() {
        if (menuAcoesPrincipais != null) {
            menuAcoesPrincipais.setVisible(true);
            menuAcoesPrincipais.setManaged(true);
        }

        if (menuHabilidadesCombate != null) {
            menuHabilidadesCombate.setVisible(false);
            menuHabilidadesCombate.setManaged(false);
        }
    }

    private void mostrarMenuHabilidades() {
        if (menuAcoesPrincipais != null) {
            menuAcoesPrincipais.setVisible(false);
            menuAcoesPrincipais.setManaged(false);
        }

        if (menuHabilidadesCombate != null) {
            menuHabilidadesCombate.setVisible(true);
            menuHabilidadesCombate.setManaged(true);
        }
    }

    private boolean menuHabilidadesEstaAberto() {
        return menuHabilidadesCombate != null && menuHabilidadesCombate.isVisible();
    }

    private void atualizarTela() {
        labelTurno.setText("Turno " + turnoAtual);

        String textoJogador = "Jogador | Vida: " + vidaJogador + "/" + vidaMaximaJogador;
        if (jogadorCombate != null) {
            textoJogador += " | Sanidade: " + jogadorCombate.getSanidadeAtual() + "/" + jogadorCombate.getSanidadeMaxima();
            textoJogador += "\n" + HabilidadeCatalogo.nomeCaminhoParaExibicao(jogadorCombate.getCaminho());
            if (jogadorCombate.getSequencia() <= 9) {
                textoJogador += " | Sequência " + jogadorCombate.getSequencia();
            }
            textoJogador += "\nMáculas: " + jogadorCombate.textoStatus();
        }
        labelJogador.setText(textoJogador);
        atualizarBarra(barraVidaJogador, vidaJogador, vidaMaximaJogador);
        if (jogadorCombate != null) {
            atualizarBarra(barraSanidadeJogador, jogadorCombate.getSanidadeAtual(), jogadorCombate.getSanidadeMaxima());
        }

        String textoInimigo = nomeInimigo() + " | Vida: " + inimigo.getVidaAtual() + "/" + inimigo.getVidaMaxima();
        if (inimigoCombate != null && inimigo instanceof Beyonder) {
            textoInimigo += " | Sanidade: " + inimigoCombate.getSanidadeAtual() + "/" + inimigoCombate.getSanidadeMaxima();
            textoInimigo += "\n" + HabilidadeCatalogo.nomeCaminhoParaExibicao(inimigoCombate.getCaminho())
                    + " | Sequência " + inimigoCombate.getSequencia();
        }
        if (inimigoCombate != null) {
            textoInimigo += "\nMáculas: " + inimigoCombate.textoStatus();
        }
        labelInimigo.setText(textoInimigo);
        atualizarBarra(barraVidaInimigo, inimigo.getVidaAtual(), inimigo.getVidaMaxima());
        if (inimigoCombate != null) {
            atualizarBarra(barraSanidadeInimigo, inimigoCombate.getSanidadeAtual(), inimigoCombate.getSanidadeMaxima());
        }

        if (sequenciaTirosEmAndamento) {
            labelAcoes.setText("Sequência de tiros em andamento");
        } else {
            labelAcoes.setText(acaoDisponivel ? "Ação disponível" : "Ação usada");
        }

        if (labelMunicao != null) {
            if (possuiRevolver) {
                labelMunicao.setText("Revólver: " + municoesRevolver + "/" + MUNICAO_MAXIMA_REVOLVER + " balas");
            } else {
                labelMunicao.setText("Revólver: não equipado");
            }
        }

        boolean podeClicar = !combateFinalizado && acaoDisponivel && !sequenciaTirosEmAndamento;
        boolean podeContinuarTiro = !combateFinalizado && sequenciaTirosEmAndamento && possuiRevolver && municoesRevolver > 0;
        boolean podeAtirar = (podeClicar || podeContinuarTiro) && possuiRevolver && municoesRevolver > 0;
        boolean podeUsarHabilidade = podeClicar
                && habilidadesJogador != null
                && !habilidadesJogador.isEmpty()
                && jogadorCombate != null
                && jogadorCombate.podeUsarHabilidadeEspecial();

        botaoAtacar.setDisable(!podeClicar);
        botaoAtirar.setDisable(!podeAtirar);
        botaoDefender.setDisable(!podeClicar);
        botaoConversar.setDisable(!podeClicar);
        if (botaoAbrirHabilidades != null) {
            botaoAbrirHabilidades.setDisable(!podeUsarHabilidade);
        }
        if (botaoVoltarAcoes != null) {
            botaoVoltarAcoes.setDisable(combateFinalizado);
        }
        atualizarEstadoBotoesHabilidades(podeUsarHabilidade && menuHabilidadesEstaAberto());
        botaoFugir.setDisable(combateFinalizado || sequenciaTirosEmAndamento);
    }


    private void atualizarBarra(ProgressBar barra, int atual, int maximo) {
        if (barra == null) {
            return;
        }

        if (maximo <= 0) {
            barra.setProgress(0.0);
            return;
        }

        double progresso = Math.max(0.0, Math.min(1.0, atual / (double) maximo));
        barra.setProgress(progresso);
    }

    private void atualizarEstadoBotoesHabilidades(boolean habilitar) {
        for (Button botao : botoesHabilidades) {
            botao.setDisable(!habilitar);
        }
    }

    private void adicionarLog(String mensagem) {
        if (logCombate == null) {
            return;
        }

        if (logCombate.getText() == null || logCombate.getText().isBlank()) {
            logCombate.setText(mensagem);
        } else {
            logCombate.appendText("\n" + mensagem);
        }

        logCombate.setScrollTop(Double.MAX_VALUE);
    }

    private void desabilitarBotoes() {
        botaoAtacar.setDisable(true);
        botaoAtirar.setDisable(true);
        botaoDefender.setDisable(true);
        botaoConversar.setDisable(true);
        if (botaoAbrirHabilidades != null) {
            botaoAbrirHabilidades.setDisable(true);
        }
        if (botaoVoltarAcoes != null) {
            botaoVoltarAcoes.setDisable(true);
        }
        atualizarEstadoBotoesHabilidades(false);
        botaoFugir.setDisable(true);
    }

    private String nomeInimigo() {
        if (inimigo == null || inimigo.getNome() == null || inimigo.getNome().isBlank()) {
            return "Inimigo";
        }

        return inimigo.getNome();
    }
}
