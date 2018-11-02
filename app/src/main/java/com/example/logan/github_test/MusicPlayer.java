package com.example.logan.github_test;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;

public class MusicPlayer {
   static MediaPlayer mediaPlayer = new MediaPlayer();
   MainActivity mainActivity;
    Song song;

   MusicPlayer(MainActivity mainActivity){
       this.mainActivity = mainActivity;
   }


   void play(Song song) {
        this.song = song;

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
            mediaPlayer.setDataSource(song.getSongURL());
        } catch (IOException e) {
            e.printStackTrace();
        }


        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mainActivity.updateMusicBar();
                    }
                });

                //mediaPlayer.seton
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
