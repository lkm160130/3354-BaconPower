package com.example.logan.github_test;

import android.animation.LayoutTransition;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import eu.bittrade.libs.steemj.SteemJ;
import eu.bittrade.libs.steemj.exceptions.SteemCommunicationException;
import eu.bittrade.libs.steemj.exceptions.SteemResponseException;

public class MainActivity extends AppCompatActivity {
    private ArrayList<Song> songs = new ArrayList<>();
    RecyclerView songRecyclerView;
    RecyclerViewAdapter adapter;
    SteemJ steemJClient;
    Button b_login;
    Button b_trending;
    Button b_hot;
    Button b_new;
    LinearLayout playBar;
    SeekBar playBarSeekBar;
    MusicPlayer player;
    Timer durationTimer;

    String startingTag = "Feed"; //Note this actually just triggers the default case in NetworkTool.getDsoundSongs. Set to Trending, Hot, or New to change starting list of songs

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            System.out.println(R.id.LoginButton + " " + v.getId());
            switch(v.getId()){
                case R.id.login:
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    break;
                case R.id.hot:
                    adapter.resetActiveHolder();
                    b_hot.setTextColor(getResources().getColor(R.color.colorText));
                    b_trending.setTextColor(getResources().getColor(R.color.colorTextLight));
                    b_new.setTextColor(getResources().getColor(R.color.colorTextLight));
                    getSongs("Hot");

                    break;
                case R.id.trending:
                    adapter.resetActiveHolder();
                    b_trending.setTextColor(getResources().getColor(R.color.colorText));
                    b_hot.setTextColor(getResources().getColor(R.color.colorTextLight));
                    b_new.setTextColor(getResources().getColor(R.color.colorTextLight));
                    getSongs("Trending");

                    break;
                case R.id._new:
                    adapter.resetActiveHolder();
                    b_new.setTextColor(getResources().getColor(R.color.colorText));
                    b_trending.setTextColor(getResources().getColor(R.color.colorTextLight));
                    b_hot.setTextColor(getResources().getColor(R.color.colorTextLight));
                    getSongs("New");

                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        player = new MusicPlayer(this);

        initMainLayout();
        initRecyclerView();
        initSteemJClient();
        initButtos();
        updateMusicBar();


        getSongs(startingTag);
    }

    private void initMainLayout(){
        playBar = findViewById(R.id.play_bar);
        playBarSeekBar = findViewById(R.id.seekBar);
    }

    private void initButtos(){
        b_hot = findViewById(R.id.hot);
        b_hot.setOnClickListener(clickListener);
        b_login = findViewById(R.id.login);
        b_login.setOnClickListener(clickListener);
        b_new = findViewById(R.id._new);
        b_new.setOnClickListener(clickListener);
        b_trending = findViewById(R.id.trending);
        b_trending.setOnClickListener(clickListener);
    }
    private void initSteemJClient(){
        try {
            steemJClient = new SteemJ();
        } catch (SteemCommunicationException e) {
            e.printStackTrace();
        } catch (SteemResponseException e) {
            e.printStackTrace();
        }
    }


    private void getSongs(final String tag){
        Runnable fetch = new Runnable() {
            @Override
            public void run() {
                try {
                    ArrayList<Song> temp = NetworkTools.getDsoundSongs(steemJClient, tag); //loading the song list takes time, temp holds new list while allowing us to keep using the
                                                                                            //current list until the change needs to be made. Not doing this causes crash when user tries
                                                                                            //to scroll while loading new songs.
                    songs.clear();
                    songs.addAll(temp);

                        Log.d("sng", Integer.toString(songs.size()));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            songRecyclerView.getAdapter().notifyDataSetChanged();
                        }
                    });
                } catch (SteemResponseException e) {
                    e.printStackTrace();
                } catch (SteemCommunicationException e) {
                    e.printStackTrace();
                }
            }
        };

        new Thread(fetch).start();
    }


    public void updateMusicBar(){
        if (MusicPlayer.mediaPlayer!=null)
            playBar.setVisibility(View.VISIBLE);
        else
            return;

        if (MusicPlayer.mediaPlayer.isPlaying()){
            ((ImageButton)playBar.findViewById(R.id.pause_play_btn)).setImageResource(R.drawable.ic_pause);
        }else {
            ((ImageButton)playBar.findViewById(R.id.pause_play_btn)).setImageResource(R.drawable.ic_play_arrow);
        }


        if (!MusicPlayer.mediaPlayer.isPlaying()) {
            if (durationTimer!=null) {
                durationTimer.cancel();
                durationTimer.purge();
                durationTimer = null;
            }
            return;
        }

        if (durationTimer == null) {
            durationTimer = new Timer();
            durationTimer.schedule(new TimerTask() {

                @Override
                public void run() {
                    if (player.song!=null){
                        if (playBarSeekBar.getMax()!= player.song.getDuration())
                            playBarSeekBar.setMax(player.song.getDuration());

                        if (MusicPlayer.mediaPlayer.isPlaying()){
                            playBarSeekBar.setProgress(MusicPlayer.mediaPlayer.getCurrentPosition()/1000);
                        }

                    }
                }

            }, 0, 1000);
        }

    }


    public void musicBarItemClicked(View v){
        if (MusicPlayer.mediaPlayer==null)
            return;

        if (MusicPlayer.mediaPlayer.isPlaying()){
            MusicPlayer.mediaPlayer.pause();
        }else {
            MusicPlayer.mediaPlayer.start();
        }

        updateMusicBar();

    }


    private void initRecyclerView(){
        songRecyclerView = findViewById(R.id.recycler_view);
        adapter = new RecyclerViewAdapter(this, songs, player);
        songRecyclerView.setAdapter(adapter);
        songRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

}
