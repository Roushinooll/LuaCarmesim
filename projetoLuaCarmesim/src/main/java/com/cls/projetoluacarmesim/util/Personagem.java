package com.cls.projetoluacarmesim.util;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Personagem {

    private ImageView personagem;
    private double speed = 450;

    public Personagem(ImageView personagem) {
        this.personagem = personagem;

        // Carrega sprite do resources/image
        Image img = new Image(
            getClass()
                .getResource("/image/front_sprite.png")
                .toExternalForm()
        );

        personagem.setImage(img);

        // Tamanho do sprite
        personagem.setFitWidth(300);
        personagem.setFitHeight(300);

        // Mantém proporção
        personagem.setPreserveRatio(true);
    }

    public void update(double delta, Input input) {
        double dx = 0;
        double dy = 0;

        if (input.up) dy -= 1;
        if (input.down) dy += 1;
        if (input.left) dx -= 1;
        if (input.right) dx += 1;

        double length = Math.sqrt(dx * dx + dy * dy);

        if (length > 0) {
            dx = dx / length * speed * delta;
            dy = dy / length * speed * delta;
        }

        personagem.setTranslateX(personagem.getTranslateX() + dx);
        personagem.setTranslateY(personagem.getTranslateY() + dy);
    }

    public ImageView getPersonagem() {
        return personagem;
    }

    public void setPersonagem(ImageView personagem) {
        this.personagem = personagem;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }
}