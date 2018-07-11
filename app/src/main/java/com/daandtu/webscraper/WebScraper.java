package com.daandtu.webscraper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;

@SuppressLint("SetJavaScriptEnabled,unused")
public class WebScraper {

    private Context context;
    private WebView web;

    private volatile boolean htmlBool;
    private String Html;
    private volatile boolean gotElementText = true;
    private String elementText;

    private String URL;
    private String userAgent;

    private Handler handler;

    public static int MAX = -1;

    private onPageLoadedListener onpageloadedlistener;

    public WebScraper(final Context context) {
        this.context = context;
        web = new WebView(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            WebView.enableSlowWholeDocumentDraw();
        }
        handler = new Handler();
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setBlockNetworkImage(true);
        web.getSettings().setLoadsImagesAutomatically(false);
        JSInterface jInterface = new JSInterface(context);
        web.addJavascriptInterface(jInterface, "HtmlViewer");
        userAgent = web.getSettings().getUserAgentString();
        web.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {

                if (onpageloadedlistener != null) {
                    onpageloadedlistener.loaded(url);
                }

                web.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                web.layout(0, 0, web.getMeasuredWidth(), web.getMeasuredHeight());
                web.setDrawingCacheEnabled(true);
            }
        });
    }

    public Bitmap takeScreenshot() { //Pay attention with big webpages
        return takeScreenshot(MAX, MAX);
    }

    public void setUserAgentToDesktop(boolean desktop){
        if (desktop){
            String osString = userAgent.substring(userAgent.indexOf("("), userAgent.indexOf(")") + 1);
            web.getSettings().setUserAgentString(userAgent.replace(osString,"(X11; Linux x86_64)"));
        }else{
            web.getSettings().setUserAgentString(userAgent);
        }
    }

    public Bitmap takeScreenshot(int width, int height) {
        if (width < 0 || height < 0) {
            web.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        }
        if (width < 0) {
            width = web.getMeasuredWidth();
        }
        if (height < 0) {
            height = web.getMeasuredHeight();
        }
        web.layout(0, 0, width, height);
        web.setDrawingCacheEnabled(true);
        try {
            Thread.sleep(30);
        } catch (InterruptedException ignored) {
        }
        try {
            return Bitmap.createBitmap(web.getDrawingCache());
        } catch (NullPointerException ignored) {
            return null;
        }
    }

    public int getMaxHeight() {
        web.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        return web.getMeasuredHeight();
    }

    public int getMaxWidth() {
        web.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        return web.getMeasuredWidth();
    }

    public View getView() {
        return web;
    }

    public String getWebsiteTitle(){
        return web.getTitle();
    }

    public String getHtml() {
        htmlBool = true;
        Html = null;
        web.evaluateJavascript("javascript:window.HtmlViewer.showHTML(document.getElementsByTagName('html')[0].innerHTML);", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String s) {
                htmlBool = false;
            }
        });
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                htmlBool = false;
            }
        },15);
        while (htmlBool) {}
        return Html;
    }

    public void clearHistory() {
        web.clearHistory();
    }
    public void clearCache() {
        web.clearCache(true);
    }
    public void clearCookies(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        }else{
            CookieSyncManager cookieSync = CookieSyncManager.createInstance(context);
            cookieSync.startSync();
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSync.stopSync();
            cookieSync.sync();
        }
    }
    public void clearAll(){
        clearHistory();
        clearCache();
        clearCookies();
    }

    public void setLoadImages(boolean enabled) {
        web.getSettings().setBlockNetworkImage(!enabled);
        web.getSettings().setLoadsImagesAutomatically(enabled);
    }

    public void loadURL(String URL) {
        this.URL = URL;
        web.loadUrl(URL);
    }

    public String getURL() {
        return web.getUrl();
    }

    public void reload() {
        web.reload();
    }


    private class JSInterface {

        private Context ctx;

        JSInterface(Context ctx) {
            this.ctx = ctx;
        }

        @JavascriptInterface
        public void showHTML(String html) {
            Html = html;
            htmlBool = false;
        }

        @JavascriptInterface
        public void processContent(String elText){
            elementText = elText;
            gotElementText = true;
        }
    }

    protected void run(String task){
        web.loadUrl(task);
    }

    protected String run2(String task){
        while (!gotElementText){}
        elementText = null;
        gotElementText = false;
        web.evaluateJavascript(task, new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String s) {
                gotElementText = true;
            }
        });
        while(!gotElementText){}
        return elementText;
    }



    //FindWebViewElement
    public Element findElementByClassName(String classname, int id){
        return new Element(this, "document.getElementsByClassName('" + classname + "')[" + String.valueOf(id) + "]");
    }
    public Element findElementByClassName(String classname){
        return findElementByName(classname, 0);
    }
    public Element findElementById(String id){
        return new Element(this, "document.getElementById('" + id + "')" );
    }
    public Element findElementByName(String name, int id){
        return new Element(this, "document.getElementsByName('" + name + "')[" + String.valueOf(id) + "]" );
    }
    public Element findElementByName(String name){
        return findElementByName(name,0);
    }
    public Element findElementByXpath(String xpath){
        return new Element(this, "document.evaluate(" + xpath + ", document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue" );
    }
    public Element findElementByJavaScript(String javascript){
        return new Element(this, javascript);
    }
    public Element findElementByValue(String value, int id){
        return new Element(this, "document.querySelectorAll('[value=\"" + value + "\"]')[" + String.valueOf(id) + "]");
    }
    public Element findElementByValue(String value){
        return findElementByValue(value,0);
    }
    public Element findElementByTitle(String title, int id){
        return new Element(this, "document.querySelectorAll('[title=\"" + title + "\"]')[" + String.valueOf(id) + "]");
    }
    public Element findElementByTitle(String title){
        return findElementByTitle(title,0);
    }
    public Element findElementByTagName(String tagName, int id){
        return new Element(this, "document.getElementsByTagName('" + tagName + "')[" + String.valueOf(id) + "]");
    }
    public Element findElementByTagName(String tagName){
        return findElementByTagName(tagName,0);
    }
    public Element findElementByType(String type, int id){
        return new Element(this, "document.querySelectorAll('[type=\"" + type + "\"]')[" + String.valueOf(id) + "]");
    }
    public Element findElementByType(String type){
        return findElementByType(type,0);
    }


    public void setOnPageLoadedListener(onPageLoadedListener onpageloadedlistener){
        this.onpageloadedlistener = onpageloadedlistener;
    }

    public interface onPageLoadedListener{
        void loaded(String URL);
    }
}