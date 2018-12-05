package com.example.logan.github_test;

import android.content.Context;
import android.net.Network;
import android.support.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;

import eu.bittrade.libs.steemj.SteemJ;
import eu.bittrade.libs.steemj.exceptions.SteemCommunicationException;
import eu.bittrade.libs.steemj.exceptions.SteemResponseException;

import static org.junit.Assert.*;

public class NetworkToolsTest {

    //use the below commented code to access the activity for unit testing
     @Rule
     public ActivityTestRule<MainActivity> nActivityRule =
     new ActivityTestRule(MainActivity.class);

     MainActivity networkActivity;



    @Before
    public void setUp() throws Exception {
         networkActivity = nActivityRule.getActivity();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getDsoundSongs() {
        networkActivity.getBaseContext();
        try {
            ArrayList<Song> songs = NetworkTools.getDsoundSongs(new SteemJ(), NetworkTools.TAG_TRENDING, null, networkActivity.getBaseContext());
            assertNotNull(songs);
            for(int i = 0; i < songs.size(); i++){
                assertNotNull(songs.get(i));
                assertTrue(songs.get(i) instanceof Song);
                assertNotNull(songs.get(i).getAuthor());
                assertNotNull(songs.get(i).getImageURL());
                assertNotNull(songs.get(i).getSongURL());
                assertNotNull(songs.get(i).getTitle());
            }

        } catch (SteemResponseException e) {
            e.printStackTrace();
        } catch (SteemCommunicationException e) {
            e.printStackTrace();
        }

    }
}