package crawler;


import crawler.crawler.robotstxt.RobotstxtConfig;
import crawler.crawler.robotstxt.RobotstxtServer;
import crawler.fetcher.PageFetcher;

/**
 * Created by qinyu on 2017-11-25.
 */
public class StartUp {
    public static void main(String[] args){
        startCrawling();
    }

    private static void startCrawling(){
        CrawlConfig config = new CrawlConfig();
        config.setMaxDepthOfCrawling(-1);
        config.setMaxPagesToFetch(-1);
        config.setPolitenessDelay(1000);
        CustomArg customArg = new CustomArg();
//        Processor processor = new Processor();
//        customArg.setProcessor(processor);

//        PageFetcher pageFetcher = new PageFetcher(config);
//        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
//        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig,pageFetcher);
        try {
            CrawlController controller = new CrawlController(config,customArg);
            controller.start(RealtorCrawler.class, 2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
