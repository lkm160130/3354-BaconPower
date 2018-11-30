package com.example.logan.github_test;

import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;
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
    ProgressBar loadingCircleView;
    ProgressBar songLoadingCircleView;
    LinearLayout playBar;
    SeekBar playBarSeekBar;
    TextView durationText;
    MusicPlayer player;
    Timer durationTimer;

    boolean seekingThroughTrack;

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
                    loadingCircleView.setVisibility(View.VISIBLE);
                    getSongs(NetworkTools.TAG_HOT);

                    break;
                case R.id.trending:
                    adapter.resetActiveHolder();
                    b_trending.setTextColor(getResources().getColor(R.color.colorText));
                    b_hot.setTextColor(getResources().getColor(R.color.colorTextLight));
                    b_new.setTextColor(getResources().getColor(R.color.colorTextLight));
                    loadingCircleView.setVisibility(View.VISIBLE);
                    getSongs(NetworkTools.TAG_TRENDING);

                    break;
                case R.id._new:
                    adapter.resetActiveHolder();
                    b_new.setTextColor(getResources().getColor(R.color.colorText));
                    b_trending.setTextColor(getResources().getColor(R.color.colorTextLight));
                    b_hot.setTextColor(getResources().getColor(R.color.colorTextLight));
                    loadingCircleView.setVisibility(View.VISIBLE);
                    getSongs(NetworkTools.TAG_NEW);

                    break;
                case R.id.logo:
                    adapter.resetActiveHolder();
                    b_new.setTextColor(getResources().getColor(R.color.colorTextLight));
                    b_trending.setTextColor(getResources().getColor(R.color.colorTextLight));
                    b_hot.setTextColor(getResources().getColor(R.color.colorTextLight));
                    loadingCircleView.setVisibility(View.VISIBLE);
                    getSongs(NetworkTools.TAG_FEED);
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("DT","onCreate");

        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        player = new MusicPlayer(this);

        initMainLayout();
        initRecyclerView();
        initSteemJClient();
        initButtos();
        initMusicBar();
        updateMusicBar();


        getSongs(NetworkTools.TAG_FEED);
    }

    private void initMainLayout(){
        playBar = findViewById(R.id.play_bar);
        playBarSeekBar = findViewById(R.id.seekBar);
        durationText = findViewById(R.id.duration);
        loadingCircleView = findViewById(R.id.progressBar);
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
        findViewById(R.id.logo).setOnClickListener(clickListener);
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


    private void getSongs(final int tag){
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
                            if (songRecyclerView.getAdapter()!=null)
                                songRecyclerView.getAdapter().notifyDataSetChanged();
                            loadingCircleView.setVisibility(View.GONE);
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


    public void initMusicBar(){
        songLoadingCircleView = findViewById(R.id.songProgressBar);
        playBarSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (seekingThroughTrack)
                    durationText.setText(player.convertPositionToTimeString(progress*1000));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                seekingThroughTrack = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekingThroughTrack = false;

                if (MusicPlayer.mediaPlayer!=null && MusicPlayer.mediaPlayer.isPlaying()){
                    MusicPlayer.mediaPlayer.seekTo(playBarSeekBar.getProgress()*1000);
                }
            }
        });
    }

    public void updateMusicBar(){
        if (MusicPlayer.mediaPlayer!=null)
            playBar.setVisibility(View.VISIBLE);
        else
            return;

        if (MusicPlayer.mediaPlayer.isPlaying()){
            songLoadingCircleView.setVisibility(View.GONE);
            durationText.setVisibility(View.VISIBLE);
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

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (!seekingThroughTrack) {
                                        playBarSeekBar.setProgress(MusicPlayer.mediaPlayer.getCurrentPosition() / 1000);
                                        durationText.setText(player.getTimeString());
                                    }
                                }
                            });
                        }
                    }
                }
            }, 0, 1000);
        }
    }


    public void musicBarItemClicked(View v){
        switch(v.getId()) {
            case R.id.pause_play_btn:
            if (MusicPlayer.mediaPlayer == null)
                return;

            if (MusicPlayer.mediaPlayer.isPlaying()) {
                MusicPlayer.mediaPlayer.pause();
            } else {
                MusicPlayer.mediaPlayer.start();
            }


        updateMusicBar();
        break;
        case R.id.next:

                int nextIndex = (adapter.getActiveHolderPosition() + 1)% songs.size();
                songLoadingCircleView.setVisibility(View.VISIBLE);
                durationText.setVisibility(View.GONE);
                player.play(songs.get(nextIndex));
                adapter.setActiveHolderPosition(nextIndex);

        break;
        case R.id.previous:
            int nextIndex2 = (adapter.getActiveHolderPosition() - 1)% songs.size();
            if(nextIndex2 < 0){
                nextIndex2 = songs.size() - 1;
            }
            songLoadingCircleView.setVisibility(View.VISIBLE);
            durationText.setVisibility(View.GONE);
            player.play(songs.get(nextIndex2));
            adapter.setActiveHolderPosition(nextIndex2);

        break;
            case R.id.shuffle:
                Random rnd = new Random();
                int randomSongIndex = rnd.nextInt(songs.size());
                songLoadingCircleView.setVisibility(View.VISIBLE);
                durationText.setVisibility(View.GONE);
                player.play(songs.get(randomSongIndex));
                adapter.setActiveHolderPosition(randomSongIndex);
    }

    }


    private void initRecyclerView(){
        songRecyclerView = findViewById(R.id.recycler_view);
        adapter = new RecyclerViewAdapter(this, songs, player);
        songRecyclerView.setAdapter(adapter);
        songRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

}
