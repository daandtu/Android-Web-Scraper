package com.daandtu.webscraper;

public class Element {

    private String elementLocator;
    private int type, count;
    private WebScraper web;

    public static final int NAME = 1, XPATH = 2, CLASS = 3, ID = 4, VALUE = 5, TITLE = 6, TAG = 7, TYPE = 8, ATTRIBUTE = 9, HTML = 10, CUSTOM = 0;

    public Element (WebScraper web, int type, String elementLocator, int count){
        this.web = web;
        this.elementLocator = elementLocator;
        this.type = type;
        this.count = count;
    }

    protected String getElement(){
        switch (this.type) {
            case NAME:
                return "document.getElementsByName(\"" + elementLocator + "\")[" + count + "]";
            case XPATH:
                return "document.evaluate(\"" + elementLocator +
                        "\", document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue";
            case CLASS:
                return "document.getElementsByClassName(\"" + elementLocator + "\")[" + count + "]";
            case ID:
                return "document.getElementById(\"" + elementLocator + "\")";
            case VALUE:
                return "document.querySelectorAll(\"[value=" + elementLocator + "]\")[" + count + "]";
            case TITLE:
                return "document.querySelectorAll(\"[title=" + elementLocator + "]\")[" + count + "]";
            case TAG:
                return "document.getElementsByTagName(\"" + elementLocator + "\")";
            case TYPE:
                return "document.querySelectorAll(\"[type=" + elementLocator + "]\")[" + count + "]";
            case ATTRIBUTE:
                return "document.querySelectorAll(\"[" + elementLocator + "]\")[" + count + "]";
            case HTML:
                return "document.evaluate(\"" + elementLocator +
                    "\", document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue";
            case CUSTOM:
                return elementLocator;
            default:
                return null;
        }
    }

    public void setAttribute(String attribute, String value){
        web.addTask("javascript:" + getElement() + ".setAttribute(\"" + attribute + "\",\"" + value + "\");void(0);");
    }

    public void setText(String text){
        web.addTask("javascript:" + getElement() + ".innerHTML = \"" + text + "\";void(0);");
    }
    public void inputText(String text){
        setAttribute("value",text);
    }
    public void setBackgroundColor(String color){  // hex value or color name
        web.addTask("javascript:" + getElement() + ".style.backgroundColor = \"" + color + "\";void(0);");
    }
    public void click(){
        web.addTask("javascript:" + getElement() + ".click();void(0);");
    }
    public void getAttribute(String attribute, String key){
        web.addTask(getElement() + "."+ attribute,true, key);
    }
    public void getName(String key){
        getAttribute("name", key);
    }
    public void focus(){
        web.addTask("javascript:" + getElement() + ".focus();void(0);");
        web.addTask("javascript:" + getElement() + ".focus();void(0);");
    }
    public void getTitle(String key){
        getAttribute("title", key);
    }
    public void getText(String key){
        getAttribute("innerHTML", key);
    }
    public void getValue(String key){
        getAttribute("value", key);
    }

}
