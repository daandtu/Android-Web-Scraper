package com.daandtu.webscraper;

class Element {

    private String elementLocator;
    private WebScraper web;

    Element (WebScraper web, String elementLocator){
        this.web = web;
        this.elementLocator = elementLocator;

    }

    void setText(String text){
        web.run("javascript:" + elementLocator + ".value = '" + text + "';void(0);");
    }

    void click(){
        web.run("javascript:" + elementLocator + ".click();void(0);");
    }

}
