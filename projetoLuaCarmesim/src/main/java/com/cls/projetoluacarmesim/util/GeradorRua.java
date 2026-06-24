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
        if (numeroRua <= 8) {
            return 1;
        }

        if (numeroRua <= 16) {
            return random.nextDouble() < 0.80 ? 1 : 2;
        }

        if (numeroRua <= 32) {
            return random.nextDouble() < 0.65 ? 1 : 2;
        }

        if (numeroRua <= 48) {
            return random.nextDouble() < 0.45 ? 1 : 2;
        }

        return 2;
    }

    public int sortearQuantidadeItens(int numeroRua) {
        if (numeroRua <= 2) {
            return 1;
        }

        return random.nextDouble() < 0.65 ? 1 : 2;
    }

    public double sortearX(double minimo, double maximo) {
        return minimo + (maximo - minimo) * random.nextDouble();
    }

    public double sortearY(double minimo, double maximo) {
        return minimo + (maximo - minimo) * random.nextDouble();
    }
}