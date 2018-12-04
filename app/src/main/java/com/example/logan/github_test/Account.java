package com.example.logan.github_test;

/**
 * Class for logged in account
 */
public class Account {
    private String userName;
    private static String PROFILE_IMAGE_SMALL_URL = "https://steemitimages.com/u/username/avatar/medium";//replace username with actual username

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    public String getImageURL(){
        return PROFILE_IMAGE_SMALL_URL.replace("username",userName);
    }

}
