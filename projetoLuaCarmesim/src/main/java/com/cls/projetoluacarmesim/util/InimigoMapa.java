package com.cls.projetoluacarmesim.util;

import com.cls.projetoluacarmesim.model.Inimigo;
import javafx.scene.image.ImageView;

public class InimigoMapa {

    private final Inimigo inimigo;
    private final ImageView view;
    private boolean perseguindo;

    public InimigoMapa(Inimigo inimigo, ImageView view) {
        this.inimigo = inimigo;
        this.view = view;
        this.perseguindo = false;
    }

    public Inimigo getInimigo() {
        return inimigo;
    }

    public ImageView getView() {
        return view;
    }

    public boolean isPerseguindo() {
        return perseguindo;
    }

    public void setPerseguindo(boolean perseguindo) {
        this.perseguindo = perseguindo;
    }
}
