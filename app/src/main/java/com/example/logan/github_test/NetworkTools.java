package com.example.logan.github_test;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import eu.bittrade.libs.steemj.SteemJ;
import eu.bittrade.libs.steemj.apis.database.models.state.Discussion;
import eu.bittrade.libs.steemj.base.models.AccountName;
import eu.bittrade.libs.steemj.base.models.DiscussionQuery;
import eu.bittrade.libs.steemj.base.models.VoteState;
import eu.bittrade.libs.steemj.enums.DiscussionSortType;
import eu.bittrade.libs.steemj.exceptions.SteemCommunicationException;
import eu.bittrade.libs.steemj.exceptions.SteemResponseException;

class NetworkTools {

    private static String IPFS_URL = "https://gateway.ipfs.io/ipfs/";
    static final int TAG_TRENDING = 0;
    static final int TAG_HOT = 1;
    static final int TAG_NEW = 2;
    static final int TAG_FEED = 3;

    //number of songs to retrieve at a time
    private static final int SONG_RETRIEVE_LIMIT = 20;

    /**
     * method gets a list of songs from server with specific tag.
     * method must be run in a non UI thread
     * @param steemJ client
     * @param tag TAG_TRENDING, TAG_HOT, TAG_NEW, or TAG_FEED
     * @param lastSong last songLoading in list so that more songs can be loaded than the limit.
     *                     param can be null for fetching the first items
     * @param c android context
     * @return ArrayList of songs
     * @throws SteemResponseException problem with Steem client has occurred
     * @throws SteemCommunicationException problem with Steem client has occurred
     */
    static ArrayList<Song> getDsoundSongs(SteemJ steemJ, int tag, Song lastSong, Context c) throws SteemResponseException, SteemCommunicationException {

        ArrayList<Song> songs = new ArrayList<>();

        DiscussionQuery discussionQuery = new DiscussionQuery();
        discussionQuery.setTag("dsound");

        if (lastSong!=null) {
            Log.d("ds","setStartPermlink "+lastSong.getTitle());
                discussionQuery.setStartPermlink(lastSong.getPermlink());
                discussionQuery.setStartAuthor(lastSong.getAuthor());
        }


        discussionQuery.setLimit(SONG_RETRIEVE_LIMIT);

        List<Discussion> discussions ;
        switch (tag){
            case TAG_TRENDING:
                 discussions = steemJ.getDiscussionsBy(discussionQuery, DiscussionSortType.GET_DISCUSSIONS_BY_TRENDING);
                break;
            case TAG_HOT:
                discussions = steemJ.getDiscussionsBy(discussionQuery, DiscussionSortType.GET_DISCUSSIONS_BY_HOT);
                break;
            case TAG_NEW:
                discussions = steemJ.getDiscussionsBy(discussionQuery, DiscussionSortType.GET_DISCUSSIONS_BY_CREATED);
                break;
            default:
                discussions = steemJ.getDiscussionsBy(discussionQuery, DiscussionSortType.GET_DISCUSSIONS_BY_FEED);
                break;
        }



        boolean failedAdding;
        for (Discussion d: discussions) {
            //prevent adding the same songLoading as the last songLoading in list
            if (lastSong == null || (!d.getPermlink().getLink().equals(lastSong.getPermlink().getLink()))) {
                failedAdding = false;
                Song song = new Song();
                song.setAuthor(d.getAuthor());
                song.setDate(d.getFirstRebloggedOn());
                song.setPermlink(d.getPermlink());
                song.setTitle(d.getTitle());
                song.setTag(tag);

                String appAccountName = Tools.getAccountName(c);

                if (Tools.getAccountName(c)!=null) {
                    for (VoteState v : d.getActiveVotes()) {
                        if (v.getVoter().getName().equals(appAccountName)) {
                            song.setAccountFavoritedSong(true);
                            break;
                        }
                    }
                }

                Log.d("dsjson", d.getJsonMetadata());

                try {

                    JSONObject jObj = new JSONObject(d.getJsonMetadata());

                    song.setImageURL(IPFS_URL + jObj.getJSONObject("audio").getJSONObject("files").getString("cover"));
                    song.setSongURL(IPFS_URL + jObj.getJSONObject("audio").getJSONObject("files").getString("sound"));
                    song.setDuration((int) jObj.getJSONObject("audio").getDouble("duration"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    failedAdding = true;
                }

                if (!failedAdding)
                    songs.add(song);
            }
        }

        return songs;

    }

}
