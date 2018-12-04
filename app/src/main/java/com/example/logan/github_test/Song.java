package com.example.logan.github_test;

import java.util.Date;

import eu.bittrade.libs.steemj.base.models.AccountName;
import eu.bittrade.libs.steemj.base.models.Permlink;

public class Song {
    private String title;
    private AccountName author;
    private Date date;
    private Permlink permlink;
    private String imageURL;
    private String songURL;
    private int tag; //TAG_TRENDING, TAG_HOT, TAG_NEW, or TAG_FEED

    public boolean isAccountFavoritedSong() {
        return accountFavoritedSong;
    }

    public void setAccountFavoritedSong(boolean accountFavoritedSong) {
        this.accountFavoritedSong = accountFavoritedSong;
    }

    public boolean getAccountFavoritedSong(){
        return  accountFavoritedSong;
    }

    private boolean accountFavoritedSong;


    public int getTag() {
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

    public Permlink getPermlink() {
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
