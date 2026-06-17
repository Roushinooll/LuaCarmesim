package com.cls.projetoluacarmesim;

import com.cls.projetoluacarmesim.util.GerenciadorAudio;
import com.cls.projetoluacarmesim.util.GerenciadorConfigs;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ConfigsController implements Initializable {

    @FXML private StackPane rootConfigs;
    @FXML private Slider sliderMusica;
    @FXML private Slider sliderSom;
    @FXML private CheckBox checkTelaCheia;
    @FXML private ComboBox<String> comboResolucao;
    @FXML private Button btnVoltar;

    private final GerenciadorConfigs configs = GerenciadorConfigs.getInstance();
    private final GerenciadorAudio   audio   = GerenciadorAudio.getInstance();

    // -------------------------------------------------------
    // INICIALIZAÇÃO — carrega os valores salvos na UI
    // -------------------------------------------------------

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        comboResolucao.getItems().addAll(
            "1280 x 720",
            "1366 x 768",
            "1600 x 900",
            "1920 x 1080"
        );

        // Preenche campos com os valores persistidos
        sliderMusica.setValue(configs.getMusica());
        sliderSom.setValue(configs.getSom());
        checkTelaCheia.setSelected(configs.isTelaCheia());
        comboResolucao.setValue(configs.getResolucao());

        if (btnVoltar != null) {
            String origem = EstadoJogo.getInstance().getTelaAnteriorConfigs();
            btnVoltar.setText("menu".equals(origem) ? "Voltar Para o Menu" : "Voltar Para o Jogo");
        }

        if (rootConfigs != null) {
            rootConfigs.setFocusTraversable(true);
            rootConfigs.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
                if (e.getCode() == KeyCode.ESCAPE || e.getCode() == KeyCode.TAB) {
                    try {
                        voltarParaOrigem();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    e.consume();
                }
            });
        }

        // Atualiza o volume em tempo real enquanto o usuário arrasta
        sliderMusica.valueProperty().addListener((obs, antigo, novo) ->
            audio.setVolumeMusica(novo.doubleValue())
        );

        sliderSom.valueProperty().addListener((obs, antigo, novo) ->
            audio.setVolumeSom(novo.doubleValue())
        );
    }

    // -------------------------------------------------------
    // SALVAR — persiste e aplica imediatamente
    // -------------------------------------------------------

    @FXML
    private void salvar() {
        configs.setMusica(sliderMusica.getValue());
        configs.setSom(sliderSom.getValue());
        configs.setTelaCheia(checkTelaCheia.isSelected());
        configs.setResolucao(comboResolucao.getValue());

        // Aplica volume
        audio.setVolumeMusica(sliderMusica.getValue());
        audio.setVolumeSom(sliderSom.getValue());

        // Aplica tela cheia
        Stage stage = App.getStage();
        stage.setFullScreen(checkTelaCheia.isSelected());

        // Aplica resolução (só quando não está em tela cheia)
        if (!checkTelaCheia.isSelected()) {
            aplicarResolucao(comboResolucao.getValue(), stage);
        }

        configs.salvar();
    }

    // -------------------------------------------------------
    // RESETAR — volta aos padrões sem salvar no disco ainda
    // -------------------------------------------------------

    @FXML
    private void resetar() {
        configs.resetar();

        sliderMusica.setValue(configs.getMusica());
        sliderSom.setValue(configs.getSom());
        checkTelaCheia.setSelected(configs.isTelaCheia());
        comboResolucao.setValue(configs.getResolucao());

        // Aplica os padrões em tempo real
        audio.setVolumeMusica(configs.getMusica());
        audio.setVolumeSom(configs.getSom());
        App.getStage().setFullScreen(false);
        aplicarResolucao(configs.getResolucao(), App.getStage());
    }

    // -------------------------------------------------------
    // VOLTAR
    // -------------------------------------------------------

    @FXML
    private void voltarParaMenu() throws IOException {
        voltarParaOrigem();
    }

    private void voltarParaOrigem() throws IOException {
        String telaAnterior = EstadoJogo.getInstance().getTelaAnteriorConfigs();

        if (telaAnterior == null || telaAnterior.isBlank()) {
            telaAnterior = "menu";
        }

        Object controller = App.setRoot(telaAnterior);

        if (controller instanceof RestroomController) {
            ((RestroomController) controller).startGame(App.getStage().getScene());
        }

        if (controller instanceof StreetsController) {
            ((StreetsController) controller).startGame(App.getStage().getScene());
        }
    }

    // -------------------------------------------------------
    // AUXILIAR — interpreta "1280 x 720" e redimensiona a janela
    // -------------------------------------------------------

    private void aplicarResolucao(String resolucao, Stage stage) {
        try {
            String[] partes = resolucao.replace(" ", "").split("x");
            double largura = Double.parseDouble(partes[0]);
            double altura  = Double.parseDouble(partes[1]);

            stage.setWidth(largura);
            stage.setHeight(altura);
            stage.centerOnScreen();
        } catch (Exception e) {
            System.err.println("Resolução inválida: " + resolucao);
        }
    }
}
