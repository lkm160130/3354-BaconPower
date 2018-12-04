package com.example.logan.github_test;

import java.util.Date;

import eu.bittrade.libs.steemj.base.models.AccountName;
import eu.bittrade.libs.steemj.base.models.Permlink;


/**
 * Class that holds information for a song.
 * Contains information such as title, image url,
 * date, and permlink.
 * Each item in recyclerview is generated from
 * an instantiated Song class
 */
public class Song {
    private String title;
    private AccountName author;
    private Date date;
    private Permlink permlink;
    private String imageURL;
    private String songURL;
    private int tag; //TAG_TRENDING, TAG_HOT, TAG_NEW, or TAG_FEED

    boolean isAccountFavoritedSong() {
        return accountFavoritedSong;
    }

    void setAccountFavoritedSong(boolean accountFavoritedSong) {
        this.accountFavoritedSong = accountFavoritedSong;
    }

    boolean getAccountFavoritedSong(){
        return  accountFavoritedSong;
    }

    private boolean accountFavoritedSong;


    int getTag() {
        return tag;
    }

    void setTag(int tag) {
        this.tag = tag;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    private int duration;

    String getSongURL(){
        return songURL;
    }

    void setSongURL(String songURL){
        this.songURL = songURL;
    }

    String getImageURL() {
        return imageURL;
    }

    void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    Permlink getPermlink() {
        return permlink;
    }

    void setPermlink(Permlink permlink) {
        this.permlink = permlink;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    AccountName getAuthor() {
        return author;
    }

    void setAuthor(AccountName author) {
        this.author = author;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
