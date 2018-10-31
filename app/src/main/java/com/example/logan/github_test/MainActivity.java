package com.example.logan.github_test;

import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;
import android.view.View;

import java.util.ArrayList;

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

        initRecyclerView();
        initSteemJClient();
        initButtos();

        getSongs(startingTag);


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



    private void initRecyclerView(){
        songRecyclerView = findViewById(R.id.recycler_view);
        adapter = new RecyclerViewAdapter(this, songs);
        songRecyclerView.setAdapter(adapter);
        songRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

}
