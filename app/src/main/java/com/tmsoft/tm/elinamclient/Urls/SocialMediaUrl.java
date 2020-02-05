package com.tmsoft.tm.elinamclient.Urls;

public class SocialMediaUrl {
    private String facebook, twitter, telegram, whatsapp;

    public SocialMediaUrl() {
        facebook = "http://www.facebook.com/elinamtrendz/";
        twitter = "http://www.twitter.com/ELINAM_TRENDZGH?s=80.com";
        telegram = "http://t.me/joinchat/AAAAAFQgF57bHS35hV7E0w";
        whatsapp = "https://wa.me/233247684356?text=Hello%20I%20would%20like%20to%20purchase%20an%20item%20from%20you";
    }

    public String getFacebook() {
        return facebook;
    }

    public String getTwitter() {
        return twitter;
    }

    public String getTelegram() {
        return telegram;
    }

    public String getWhatsapp() {
        return whatsapp;
    }
}
