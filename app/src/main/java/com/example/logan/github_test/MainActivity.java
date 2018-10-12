package com.example.logan.github_test;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;
import java.util.ArrayList;

import eu.bittrade.libs.steemj.SteemJ;
import eu.bittrade.libs.steemj.apis.database.models.state.State;
import eu.bittrade.libs.steemj.base.models.AccountName;
import eu.bittrade.libs.steemj.base.models.Permlink;
import eu.bittrade.libs.steemj.base.models.SignedBlock;
import eu.bittrade.libs.steemj.configuration.SteemJConfig;
import eu.bittrade.libs.steemj.exceptions.SteemCommunicationException;
import eu.bittrade.libs.steemj.exceptions.SteemResponseException;

public class MainActivity extends AppCompatActivity {
    private ArrayList<String> songImageURL = new ArrayList<>();
    private ArrayList<String> creatorNames = new ArrayList<>();
    private ArrayList<String> songTitles = new ArrayList<>();
    RecyclerView songRecyclerView;
    SteemJ steemJClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initSong();

        try {
            steemJClient = new SteemJ();
        } catch (SteemCommunicationException e) {
            e.printStackTrace();
        } catch (SteemResponseException e) {
            e.printStackTrace();
        }


        Runnable fetch = new Runnable() {
            @Override
            public void run() {
                try {
                    NetworkTools.getDsoundTrending(steemJClient);
                } catch (SteemResponseException e) {
                    e.printStackTrace();
                } catch (SteemCommunicationException e) {
                    e.printStackTrace();
                }
            }
        };

        new Thread(fetch).start();

    }




    private void initSong(){
        songImageURL.add("https://smiley.com/img/BRAND_SMILEY_MAIN.jpg");
        creatorNames.add("Bob");
        songTitles.add("Bob's Song");

        songImageURL.add("https://smiley.com/img/BRAND_SMILEY_MAIN.jpg");
        creatorNames.add("Sam");
        songTitles.add("Sam's Song");

        songImageURL.add("https://smiley.com/img/BRAND_SMILEY_MAIN.jpg");
        creatorNames.add("Joe");
        songTitles.add("Joe's Song");

        songImageURL.add("https://smiley.com/img/BRAND_SMILEY_MAIN.jpg");
        creatorNames.add("Tim");
        songTitles.add("Tim's Song");

        songImageURL.add("https://smiley.com/img/BRAND_SMILEY_MAIN.jpg");
        creatorNames.add("Bill");
        songTitles.add("Bill's Song");

        songImageURL.add("https://smiley.com/img/BRAND_SMILEY_MAIN.jpg");
        creatorNames.add("Jill");
        songTitles.add("Jill's Song");

        songImageURL.add("https://smiley.com/img/BRAND_SMILEY_MAIN.jpg");
        creatorNames.add("Bob");
        songTitles.add("Bob's Song");

        songImageURL.add("https://smiley.com/img/BRAND_SMILEY_MAIN.jpg");
        creatorNames.add("Bob");
        songTitles.add("Bob's Song");

        songImageURL.add("https://smiley.com/img/BRAND_SMILEY_MAIN.jpg");
        creatorNames.add("Bob");
        songTitles.add("Bob's Song");

        songImageURL.add("https://smiley.com/img/BRAND_SMILEY_MAIN.jpg");
        creatorNames.add("Bob");
        songTitles.add("Bob's Song");

        songImageURL.add("https://smiley.com/img/BRAND_SMILEY_MAIN.jpg");
        creatorNames.add("Bob");
        songTitles.add("Bob's Song");

        songImageURL.add("https://smiley.com/img/BRAND_SMILEY_MAIN.jpg");
        creatorNames.add("Bob");
        songTitles.add("Bob's Song");

        songImageURL.add("https://smiley.com/img/BRAND_SMILEY_MAIN.jpg");
        creatorNames.add("Bob");
        songTitles.add("Bob's Song");

        songImageURL.add("https://smiley.com/img/BRAND_SMILEY_MAIN.jpg");
        creatorNames.add("Bob");
        songTitles.add("Bob's Song");


        initRecyclerView();
    }

    private void initRecyclerView(){
        songRecyclerView = findViewById(R.id.recycler_view);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, creatorNames, songTitles, songImageURL);
        songRecyclerView.setAdapter(adapter);
        songRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }




}
