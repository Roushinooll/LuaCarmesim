package com.cls.projetoluacarmesim.util;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;








public class GerenciadorAudio {

    private static GerenciadorAudio instancia;

    private MediaPlayer musicaPlayer;
    private double volumeMusica = 0.5;
    private double volumeSom    = 0.7;

    private GerenciadorAudio() {}

    public static GerenciadorAudio getInstance() {
        if (instancia == null) instancia = new GerenciadorAudio();
        return instancia;
    }

    
    
    

    




    public void tocarMusica(String nomeArquivo) {
        pararMusica();

        URL url = getClass().getResource("/audio/" + nomeArquivo + ".mp3");
        if (url == null) {
            System.err.println("Áudio não encontrado: /audio/" + nomeArquivo + ".mp3");
            return;
        }

        Media media = new Media(url.toExternalForm());
        musicaPlayer = new MediaPlayer(media);
        musicaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        musicaPlayer.setVolume(volumeMusica / 100.0);
        musicaPlayer.play();
    }

    public void pararMusica() {
        if (musicaPlayer != null) {
            musicaPlayer.stop();
            musicaPlayer.dispose();
            musicaPlayer = null;
        }
    }

    public void pausarMusica() {
        if (musicaPlayer != null) musicaPlayer.pause();
    }

    public void retomarMusica() {
        if (musicaPlayer != null) musicaPlayer.play();
    }

    
    
    

    



    public void tocarSfx(String nomeArquivo) {
        URL url = getClass().getResource("/audio/sfx/" + nomeArquivo + ".mp3");
        if (url == null) {
            System.err.println("SFX não encontrado: /audio/sfx/" + nomeArquivo + ".mp3");
            return;
        }

        MediaPlayer sfxPlayer = new MediaPlayer(new Media(url.toExternalForm()));
        sfxPlayer.setVolume(volumeSom / 100.0);
        sfxPlayer.play();
        
        sfxPlayer.setOnEndOfMedia(sfxPlayer::dispose);
    }

    
    
    

    
    public void setVolumeMusica(double volume) {
        this.volumeMusica = volume;
        if (musicaPlayer != null) musicaPlayer.setVolume(volume / 100.0);
    }

    
    public void setVolumeSom(double volume) {
        this.volumeSom = volume;
    }

    public double getVolumeMusica() { return volumeMusica; }
    public double getVolumeSom()    { return volumeSom; }
}
