package com.cls.projetoluacarmesim.util;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class Input {

    public boolean up, left, right, down;
    public boolean interact;

    public Input(Scene scene) {

        scene.addEventFilter(KeyEvent.KEY_PRESSED, e -> {

            if (e.getCode() == KeyCode.W || e.getCode() == KeyCode.UP) {
                up = true;
            }

            if (e.getCode() == KeyCode.S || e.getCode() == KeyCode.DOWN) {
                down = true;
            }

            if (e.getCode() == KeyCode.A || e.getCode() == KeyCode.LEFT) {
                left = true;
            }

            if (e.getCode() == KeyCode.D || e.getCode() == KeyCode.RIGHT) {
                right = true;
            }

            if (e.getCode() == KeyCode.F) {
                interact = true;
            }
        });

        scene.addEventFilter(KeyEvent.KEY_RELEASED, e -> {

            if (e.getCode() == KeyCode.W || e.getCode() == KeyCode.UP) {
                up = false;
            }

            if (e.getCode() == KeyCode.S || e.getCode() == KeyCode.DOWN) {
                down = false;
            }

            if (e.getCode() == KeyCode.A || e.getCode() == KeyCode.LEFT) {
                left = false;
            }

            if (e.getCode() == KeyCode.D || e.getCode() == KeyCode.RIGHT) {
                right = false;
            }

            if (e.getCode() == KeyCode.F) {
                interact = false;
            }
        });
    }
}