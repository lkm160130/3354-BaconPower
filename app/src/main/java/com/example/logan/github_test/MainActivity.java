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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import eu.bittrade.libs.steemj.SteemJ;
import eu.bittrade.libs.steemj.exceptions.SteemCommunicationException;
import eu.bittrade.libs.steemj.exceptions.SteemInvalidTransactionException;
import eu.bittrade.libs.steemj.exceptions.SteemResponseException;

public class MainActivity extends AppCompatActivity {
    private ArrayList<Song> songs = new ArrayList<>();
    RecyclerView songRecyclerView;
    RecyclerViewAdapter adapter;
    SteemJ steemJClient;
    Button loginButton;
    Button trendingButton;
    Button hotButton;
    Button newButton;
    ImageView profileIcon;

    ProgressBar loadingCircleView;
    ProgressBar songLoadingCircleView;
    LinearLayout playBar;
    SeekBar playBarSeekBar;
    TextView durationText;
    TextView songTitleText;
    Timer durationTimer;

    //TAG_TRENDING, TAG_HOT, TAG_NEW, or TAG_FEED
    int currentTagSelected;
    Account loggedInAccount;


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
                    hotButton.setTextColor(getResources().getColor(R.color.colorText));
                    trendingButton.setTextColor(getResources().getColor(R.color.colorTextLight));
                    newButton.setTextColor(getResources().getColor(R.color.colorTextLight));
                    loadingCircleView.setVisibility(View.VISIBLE);
                    getSongs(NetworkTools.TAG_HOT);
                    break;
                case R.id.trending:
                    adapter.resetActiveHolder();
                    trendingButton.setTextColor(getResources().getColor(R.color.colorText));
                    hotButton.setTextColor(getResources().getColor(R.color.colorTextLight));
                    newButton.setTextColor(getResources().getColor(R.color.colorTextLight));
                    loadingCircleView.setVisibility(View.VISIBLE);
                    getSongs(NetworkTools.TAG_TRENDING);

                    break;
                case R.id._new:
                    adapter.resetActiveHolder();
                    newButton.setTextColor(getResources().getColor(R.color.colorText));
                    trendingButton.setTextColor(getResources().getColor(R.color.colorTextLight));
                    hotButton.setTextColor(getResources().getColor(R.color.colorTextLight));
                    loadingCircleView.setVisibility(View.VISIBLE);
                    getSongs(NetworkTools.TAG_NEW);

                    break;
                case R.id.logo:
                    adapter.resetActiveHolder();
                    newButton.setTextColor(getResources().getColor(R.color.colorTextLight));
                    trendingButton.setTextColor(getResources().getColor(R.color.colorTextLight));
                    hotButton.setTextColor(getResources().getColor(R.color.colorTextLight));
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

        initMainLayout();
        initRecyclerView();
        initButtos();
        initSteemJClient();
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
        hotButton = findViewById(R.id.hot);
        hotButton.setOnClickListener(clickListener);
        loginButton = findViewById(R.id.login);
        loginButton.setOnClickListener(clickListener);
        newButton = findViewById(R.id._new);
        newButton.setOnClickListener(clickListener);
        trendingButton = findViewById(R.id.trending);
        trendingButton.setOnClickListener(clickListener);
        profileIcon = findViewById(R.id.profile);
        findViewById(R.id.logo).setOnClickListener(clickListener);
    }
    private void initSteemJClient(){
        try {
            steemJClient = new SteemJ();

            try {
                loggedInAccount = Tools.loginIfPossible(this);
                loginButton.setVisibility(View.GONE);
                profileIcon.setVisibility(View.VISIBLE);

                Transformation transformation = new RoundedTransformationBuilder()
                        .cornerRadiusDp(30)
                        .oval(false)
                        .build();


                Picasso.get().load(loggedInAccount.getImageURL()).placeholder(R.drawable.login).transform(transformation).into(
                        (profileIcon));


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

                    //loading the songLoading list takes time, temp holds new list while allowing us to keep using the
                    //current list until the change needs to be made. Not doing this causes crash when user tries
                    //to scroll while loading new songs.
                    ArrayList<Song> temp;

                    //if some songs are already populated, load more songs after last songLoading (continuous scrolling)
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
                    durationText.setText(MusicPlayer.getInstance().convertPositionToTimeString(progress*1000));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                seekingThroughTrack = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekingThroughTrack = false;

                if (!MusicPlayer.getInstance().isMediaPlayerNull() && MusicPlayer.getInstance().isPlaying()){
                    MusicPlayer.getInstance().seekTo(playBarSeekBar.getProgress()*1000);
                }
            }
        });
    }


    public void updateMusicBar(){
        if (!MusicPlayer.getInstance().isMediaPlayerNull())
            playBar.setVisibility(View.VISIBLE);
        else
            return;

        if (MusicPlayer.getInstance().isPlaying()){
            songLoadingCircleView.setVisibility(View.GONE);
            durationText.setVisibility(View.VISIBLE);
            ((ImageButton)playBar.findViewById(R.id.pause_play_btn)).setImageResource(R.drawable.ic_pause);
        }else {
            ((ImageButton)playBar.findViewById(R.id.pause_play_btn)).setImageResource(R.drawable.ic_play_arrow);
        }

        if (MusicPlayer.getInstance().getCurrentSongPlaying()!=null){
            songTitleText.setVisibility(View.VISIBLE);
            songTitleText.setText(MusicPlayer.getInstance().getCurrentSongPlaying().getTitle());

            if (MusicPlayer.getInstance().getCurrentSongPlaying().getAccountFavoritedSong())
                playBar.findViewById(R.id.favButton).setBackgroundResource(R.drawable.ic_favorite);
            else
                playBar.findViewById(R.id.favButton).setBackgroundResource(R.drawable.ic_favorite_border);
        }else {
            songTitleText.setVisibility(View.GONE);
        }


        if (!MusicPlayer.getInstance().isPlaying()) {
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
                    if (MusicPlayer.getInstance().getCurrentSongPlaying() !=null){
                        if (playBarSeekBar.getMax()!= MusicPlayer.getInstance().getCurrentSongPlaying().getDuration())
                            playBarSeekBar.setMax(MusicPlayer.getInstance().getCurrentSongPlaying().getDuration());

                        if (MusicPlayer.getInstance().isPlaying()){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (!seekingThroughTrack) {
                                        playBarSeekBar.setProgress(MusicPlayer.getInstance().getCurrentPosition() / 1000);
                                        durationText.setText(MusicPlayer.getInstance().getTimeString());
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
            if (MusicPlayer.getInstance().isMediaPlayerNull())
                return;

            if (MusicPlayer.getInstance().isPlaying()) {
                MusicPlayer.getInstance().pause();
            } else {
                MusicPlayer.getInstance().resume();
            }


        updateMusicBar();
        break;
        case R.id.next:

                int nextIndex = (adapter.getActiveHolderPosition() + 1)% songs.size();
                songLoadingCircleView.setVisibility(View.VISIBLE);
                durationText.setVisibility(View.GONE);
                MusicPlayer.getInstance().play(songs.get(nextIndex), this);
                adapter.setActiveHolderPosition(nextIndex);

        break;
        case R.id.previous:
            int nextIndex2 = (adapter.getActiveHolderPosition() - 1)% songs.size();
            if(nextIndex2 < 0){
                nextIndex2 = songs.size() - 1;
            }
            songLoadingCircleView.setVisibility(View.VISIBLE);
            durationText.setVisibility(View.GONE);
            MusicPlayer.getInstance().play(songs.get(nextIndex2), this);
            adapter.setActiveHolderPosition(nextIndex2);

        break;
        case R.id.shuffle:
            Random rnd = new Random();
            int randomSongIndex = rnd.nextInt(songs.size());
            songLoadingCircleView.setVisibility(View.VISIBLE);
            durationText.setVisibility(View.GONE);
            MusicPlayer.getInstance().play(songs.get(randomSongIndex), this);
            adapter.setActiveHolderPosition(randomSongIndex);
            break;
        case R.id.favButton:
                if (Tools.getAccountName(this)!=null) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Song s = MusicPlayer.getInstance().getCurrentSongPlaying();
                                steemJClient.vote(s.getAuthor(), MusicPlayer.getInstance().getCurrentSongPlaying().getPermlink(), (short) 100);
                                s.setAccountFavoritedSong(true);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        updateMusicBar();
                                    }
                                });
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
        adapter = new RecyclerViewAdapter(this, songs, MusicPlayer.getInstance());
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
