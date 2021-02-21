package com.daandtu.webscraper

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.InflateException
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import kotlinx.coroutines.*
import java.lang.IllegalStateException
import java.security.InvalidParameterException
import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.resume

@SuppressLint("SetJavaScriptEnabled")
open class WebScraper(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : FrameLayout(context, attributeSet, defStyleAttr, defStyleRes) {

    constructor(context: Context, attributeSet: AttributeSet): this(context, attributeSet, 0, 0)
    constructor(context: Context): this(context, null, 0, 0)

    private var webView: WebView
    var defaultTimeout = 10_000L
    private val webViewSettings: WebSettings

    companion object {
        private const val TAG = "WebScraper"
        fun launch(block: suspend CoroutineScope.() -> Unit) {
            CoroutineScope(Dispatchers.Main).launch(EmptyCoroutineContext, CoroutineStart.DEFAULT, block)
        }
    }

    init {
        inflate(context, R.layout.webscraper, this)
        webView = findViewById(R.id.webScraperWebView)
        webView.webViewClient = WebScraperWebViewClient()
        webViewSettings = webView.settings
        webViewSettings.javaScriptEnabled = true
    }

    fun getWebView() : WebView = webView

    suspend fun loadUrl(url: String, additionalHttpHeaders: Map<String, String>? = null) {
        if (additionalHttpHeaders != null) {
            webView.loadUrl(url, additionalHttpHeaders)
        } else {
            webView.loadUrl(url)
        }
        evaluateJavascript("(function(){while (document.readyState !== 'complete') {} })();")
    }

    suspend fun getHtmlContent(): String = evaluateJavascript("(function(){return window.document.body.outerHTML})();")

    private class WebScraperWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
            return false
        }
    }

    private suspend inline fun <T> suspendCoroutineWithTimeout(timeout: Long, crossinline block: (Continuation<T>) -> Unit) = withTimeout(timeout) {
        suspendCancellableCoroutine(block = block)
    }

    private suspend inline fun evaluateJavascript(query: String, timeout: Long = defaultTimeout) : String = suspendCoroutineWithTimeout(timeout) { continuation ->
        webView.evaluateJavascript(query) { result -> continuation.resume(result) }
    }


    // Element classes and methods

    fun getElementById(id: String): Element = Element(this, BaseElement.IdentifierType.ID, id)
    fun getElementByClass(className: String): Element = Element(this, BaseElement.IdentifierType.CLASS, className)
    fun getElementByName(name: String): Element = Element(this, BaseElement.IdentifierType.NAME, name)
    fun getElementByXpath(xpath: String): Element = Element(this, BaseElement.IdentifierType.XPATH, xpath)

    abstract class BaseElement(private val webScraper: WebScraper, val identifierType: IdentifierType, val identifier: String) {
        enum class IdentifierType {
            ID, CLASS, NAME, XPATH
        }
        protected fun ensureSecureValueString(input: String): String {
            return input
                    .replace("""[\t\r\n]""".toRegex(), "")
                    .replace("\"", "&quot;")
                    .replace("&", "&amp;")
                    .trim()
        }
        protected fun ensureSecureAttributeString(input: String) : String {
            return input.replace("""[^a-zA-Z]""".toRegex(), "")
        }
    }

    open class Element internal constructor(private val webScraper: WebScraper, identifierType: IdentifierType, identifier: String) : BaseElement(webScraper, identifierType, identifier){
        private fun constructJavascriptIdentifier() : String {
            return when(identifierType) {
                IdentifierType.ID -> "document.getElementById(\"${ensureSecureValueString(identifier)}\")"
                IdentifierType.CLASS -> "document.getElementsByClassName(\"${ensureSecureValueString(identifier)}\")[0]"
                IdentifierType.NAME -> "document.getElementsByName(\"${ensureSecureValueString(identifier)}\")[0]"
                IdentifierType.XPATH -> "(function getElementsByXPath(){let results = []; let query = document.evaluate(\"${ensureSecureValueString(identifier)}\", document, null, XPathResult.ORDERED_NODE_SNAPSHOT_TYPE, null); for (let i = 0, length = query.snapshotLength; i < length; ++i) { results.push(query.snapshotItem(i)); } return results;})"
            }
        }
        suspend fun checkIfExists() : Boolean {
            return webScraper.evaluateJavascript(
                    "(function(){var element = ${constructJavascriptIdentifier()}; return (typeof(element) != 'undefined' && element != null)})();"
            ) == "true"
        }
    }

    fun getElementListByClass(className: String): ElementList = ElementList(this, BaseElement.IdentifierType.CLASS, className)
    fun getElementListByName(name: String): ElementList = ElementList(this, BaseElement.IdentifierType.NAME, name)
    fun getElementListByXpath(xpath: String): ElementList = ElementList(this, BaseElement.IdentifierType.XPATH, xpath)

    class ElementList internal constructor(private val webScraper: WebScraper, identifierType: IdentifierType, identifier: String) : Element(webScraper, identifierType, identifier) {
         private fun constructJavascriptIdentifier() : String {
            return when(identifierType) {
                IdentifierType.CLASS -> "document.getElementsByClassName(\"${ensureSecureValueString(identifier)}\")"
                IdentifierType.NAME -> "document.getElementsByName(\"${ensureSecureValueString(identifier)}\")"
                IdentifierType.XPATH -> "document.evaluate(\"${ensureSecureValueString(identifier)}\", document, null, XPathResult.ORDERED_NODE_SNAPSHOT_TYPE, null)"
                else -> throw InvalidParameterException("Unsupported IdentifierType for ElementList")
            }
        }
        suspend fun count(): Int {
            return webScraper.evaluateJavascript("${constructJavascriptIdentifier()}.length").toIntOrNull() ?: 0
        }
    }

}
