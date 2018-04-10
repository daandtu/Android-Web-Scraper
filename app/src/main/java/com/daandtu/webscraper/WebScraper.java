package com.daandtu.webscraper;

import android.content.Context;
import android.webkit.WebView;

public class WebScraper {

    private Context context;
    private WebView web;

    WebScraper(Context context){
        this.context = context;
        web = new WebView(context);
    }
}
