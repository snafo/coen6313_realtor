package crawler;

import crawler.crawler.exceptions.ParseException;
import crawler.frontier.Frontier;
import crawler.parser.NotAllowedContentException;
import crawler.url.URLCanonicalizer;
import crawler.url.WebURL;
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
 * Created by qinyu on 2017-11-23.
 */
public class Extractor implements Runnable{
    private WebDriver webDriver;
    private Frontier frontier;
    private WebURL seed;
    private int failedTime;
    private int threshold;

    public Extractor(Frontier frontier) throws IllegalAccessException, InstantiationException {
        System.setProperty("webdriver.gecko.driver", "C:\\Program Files\\geckodriver\\geckodriver.exe");
        webDriver = new FirefoxDriver();
        this.frontier = frontier;
        failedTime = 0;
        threshold = 10;
        seed = generateSeed("https://www.centris.ca/en/properties~for-sale~montreal-island?view=List&uc=0");
//        seed = generateSeed("https://duproprio.com/en/search/list?search=true&regions%5B0%5D=6&is_for_sale=1&with_builders=1&parent=1&pageNumber=1&sort=-published_at");
    }

    public void run(){
        try {
            extractUrl(seed);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NotAllowedContentException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void extractUrl(WebURL seed) throws IOException, NotAllowedContentException, ParseException, InterruptedException {
        webDriver.manage().timeouts().pageLoadTimeout(5, TimeUnit.SECONDS);
        webDriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        String baseUri = "https://www.centris.ca";
//        String baseUri = "https://duproprio.com";
        try {
            webDriver.get(seed.getURL());
        }catch (Exception e){}
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
                            if (!urls.contains(webURL)){
                                urls.add(webURL);
                                frontier.schedule(webURL);
                            }
                        }
                    }
                }
                failedTime = 0;
            }
//            writeCSV(urls);
//            urls.clear();

            try {
//                try {
//                    webDriver.findElements(By.cssSelector(".info-sessions-popup__close-icon")).get(0).click();
//                }catch (Exception e){}
//                try {
//                    webDriver.findElements(By.cssSelector(".email-alerts-form__close-icon")).get(0).click();
//                }catch (Exception e){}
                webDriver.findElements(By.cssSelector("#divWrapperPager > ul > li.next > a")).get(0).click();
//                webDriver.findElements(By.cssSelector("#react-component-SearchPagination > nav > div.pagination__arrow.pagination__arrow--right")).get(0).click();
                Thread.sleep(1000);
            }catch (Exception e){
                if (++failedTime >= threshold){
                    webDriver.close();
                    break;
                }
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
