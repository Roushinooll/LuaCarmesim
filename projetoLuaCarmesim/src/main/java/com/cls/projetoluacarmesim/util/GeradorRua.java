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
        if (numeroRua <= 5) {
            return 1;
        }

        if (numeroRua <= 10) {
            return random.nextDouble() < 0.70 ? 1 : 2;
        }

        if (numeroRua <= 25) {
            return random.nextBoolean() ? 1 : 2;
        }

        if (numeroRua <= 40) {
            return 1 + random.nextInt(3);
        }

        return 2 + random.nextInt(2);
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