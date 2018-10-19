package com.example.logan.github_test;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;

public class MusicPlayer {
   static MediaPlayer mediaPlayer = new MediaPlayer();


    static public void play(String songURL) {

        class ReleaseRunner implements Runnable{
            private MediaPlayer mp;

            private ReleaseRunner(MediaPlayer mp){
                this.mp = mp;
            }

            @Override
            public void run() {
                if (mp != null) {
                    try {
                        Log.d("ds","Releasing " +mp);
                        mp.release();
                        Log.d("ds","Release done");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }

        new Thread(new ReleaseRunner(mediaPlayer)).start();


        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(songURL);
        } catch (IOException e) {
            e.printStackTrace();
        }


        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                Log.d("ds",5+ ""+ mediaPlayer);
                mediaPlayer.start();
                Log.d("ds",6+"");
            }
        });

        Log.d("ds",3+"");
        mediaPlayer.prepareAsync();
        Log.d("ds",4+"");

    }

    static public void pause(){
        mediaPlayer.pause();
    }





}
