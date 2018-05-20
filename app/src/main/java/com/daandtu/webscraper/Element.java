package com.daandtu.webscraper;

import android.util.Log;

class Element {

    private String elementLocator;
    private WebScraper web;

    public Element (WebScraper web, String elementLocator){
        this.web = web;
        this.elementLocator = elementLocator;

    }

    public void setText(String text){
        String t = "javascript:" + elementLocator + ".value='" + text + "';void(0);";
        Log.i("Logmsg",t);
        web.run(t);
    }

    public void click(){
        web.run("javascript:" + elementLocator + ".click();void(0);");
    }

    public String getText(){
        return web.run2("javascript:window.HtmlViewer.processContent(" + elementLocator + ".innerText);");
    }

    public String getValue(){
        return web.run2("javascript:window.HtmlViewer.processContent(" + elementLocator + ".value);");
    }

    public String getName(){
        return web.run2("javascript:window.HtmlViewer.processContent(" + elementLocator + ".name);");
    }

}
