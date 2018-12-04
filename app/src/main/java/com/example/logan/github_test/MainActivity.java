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

import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import eu.bittrade.libs.steemj.SteemJ;
import eu.bittrade.libs.steemj.base.models.AccountName;
import eu.bittrade.libs.steemj.configuration.SteemJConfig;
import eu.bittrade.libs.steemj.enums.PrivateKeyType;
import eu.bittrade.libs.steemj.exceptions.SteemCommunicationException;
import eu.bittrade.libs.steemj.exceptions.SteemInvalidTransactionException;
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
    TextView songTitleText;
    MusicPlayer player;
    Timer durationTimer;

    //TAG_TRENDING, TAG_HOT, TAG_NEW, or TAG_FEED
    int currentTagSelected;
    Song currentSongPlaying;

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

        Log.d("ds","onCreate");

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

            try {
                Tools.loginIfPossible(this);
                b_login.setVisibility(View.GONE);
            }catch (Exception e){
                e.printStackTrace();
            }


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

                    //loading the song list takes time, temp holds new list while allowing us to keep using the
                    //current list until the change needs to be made. Not doing this causes crash when user tries
                    //to scroll while loading new songs.
                    ArrayList<Song> temp;

                    //if some songs are already populated, load more songs after last song (continuous scrolling)
                    if (currentTagSelected == tag && songs.size()>0 && songs.get(songs.size()-1).getTag()==tag){
                        temp = NetworkTools.getDsoundSongs(steemJClient, tag, songs.get(songs.size()-1), MainActivity.this);
                    }else {
                        temp = NetworkTools.getDsoundSongs(steemJClient, tag, null, MainActivity.this);
                        songs.clear();
                    }


                    songs.addAll(temp);
                    currentTagSelected = tag;

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
        songTitleText = findViewById(R.id.songTitle);
        //required for scrolling text
        songTitleText.setSelected(true);
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

                if (!player.isMediaPlayerNull() && player.isPlaying()){
                    player.seekTo(playBarSeekBar.getProgress()*1000);
                }
            }
        });
    }

    public void setCurrentSongPlaying(Song currentSongPlaying) {
        this.currentSongPlaying = currentSongPlaying;
    }

    public void updateMusicBar(){
        if (!player.isMediaPlayerNull())
            playBar.setVisibility(View.VISIBLE);
        else
            return;

        if (player.isPlaying()){
            songLoadingCircleView.setVisibility(View.GONE);
            durationText.setVisibility(View.VISIBLE);
            ((ImageButton)playBar.findViewById(R.id.pause_play_btn)).setImageResource(R.drawable.ic_pause);
        }else {
            ((ImageButton)playBar.findViewById(R.id.pause_play_btn)).setImageResource(R.drawable.ic_play_arrow);
        }

        if (currentSongPlaying!=null){
            songTitleText.setVisibility(View.VISIBLE);
            songTitleText.setText(currentSongPlaying.getTitle());

            if (currentSongPlaying.getAccountFavoritedSong())
                playBar.findViewById(R.id.favButton).setBackgroundResource(R.drawable.ic_favorite);
            else
                playBar.findViewById(R.id.favButton).setBackgroundResource(R.drawable.ic_favorite_border);
        }else {
            songTitleText.setVisibility(View.GONE);
        }


        if (!player.isPlaying()) {
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

                        if (player.isPlaying()){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (!seekingThroughTrack) {
                                        playBarSeekBar.setProgress(player.getCurrentPosition() / 1000);
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
            if (player.isMediaPlayerNull())
                return;

            if (player.isPlaying()) {
                player.pause();
            } else {
                player.resume();
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
            break;
        case R.id.favButton:
                if (Tools.getAccountName(this)!=null) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Song s = currentSongPlaying;
                                steemJClient.vote(s.getAuthor(), currentSongPlaying.getPermlink(), (short) 100);
                                s.setAccountFavoritedSong(true);
                            } catch (SteemCommunicationException e) {
                                e.printStackTrace();
                            } catch (SteemResponseException e) {
                                e.printStackTrace();
                            } catch (SteemInvalidTransactionException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }else {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }

            break;
    }

    }


    private void initRecyclerView(){
        songRecyclerView = findViewById(R.id.recycler_view);
        adapter = new RecyclerViewAdapter(this, songs, player);
        adapter.setOnBottomReachedListener(new OnBottomReachedListener() {
            @Override
            public void onBottomReached(int position) {
                Log.d("ds", "onBottomReached");
                getSongs(currentTagSelected);
            }
        });


        songRecyclerView.setAdapter(adapter);
        songRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

}
