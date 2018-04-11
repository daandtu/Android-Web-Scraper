package com.daandtu.webscraper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Build;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

@SuppressLint("SetJavaScriptEnabled")
public class WebScraper {

    private Context context;
    private WebView web;
    private String URL;

    WebScraper(Context context){
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
                URL = url;
                run(view);
            }
        });
    }

    private void run(WebView view){

    }

    public Bitmap takeScreenshot(View parentView){
        //TODO
        int specWidth = View.MeasureSpec.makeMeasureSpec(parentView.getWidth(), View.MeasureSpec.AT_MOST);
        int specHeight = View.MeasureSpec.makeMeasureSpec(parentView.getHeight(), View.MeasureSpec.AT_MOST);
        web.measure(specWidth, specHeight);
        Bitmap b = Bitmap.createBitmap(parentView.getWidth(), parentView.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        web.layout(0, 0, web.getMeasuredWidth(), web.getMeasuredHeight());
        web.draw(c);
        return b;
    }

    public View getView(){
        return web;
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

        }
    }
}