package com.daandtu.webscraper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

@SuppressLint("SetJavaScriptEnabled")
public class WebScraper {

    private Context context;
    private WebView web;
    private String Html;
    private String URL;

    public static int MAX = 0;

    private onPageLoadedListener onpageloadedlistener;

    WebScraper(final Context context){
        this.context = context;
        web = new WebView(context);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            WebView.enableSlowWholeDocumentDraw();
        }
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setBlockNetworkImage(true);
        web.getSettings().setLoadsImagesAutomatically(false);
        JSInterface jInterface = new JSInterface(context);
        web.addJavascriptInterface(jInterface, "HtmlViewer");
        web.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {

                if (onpageloadedlistener!=null){
                    onpageloadedlistener.loaded(url);
                }

                web.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                web.layout(0, 0, web.getMeasuredWidth(), web.getMeasuredHeight());
                web.setDrawingCacheEnabled(true);
            }
        });
    }


    public void run(){
    }

    public Bitmap takeScreenshot() {
        return takeScreenshot(MAX,MAX);
    }

    public Bitmap takeScreenshot(int width, int height) {
        web.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        if (width == 0){
            width = web.getMeasuredWidth();
        }
        if (height == 0){
            height = web.getMeasuredHeight();
        }
        web.layout(0, 0, width, height);
        web.setDrawingCacheEnabled(true);
        try { Thread.sleep(30); }catch (InterruptedException ignored){}
        try { return Bitmap.createBitmap(web.getDrawingCache());
        }catch (NullPointerException ignored){return null;}
    }

    public View getView(){
        return web;
    }

    public String getHtml(){
        //web.loadUrl("javascript:window.HtmlViewer.showHTML(document.getElementsByTagName('html')[0].innerHTML);");
        return Html;
    }

    public void clearHistory(){
        web.clearHistory();
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


    private class JSInterface {

        private Context ctx;

        JSInterface(Context ctx) {
            this.ctx = ctx;
        }

        @JavascriptInterface
        public void showHTML(String html) {
            Html = html;
        }
    }


    public void setOnPageLoadedListener(onPageLoadedListener onpageloadedlistener){
        this.onpageloadedlistener = onpageloadedlistener;
    }

    public interface onPageLoadedListener{
        void loaded(String URL);
    }
}