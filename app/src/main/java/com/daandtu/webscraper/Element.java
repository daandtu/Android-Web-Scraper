package com.daandtu.webscraper;

import android.util.Log;

class Element {

    private String elementLocator;
    private WebScraper web;

    Element (WebScraper web, String elementLocator){
        this.web = web;
        this.elementLocator = elementLocator;

    }

    void setText(String text){
        String t = "javascript:" + elementLocator + ".value='" + text + "';void(0);";
        Log.i("Logmsg",t);
        web.run(t);
    }

    void click(){
        web.run("javascript:" + elementLocator + ".click();void(0);");
    }

}
