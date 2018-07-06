# android-web-scraper
Android Web Scraper is a simple library for android web automation. You can perform web task in background to fetch website data programmatically.

# Setup
Add project repository to *build.gradle*:
```
repositories {
    maven {
        url "https://dl.bintray.com/daandtu/maven"
          }
}
```
and the following to your dependencies:
```
compile 'com.daandtu:android-web-scraper:1.0.0'
```
Add internet permission to *AndroidManifest.xml*:
```
<uses-permission android:name="android.permission.INTERNET"/>
```

# Sample usage
Initialisation:
```
webScraper = new WebScraper(this);
webScraper.setUserAgentToDesktop(true); //default: false
webScraper.setLoadImages(true); //default: false
webScraper.loadURL("https://www.github.com/");
```
If you want to see the browser automation in action:
```
layout.addView(webScraper.getView());
```
Interact with webpage elements:
```
Element el1 = webScraper.findElementByXpath("//*[@id=\"search\"]");
Element el2 = webScraper.findElementByName("img",3);
el1.setText("Android");
el2.click();
Element el3 = webScraper.findElementById("result");
String result = el3.getValue();
```
Setup *OnPageLoadedListener*:
```
webScraper.setOnPageLoadedListener(new WebScraper.onPageLoadedListener() {
            @Override
            public void loaded(String URL) {
                //TODO
            }
        });
```
Other methods:
```
Bitmap screenshot = webScraper.takeScreenshot(); //Pay attention with big webpages or use
Bitmap screenshot2 = webScraper.takeScreenshot(500,MAX);

String title = webScraper.getWebsiteTitle();

String html = webScraper.getHtml();

webScraper.clearHistory();
webScraper.clearCache();
webScraper.clearCookies();
webScraper.clearAll(); //Clear history, cache and cookies

webScraper.reload();
```
