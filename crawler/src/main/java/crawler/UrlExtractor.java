package crawler;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.exceptions.ParseException;
import edu.uci.ics.crawler4j.parser.NotAllowedContentException;
import edu.uci.ics.crawler4j.parser.Parser;
import edu.uci.ics.crawler4j.url.URLCanonicalizer;
import edu.uci.ics.crawler4j.url.WebURL;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by qinyu on 2017-10-31.
 */
public class UrlExtractor {
    private static Parser parser;

    public UrlExtractor() throws IllegalAccessException, InstantiationException {
        parser = new Parser(new CrawlConfig());
    }

    public static void main(String[] args) throws IOException, NotAllowedContentException, ParseException, InterruptedException {

        System.setProperty("webdriver.gecko.driver", "C:\\Program Files\\geckodriver\\geckodriver.exe");
        WebDriver webDriver = new FirefoxDriver();
        webDriver.manage().timeouts().pageLoadTimeout(2, TimeUnit.SECONDS);
        webDriver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
        String baseUri = "https://www.centris.ca";
//        String baseUri = "https://duproprio.com";
        WebURL seed = generateSeed("https://www.centris.ca/en/properties~for-sale~montreal-island?view=List&uc=0");
//        WebURL seed = generateSeed("https://duproprio.com/en/search/list?search=true&regions%5B0%5D=6&is_for_sale=1&with_builders=1&parent=1&pageNumber=1&sort=-published_at");
//        WebDriverWait wait = new WebDriverWait(webDriver, 5);
        try {
            webDriver.get(seed.getURL());
        }catch (Exception e){}
//        webDriver.get("https://www.centris.ca/en/duplexes~for-sale~cote-des-neiges-notre-dame-de-grace-montreal/17035183?view=Summary&uc=1");
        Document document = Jsoup.parse(webDriver.getPageSource());


        Set<WebURL> urls = new HashSet<>();
        while(true) {
            document = Jsoup.parse(webDriver.getPageSource());
            document.setBaseUri(baseUri);
            Elements elements = document.select("#divMainResult > div > a.btn.a-more-detail");
//            Elements elements = document.select("div.search-results-listings-list__item-bottom-container > a");
            if (elements != null) {
                for (Element element : elements) {
                    Elements outgoingElems = element.getElementsByAttribute("href");
                    if(outgoingElems != null && !outgoingElems.isEmpty()) {
                        for (Element outgoingElem : outgoingElems) {
                            WebURL webURL = new WebURL();
                            String outgoingLink = outgoingElem.absUrl("href");
                            if (outgoingLink.equals("")) {
                                continue;
                            }
                            webURL.setURL(outgoingLink);
                            webURL.setTag(outgoingElem.tagName());
                            webURL.setAnchor(outgoingElem.text());
                            webURL.setParentUrl(seed.getURL());
                            webURL.setDepth((short) (2));
                            urls.add(webURL);
                        }
                    }
                }
            }
            writeCSV(urls);
            urls.clear();

            try {
//                try {
//                    webDriver.findElements(By.cssSelector(".info-sessions-popup__close-icon")).get(0).click();
//                }catch (Exception e){}
            webDriver.findElements(By.cssSelector("#divWrapperPager > ul > li.next > a")).get(0).click();
//                webDriver.findElements(By.cssSelector("#react-component-SearchPagination > nav > div.pagination__arrow.pagination__arrow--right")).get(0).click();
//        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".model-name")));
            }catch (Exception e){

            }
        }
    }

    private static WebURL generateSeed(String pageUrl) {
        WebURL webUrl = new WebURL();
        String seedCanonicalUrl = URLCanonicalizer.getCanonicalURL(pageUrl);
        if (seedCanonicalUrl == null) {
        } else {
            webUrl.setURL(seedCanonicalUrl);
            webUrl.setDepth((short) 0);
        }
        return webUrl;
    }

    private static void writeCSV(Set<WebURL> urls)  {
        String fileFullPathName = "centris_url.csv";
        try (PrintWriter printWriter = new PrintWriter(new FileWriter(fileFullPathName, true))){

            for (WebURL webURL : urls) {
                printWriter.println(webURL.getURL());
            }
        } catch (IOException e) {}
    }
}

