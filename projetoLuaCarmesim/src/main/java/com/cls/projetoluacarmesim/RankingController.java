package com.cls.projetoluacarmesim;

import com.cls.projetoluacarmesim.dao.RankingDAO;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;

public class RankingController {

    @FXML
    private ListView<String> listaRanking;

    @FXML
    public void initialize() {
        carregarRanking();
    }

    private void carregarRanking() {
        listaRanking.getItems().clear();

        try {
            RankingDAO rankingDAO = new RankingDAO();
            List<String> ranking = rankingDAO.listarTop10ComNomes();

            if (ranking.isEmpty()) {
                listaRanking.getItems().add("Nenhum jogador no ranking ainda.");
                return;
            }

            for (int i = 0; i < ranking.size(); i++) {
                listaRanking.getItems().add((i + 1) + "º - " + ranking.get(i));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            listaRanking.getItems().add("Erro ao carregar ranking.");
            listaRanking.getItems().add(e.getMessage());
        }
    }

    @FXML
    private void voltarMenu() throws IOException {
        App.setRoot("menu");
    }
}