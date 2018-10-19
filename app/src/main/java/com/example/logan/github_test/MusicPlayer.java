package com.example.logan.github_test;

import android.media.AudioManager;
import android.media.MediaPlayer;

import java.io.IOException;

public class MusicPlayer {
   static MediaPlayer mediaPlayer = new MediaPlayer();


    static public void play(String songURL) {
        mediaPlayer.reset();
        mediaPlayer = new MediaPlayer();

        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);


            try {

                mediaPlayer.setDataSource(songURL);

            } catch (IOException e) {
                e.printStackTrace();
            }
        try {
            mediaPlayer.prepare(); // might take long! (for buffering, etc)
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.start();


    }

    static public void pause(){
        mediaPlayer.pause();
    }





}
