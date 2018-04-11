package com.daandtu.webscraper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.webkit.WebView;

@SuppressLint("SetJavaScriptEnabled")
public class WebScraper {

    private Context context;
    private WebView web;
    private String URL;

    WebScraper(Context context){
        this.context = context;
        web = new WebView(context);
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setBlockNetworkImage(true);
        web.getSettings().setLoadsImagesAutomatically(false);
    }

    public View getView(){
        return web;
    }

    public void setJavaScriptEnabled(boolean enabled){
        web.getSettings().setJavaScriptEnabled(enabled);
    }

    public void setLoadImages(boolean enabled){
        web.getSettings().setBlockNetworkImage(false);
        web.getSettings().setLoadsImagesAutomatically(true);
    }

    public void loadURL(String URL){
        this.URL = URL;
        web.loadUrl(URL);
    }

    public String getURL() {
        return URL;
    }

    public void reload(){
        web.reload();
    }
}


