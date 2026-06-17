package com.cls.projetoluacarmesim.combate;

import java.util.ArrayList;
import java.util.List;

public class ResultadoHabilidade {

    private final boolean sucesso;
    private final boolean encerraTurno;
    private final List<String> mensagens = new ArrayList<>();

    private ResultadoHabilidade(boolean sucesso, boolean encerraTurno) {
        this.sucesso = sucesso;
        this.encerraTurno = encerraTurno;
    }

    public static ResultadoHabilidade sucesso() {
        return new ResultadoHabilidade(true, true);
    }

    public static ResultadoHabilidade falha(String mensagem) {
        ResultadoHabilidade resultado = new ResultadoHabilidade(false, false);
        resultado.adicionarMensagem(mensagem);
        return resultado;
    }

    public boolean isSucesso() {
        return sucesso;
    }

    public boolean encerraTurno() {
        return encerraTurno;
    }

    public List<String> getMensagens() {
        return mensagens;
    }

    public void adicionarMensagem(String mensagem) {
        if (mensagem != null && !mensagem.isBlank()) {
            mensagens.add(mensagem);
        }
    }
}
