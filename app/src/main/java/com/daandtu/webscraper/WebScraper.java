package com.daandtu.webscraper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

@SuppressLint("SetJavaScriptEnabled")
public class WebScraper {

    private Context context;
    private WebView web;
    private String Html;
    private String URL;

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
                view.loadUrl("javascript:window.HtmlViewer.showHTML" +
                        "(document.getElementsByTagName('html')[0].innerHTML);");
            }
        });
    }

    public void run(){

    }

    public Bitmap takeScreenshot(){
        web.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        web.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        web.layout(0, 0, web.getMeasuredWidth(), web.getMeasuredHeight());
        Bitmap bitmap = Bitmap.createBitmap(web.getMeasuredWidth(), web.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
        web.draw(c);
        return bitmap;
    }

    public Bitmap takeScreenshot(int maxWidthOrHeight){
        return resizeBitmap(takeScreenshot(),maxWidthOrHeight);
    }

    public View getView(){
        return web;
    }

    public String getHtml(){
        return Html;
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


    private Bitmap resizeBitmap(Bitmap bitmap, int scaleSize) {
        Bitmap resizedBitmap;
        int originalWidth = bitmap.getWidth(), originalHeight = bitmap.getHeight(), newWidth = -1, newHeight = -1;
        float fac;
        if(originalHeight > originalWidth) {
            newHeight = scaleSize ;
            fac = (float) originalWidth/(float) originalHeight;
            newWidth = (int) (newHeight*fac);
        } else if(originalWidth > originalHeight) {
            newWidth = scaleSize ;
            fac = (float) originalHeight/ (float)originalWidth;
            newHeight = (int) (newWidth*fac);
        } else if(originalHeight == originalWidth) {
            newHeight = scaleSize ;
            newWidth = scaleSize ;
        }
        resizedBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, false);
        return resizedBitmap;
    }

    public void setOnPageLoadedListener(onPageLoadedListener onpageloadedlistener){
        this.onpageloadedlistener = onpageloadedlistener;
    }

    public interface onPageLoadedListener{
        void loaded(String URL);
    }
}