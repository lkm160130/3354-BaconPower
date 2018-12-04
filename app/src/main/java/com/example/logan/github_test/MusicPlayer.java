package com.example.logan.github_test;

import android.annotation.SuppressLint;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


/**
 * class for playing, pausing, and getting information about playing songs
 */
class MusicPlayer {
   private static MediaPlayer mediaPlayer = new MediaPlayer();
   private Song currentSongPlaying;
   private boolean onRepeat;

   private static MusicPlayer musicPlayer;
   private MusicPlayer(){}

   static MusicPlayer getInstance(){
       if (musicPlayer == null)
           musicPlayer = new MusicPlayer();

       return musicPlayer;
   }


    /**
     * loads and starts playing a songLoading from the network in the background
     * method will automatically call updateMusicBar once
     * songLoading starts playing in order to update UI
     * @param song object which must include a songLoading URL
     */
   void play(final Song song, final MainActivity mainActivity) {

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

        //releases previous songLoading on a different thread because it was discovered that
        //the release method can take up lots of time to complete
        new Thread(new ReleaseRunner(mediaPlayer)).start();


        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(song.getSongURL());
        } catch (IOException e) {
            e.printStackTrace();
        }


        //when media is loaded it will start playing and update the ui
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setCurrentSongPlaying(song);
                        mainActivity.updateMusicBar();
                    }
                });
                }
        });

        //when media finishes playing it either goess to the next song or repeats
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.d("ds", "media source reached the end");
                if (onRepeat) {
                    mediaPlayer.seekTo(0);
                    mediaPlayer.start();
                }else {
                    mainActivity.nextSong();
                }
            }
        });

        mediaPlayer.prepareAsync();
    }


    /**
     * pauses MediaPlayer if it is playing
     */
    void pause(){
       if (mediaPlayer!=null && mediaPlayer.isPlaying())
           mediaPlayer.pause();
    }


    /**
     * resumes MediaPlayer if it is not playing
     */
    void resume(){
        if (mediaPlayer!=null && !mediaPlayer.isPlaying())
            mediaPlayer.start();
    }

    /**
     * @return if the MediaPlayer is currently playing a songLoading
     */
    boolean isPlaying(){
       if (mediaPlayer!=null)
           return mediaPlayer.isPlaying();
       else
           return false;
    }

    /**
     * @return if the MediaPlayer object has been initialized
     */
    boolean isMediaPlayerNull(){
       return mediaPlayer == null;
    }

    /**
     * @return formatted string mm:ss or HH:mm:ss for the current position of mediaPlayer
     */
    String getTimeString(){
        if (mediaPlayer!=null && mediaPlayer.isPlaying()) {
            return convertPositionToTimeString(mediaPlayer.getCurrentPosition());
        }
        return null;
    }


    /**
     * @return songLoading that is playing (only after it has loaded and started playing)
     */
    Song getCurrentSongPlaying() {
        return currentSongPlaying;
    }

    /**
     * @param currentSongPlaying songLoading that has been loaded and started playing
     */
    private void setCurrentSongPlaying(Song currentSongPlaying) {
        this.currentSongPlaying = currentSongPlaying;
    }


    /**
     * @return position of mediaPlayer in ms
     */
    int getCurrentPosition(){
       if (mediaPlayer!=null)
           return mediaPlayer.getCurrentPosition();
       else
           return 0;
    }

    /**
     * @param duration in ms
     * @return formatted string mm:ss or HH:mm:ss for the given duration
     */
    @SuppressLint("SimpleDateFormat")
    String convertPositionToTimeString(int duration){
        TimeZone tz = TimeZone.getTimeZone("UTC");
        SimpleDateFormat df;
        //if sound is longer than an hour then diplay hours
        if ((mediaPlayer.getDuration() / 1000) > 3600) {
            df = new SimpleDateFormat("HH:mm:ss");
        } else {
            df = new SimpleDateFormat("mm:ss");
        }
        df.setTimeZone(tz);
        return df.format(new Date(duration));
    }

    /**
     * @param time in ms
     */
    void seekTo(int time) {
        if (mediaPlayer!=null)
            mediaPlayer.seekTo(time);
    }

    /**
     * @return if music player is set to repeat current song
     */
    boolean isOnRepeat() {
        return onRepeat;
    }

    /**
     * @param onRepeat indicates if song should repeat
     */
    void setOnRepeat(boolean onRepeat) {
        this.onRepeat = onRepeat;
    }
}
