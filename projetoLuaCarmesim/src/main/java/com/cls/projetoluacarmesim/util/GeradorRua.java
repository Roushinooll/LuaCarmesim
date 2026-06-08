package com.cls.projetoluacarmesim.util;

import com.cls.projetoluacarmesim.enums.TipoRua;
import java.util.Random;

public class GeradorRua {

    private final Random random = new Random();

    public TipoRua sortearTipoRua() {
        TipoRua[] tipos = TipoRua.values();
        return tipos[random.nextInt(tipos.length)];
    }

    public int sortearQuantidadeInimigos(int numeroRua) {
        int minimo = 1;
        int maximo = Math.min(1 + numeroRua, 4);

        return random.nextInt(maximo - minimo + 1) + minimo;
    }

    public int sortearQuantidadeItens(int numeroRua) {
        if (numeroRua <= 2) {
            return 1;
        }

        return random.nextBoolean() ? 1 : 2;
    }

    public double sortearX(double minimo, double maximo) {
        return minimo + (maximo - minimo) * random.nextDouble();
    }

    public double sortearY(double minimo, double maximo) {
        return minimo + (maximo - minimo) * random.nextDouble();
    }
}