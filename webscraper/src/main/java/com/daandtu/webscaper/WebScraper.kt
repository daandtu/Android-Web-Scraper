package com.daandtu.webscaper

import android.content.Context
import android.util.AttributeSet
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class WebScraper(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : LinearLayout(context) {

    constructor(context: Context, attributeSet: AttributeSet): this(context, attributeSet, 0, 0)
    constructor(context: Context): this(context, null, 0, 0)

    public val webView = WebView(context, attributeSet, defStyleAttr, defStyleRes)

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private var pageFinishedListener: PageFinishedListener? = null

    init {
        addView(webView)
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
            }
        }
    }

    suspend fun loadUrl(url: String, additionalHttpHeaders: Map<String, String>? = null) = suspend {
        if (additionalHttpHeaders != null) {
            webView.loadUrl(url, additionalHttpHeaders)
        } else {
            webView.loadUrl(url)
        }
    }

    suspend fun getHtmlContent(): String = suspendCoroutine {
        webView.evaluateJavascript("(function(){return window.document.body.outerHTML})();") { content -> it.resume(content) }
    }

    private interface PageFinishedListener {
        fun onPageFinished()
    }

}