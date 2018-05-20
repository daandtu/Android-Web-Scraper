# android-web-scraper
Android Web Scraper is a simple library for android web automation.
# Usage
Initialisation and setup possibilities:
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
OnPageLoadedListener:
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
Bitmap screenshot = webScraper.takeScreenshot(); //Pay attention with big webpages
```
```
String title = webScraper.getWebsiteTitle();
```
```
String html = webScraper.getHtml();
```
```
webScraper.clearHistory();
webScraper.clearCache();
webScraper.clearCookies();
webScraper.clearAll(); //Clear history, cache and cookies
```
```
webScraper.reload();
```
WebPage Elements:
```
Element el1 = webScraper.findElementByXpath("//*[@id=\"search\"]");
Element el2 = webScraper.findElementByName("img",3);
el1.setText("Android");
el2.click();
Element el3 = webScraper.findElementById("result");
String result = el3.getValue();
```
