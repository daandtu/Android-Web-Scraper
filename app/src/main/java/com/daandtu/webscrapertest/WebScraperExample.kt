package com.daandtu.webscrapertest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import com.daandtu.webscaper.WebScraper

class WebScraperExample : AppCompatActivity() {

    private lateinit var webScraper: WebScraper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_scraper_example)
        val parentLayout = findViewById<LinearLayout>(R.id.test_layout)

        val webScraper = WebScraper(this)
        parentLayout.addView(webScraper)
    }
}