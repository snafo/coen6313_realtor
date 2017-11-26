package crawler;

import com.google.gson.Gson;
import crawler.entity.Broker;
import crawler.entity.PropertyEntity;
import crawler.url.WebURL;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by qinyu on 2017-11-23.
 */
public class Processor {
    Gson gson;
    WebDriver webDriver;
    String source;
    String outFileName;

    public Processor(){
        gson = new Gson();
        System.setProperty("webdriver.gecko.driver", "C:\\Program Files\\geckodriver\\geckodriver.exe");
        webDriver = new FirefoxDriver();
        webDriver.manage().timeouts().pageLoadTimeout(2, TimeUnit.SECONDS);
        webDriver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);


        source = "centris";
        outFileName = "centris_info.csv";
//        source = "duproprio";
//        outFileName = "duproprio_info.json";
    }

    public void process(WebURL curURL){
        Wait<WebDriver> wait = new WebDriverWait(webDriver, 5);
        Document document;
        PropertyEntity property = new PropertyEntity();
        try {
            try {
                webDriver.get(curURL.getURL());
            }catch (Exception e){}
            document = Jsoup.parse(webDriver.getPageSource());
            property.setSource(source);
            property.setUrl(curURL.getURL());
            getCentrisProperty(document, property);
//                getDuproprioProperty(document, property);
            if (property.getAddress() != null){
                writeJson(gson.toJson(property),outFileName);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        if (property.getAddress() == null){
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
    }

    private PropertyEntity getCentrisProperty(Document document, PropertyEntity property){
        String typeSelector = "#overview > div.grid_3 > div > h1 > span:nth-child(1)";
        String addressSelector = "#overview > div.grid_3 > div > div.address > h2";
        String priceSelector = "#BuyPrice";
        String roomSelector = ".teaser";
        String discriptionSelector = "div[itemprop=\"description\"]";
        String tableSelector = "#overview > div.grid_3 > div > table";
        String yearRegex = "td-tag Year built td-tag td-tag (.+?) td-tag";
        String areaRegex = "(td-tag \\b(?!td-tag\\b)\\w+ area td-tag td-tag (.+?) td-tag)";
        String firmSelector = ".firm>p>span[itemprop=legalName]";
        String imgSelector = ".thumbnail>a>img";

        property.setType(extractInfo(document, typeSelector));
        property.setAddress(extractInfo(document, addressSelector));
        property.setUnparsedAddress(property.getAddress());
        property.setPrice(filterNumber(extractInfo(document, priceSelector)));
        property.setDiscription(extractInfo(document, discriptionSelector));
        property.setRoom(extractInfo(document, roomSelector));
        property.setYear(doubleToInt(filterNumber(applyTransform(extractInfo(insertMarkIntoText(document, "td"), tableSelector), yearRegex))));
        try {
            property.setArea(filterNumber(applyTransform(extractInfo(insertMarkIntoText(document, "td"), tableSelector), areaRegex)));
        }catch (Exception e){}
        property.setFirm(extractInfo(document,firmSelector));
        property.setBrokers(extractBrokerList(document));
        property.setImage(extractAttr(document, imgSelector, "src"));

        return property;
    }

    private PropertyEntity getDuproprioProperty(Document document, PropertyEntity property){
        String typeSelector = "body > div.container > div.listing-container > div > div.listing-sidebar > div.listing-information > div.listing-profile > div.listing-address > h3";
        String addressSelector = "body > div.container > div.listing-container > div > div.listing-sidebar > div.listing-information > div.listing-profile > div.listing-address > div.listing-location__group-address > div";
        property.setUnparsedAddress(property.getAddress());
        String priceSelector = "body > div.container > div.listing-container > div > div.listing-sidebar > div.listing-information > div.listing-profile > div.listing-address > div.listing-price > div.listing-price__amount";
//        String roomSelector = "body > div.container > div.listing-container > div > div.listing-content > section.listing-tab-content.listing-tab-content--description.listing-tab-content--active > article > div.listing-box.listing-main-characteristics.js-listing-main-characteristics";
        String roomSelector = ".listing-main-characteristics.js-listing-main-characteristics";
        String discriptionSelector = ".listing-owners-comments__description";
//        String tableSelector = "body > div.container > div.listing-container > div > div.listing-content > section.listing-tab-content.listing-tab-content--description.listing-tab-content--active > article > div.listing-box.listing-list-characteristics > div.listing-list-characteristics__viewport";
        String tableSelector = ".listing-list-characteristics__viewport";
        String imgSelector = ".photo-viewer__slide.slick-current>div>img";
        String yearRegex = "div-tag Year of construction div-tag div-tag div-tag div-tag (.+?) div-tag";
        String typeRegex = "\\w+(?=.*for)";
        String areaRegex = "(?<=\\D)(\\d| )+(?:\\.\\d+)?(?= *ft²+)";


        property.setType(applyTransform(extractInfo(document, typeSelector),typeRegex));
        property.setAddress(extractInfo(document, addressSelector));
        property.setPrice(filterNumber(extractInfo(document, priceSelector)));
        property.setDiscription(extractInfo(document, discriptionSelector));
        property.setRoom(extractInfo(document, roomSelector));
        property.setYear(doubleToInt(filterNumber(applyTransform(extractInfo(insertMarkIntoText(document, "div"), tableSelector), yearRegex))));
        property.setImage(extractAttr(document, imgSelector, "src"));
        try {
            property.setArea(filterNumber(applyTransform(extractInfo(document, roomSelector), areaRegex).replaceAll(" ", "")));
        }catch (Exception e){}


        return property;
    }

    private String extractInfo(Document document, String selector){
        try {
            return document.select(selector).get(0).text();
        }catch (Exception e){
            return null;
        }
    }

    private String extractAttr(Document document, String selector, String attr){
        try {
            return document.select(selector).get(0).attr(attr);
        }catch (Exception e){
            return null;
        }
    }

    private List<Broker> extractBrokerList(Document document){
        List<Broker> brokers = new ArrayList<>();
        String brokerSelector = ".divcontainingbrokers>.row";
        String brokerNameSelector = ".brokerid>p>span[itemprop=name]";
        String firmNameSelector = ".firm>p>span[itemprop=legalName]";
        try {
            Elements elements = document.select(brokerSelector);
            if (elements != null && !elements.isEmpty()) {
                for (Element element : elements) {
                    Broker broker = new Broker();
                    String name = element.select(brokerNameSelector).attr("content");
                    String firmName = element.select(firmNameSelector).text();
                    if (name != null && !name.isEmpty()) {
                        broker.setName(name);
                        if (firmName != null && !firmName.isEmpty()) {
                            broker.setFirm(firmName);
                        }
                        brokers.add(broker);
                    }
                }
            }
        }catch (Exception e){

        }

        if (!brokers.isEmpty()){
            return brokers;
        }else{
            return null;
        }
    }

    private Double filterNumber(String input){
        if (input == null || input.isEmpty()){
            return null;
        }

        String regex = "(\\d+(?:\\.\\d+)?)";
        input = input.replaceAll(" +"," ").replaceAll(",","");
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        if(matcher.find()) {
            return Double.valueOf(matcher.group(0));
        }
        return null;
    }

    private Document insertMarkIntoText(Document document, String selector) {
        Document clone = document.clone();
        clone.select(selector).prepend(selector + "-tag ");
        clone.select(selector).append(" " + selector + "-tag");
        return clone;
    }

    private String applyTransform(String value, String regex) {
        try {
            value = value.replaceAll(" +", " ");
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(value);
            if (matcher.find()) {
                return matcher.group(0);
            }
        }catch (Exception e){
        }
        return null;
    }

    private void writeJson(String jsonString, String fileName)  {
        try (PrintWriter printWriter = new PrintWriter(new FileWriter(fileName, true))){
            printWriter.println(jsonString);
        } catch (IOException e) {}
    }

    private Integer doubleToInt(Double input){
        if (input != null){
            return input.intValue();
        } else {
            return null;
        }
    }

    public void closeWebDriver(){
        webDriver.close();
    }

//    static class Property{
//        String source;
//        String url;
//        String type;
//        String address;
//        Double price;
//        String room;
//        String discription;
//        Double year;
//        Double area;
//
//        public Property(){}
//
//        public String getSource() {
//            return source;
//        }
//
//        public String getUrl() {
//            return url;
//        }
//
//        public void setUrl(String url) {
//            this.url = url;
//        }
//
//        public void setSource(String source) {
//            this.source = source;
//        }
//
//        public String getType() {
//            return type;
//        }
//
//        public void setType(String type) {
//            this.type = type;
//        }
//
//        public String getAddress() {
//            return address;
//        }
//
//        public void setAddress(String address) {
//            this.address = address;
//        }
//
//        public Double getPrice() {
//            return price;
//        }
//
//        public void setPrice(Double price) {
//            this.price = price;
//        }
//
//        public String getRoom() {
//            return room;
//        }
//
//        public void setRoom(String room) {
//            this.room = room;
//        }
//
//        public String getDiscription() {
//            return discription;
//        }
//
//        public void setDiscription(String discription) {
//            this.discription = discription;
//        }
//
//        public Double getYear() {
//            return year;
//        }
//
//        public void setYear(Double year) {
//            this.year = year;
//        }
//
//        public Double getArea() {
//            return area;
//        }
//
//        public void setArea(Double area) {
//            this.area = area;
//        }
//    }

}
