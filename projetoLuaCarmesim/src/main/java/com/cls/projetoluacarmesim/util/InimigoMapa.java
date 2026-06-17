package com.cls.projetoluacarmesim.util;

import com.cls.projetoluacarmesim.model.Inimigo;
import javafx.scene.shape.Rectangle;

public class InimigoMapa {

    private final Inimigo inimigo;
    private final Rectangle view;
    private boolean perseguindo;

    public InimigoMapa(Inimigo inimigo, Rectangle view) {
        this.inimigo = inimigo;
        this.view = view;
        this.perseguindo = false;
    }

    public Inimigo getInimigo() {
        return inimigo;
    }

    public Rectangle getView() {
        return view;
    }

    public boolean isPerseguindo() {
        return perseguindo;
    }

    public void setPerseguindo(boolean perseguindo) {
        this.perseguindo = perseguindo;
    }
}
