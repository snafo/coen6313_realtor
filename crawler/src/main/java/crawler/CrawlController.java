package crawler; /**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

//import com.sleepycat.je.Environment;
//import com.sleepycat.je.EnvironmentConfig;
//import edu.uci.ics.crawler4j.crawler.crawler.Configurable;
//import edu.uci.ics.crawler4j.crawler.crawler.CrawlConfig;
//import edu.uci.ics.crawler4j.crawler.crawler.WebCrawler;
//import edu.uci.ics.crawler4j.fetcher.PageFetcher;
//import edu.uci.ics.crawler4j.crawler.frontier.DocIDServer;
//import edu.uci.ics.crawler4j.crawler.frontier.Frontier;
//import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
//import edu.uci.ics.crawler4j.crawler.url.TLDList;
//import edu.uci.ics.crawler4j.crawler.url.URLCanonicalizer;
//import edu.uci.ics.crawler4j.crawler.url.WebURL;
//import edu.uci.ics.crawler4j.util.IO;
import crawler.crawler.robotstxt.RobotstxtServer;
import crawler.fetcher.PageFetcher;
import crawler.frontier.Frontier;
import crawler.url.TLDList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import crawler.url.URLCanonicalizer;
import crawler.url.WebURL;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * The controller that manages a crawling session. This class creates the
 * crawler threads and monitors their progress.
 *
 * @author Yasser Ganjisaffar
 */
public class CrawlController extends Configurable {

    static final Logger logger = LoggerFactory.getLogger(CrawlController.class);

    /**
     * The 'customData' object can be used for passing custom crawl-related
     * configurations to different components of the crawler.
     */
    protected Object customData;

    /**
     * Once the crawling session finishes the controller collects the local data
     * of the crawler threads and stores them in this List.
     */
    protected List<Object> crawlersLocalData = new ArrayList<>();


    /**
     * Is the crawling of this session finished?
     */
    protected boolean finished;

    /**
     * Is the crawling session set to 'shutdown'. Crawler threads monitor this
     * flag and when it is set they will no longer process new pages.
     */
    protected boolean shuttingDown;

//    protected PageFetcher pageFetcher;
//    protected RobotstxtServer robotstxtServer;
    protected Frontier frontier;

    protected String seedCanonicalUrl;

//    private RedisConfig redisConfig;

//    private RedisService redisService;

    private Object customArg;

    /**
     * @param config
     * @param customArg
     * 					custom argument for the constructor of your custom crawler
     * @throws Exception
     */
    public CrawlController(CrawlConfig config, Object customArg)
            throws Exception {
        super(config);

        config.validate();

//        TLDList.setUseOnline(config.isOnlineTldListUpdate());

        frontier = new Frontier(config);

//        this.pageFetcher = pageFetcher;
//        this.robotstxtServer = robotstxtServer;

        finished = false;
        shuttingDown = false;
        this.customArg = customArg;
    }

    public interface WebCrawlerFactory<T extends WebCrawler> {
        T newInstance() throws Exception;
    }

    /**
     * Start the crawling session and wait for it to finish.
     * This method utilizes default crawler factory that creates new crawler using Java reflection
     *
     * @param _c
     *            the class that implements the logic for crawler threads
     * @param numberOfCrawlers
     *            the number of concurrent threads that will be contributing in
     *            this crawling session.
     * @param <T> Your class extending WebCrawler
     */
    public <T extends WebCrawler> void start(final Class<T> _c, final int numberOfCrawlers) {
        this.start(new DefaultWebCrawlerFactory<>(_c), numberOfCrawlers, true);
    }

    /**
     * Start the crawling session and wait for it to finish.
     *
     * @param crawlerFactory
     *            factory to create crawlers on demand for each thread
     * @param numberOfCrawlers
     *            the number of concurrent threads that will be contributing in
     *            this crawling session.
     * @param <T> Your class extending WebCrawler
     */
    public <T extends WebCrawler> void start(final WebCrawlerFactory<T> crawlerFactory, final int numberOfCrawlers) {
        this.start(crawlerFactory, numberOfCrawlers, true);
    }

    /**
     * Start the crawling session and return immediately.
     *
     * @param crawlerFactory
     *            factory to create crawlers on demand for each thread
     * @param numberOfCrawlers
     *            the number of concurrent threads that will be contributing in
     *            this crawling session.
     * @param <T> Your class extending WebCrawler
     */
    public <T extends WebCrawler> void startNonBlocking(WebCrawlerFactory<T> crawlerFactory, final int numberOfCrawlers) {
        this.start(crawlerFactory, numberOfCrawlers, false);
    }

    /**
     * Start the crawling session and return immediately.
     * This method utilizes default crawler factory that creates new crawler using Java reflection
     *
     * @param _c
     *            the class that implements the logic for crawler threads
     * @param numberOfCrawlers
     *            the number of concurrent threads that will be contributing in
     *            this crawling session.
     * @param <T> Your class extending WebCrawler
     */
    public <T extends WebCrawler> void startNonBlocking(final Class<T> _c, final int numberOfCrawlers) {
        this.start(new DefaultWebCrawlerFactory<>(_c), numberOfCrawlers, false);
    }

    /**
     * Wait until this crawling session finishes.
     */
    public void waitUntilFinish() {
        while (!finished) {
            sleep(10);
        }
    }

    /**
     * Once the crawling session finishes the controller collects the local data of the crawler threads and stores them
     * in a List.
     * This function returns the reference to this list.
     *
     * @return List of Objects which are your local data
     */
    public List<Object> getCrawlersLocalData() {
        return crawlersLocalData;
    }

    /**
     * Adds a new seed URL. A seed URL is a URL that is fetched by the crawler
     * to extract new URLs in it and follow them for crawling.
     *
     * @param pageUrl
     *            the URL of the seed
     */
    public void addSeed(String pageUrl) {
        addSeedNow(pageUrl);
    }

    /**
     * Adds a new seed URL. A seed URL is a URL that is fetched by the crawler
     * to extract new URLs in it and follow them for crawling. You can also
     * specify a specific document id to be assigned to this seed URL. This
     * document id needs to be unique. Also, note that if you add three seeds
     * with document ids 1,2, and 7. Then the next URL that is found during the
     * crawl will get a doc id of 8. Also you need to ensure to add seeds in
     * increasing order of document ids.
     *
     * Specifying doc ids is mainly useful when you have had a previous crawl
     * and have stored the results and want to start a new crawl with seeds
     * which get the same document ids as the previous crawl.
     *
     * @param pageUrl
     *            the URL of the seed
     */
    public void addSeedNow(String pageUrl) {
        seedCanonicalUrl = URLCanonicalizer.getCanonicalURL(pageUrl);
        if (seedCanonicalUrl == null) {
            logger.error("Invalid seed URL: {}", pageUrl);
        } else {
            WebURL webUrl = new WebURL();
            webUrl.setURL(seedCanonicalUrl);
            webUrl.setDepth((short) 0);
            frontier.schedule(webUrl);

        }
    }


    public Frontier getFrontier() {
        return frontier;
    }

    public void setFrontier(Frontier frontier) {
        this.frontier = frontier;
    }

    public Object getCustomData() {
        return customData;
    }

    public void setCustomData(Object customData) {
        this.customData = customData;
    }

    public boolean isFinished() {
        return this.finished;
    }

    public boolean isShuttingDown() {
        return shuttingDown;
    }

    public String getSeedCanonicalUrl() {
        return seedCanonicalUrl;
    }

    public void setSeedCanonicalUrl(String seedCanonicalUrl) {
        this.seedCanonicalUrl = seedCanonicalUrl;
    }

    public Object getCustomArg() {
        return customArg;
    }

    public void setCustomArg(Object customArg) {
        this.customArg = customArg;
    }

    /**
     * Set the current crawling session set to 'shutdown'. Crawler threads
     * monitor the shutdown flag and when it is set to true, they will no longer
     * process new pages.
     */
    public void shutdown() {
        this.shuttingDown = true;
        frontier.finish();
    }

    protected static void sleep(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException ignored) {
            // Do nothing
        }
    }

    protected <T extends WebCrawler> void start(final WebCrawlerFactory<T> crawlerFactory, final int numberOfCrawlers, boolean isBlocking) {
        try {
            finished = false;
            final List<Thread> threads = new ArrayList<>();
            final List<T> crawlers = new ArrayList<>();

            new Thread(new Extractor(frontier)).start();

            for (int i = 1; i <= numberOfCrawlers; i++) {
                T crawler = crawlerFactory.newInstance();
                Thread thread = new Thread(crawler, "Crawler " + i);
                crawler.setThread(thread);
                crawler.init(i, this);
                thread.start();
                crawlers.add(crawler);
                threads.add(thread);
                logger.info("Crawler {} started", i);
            }

            final CrawlController controller = this;

            Thread monitorThread = new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        while (true) {
                            sleep(10);

                            if(config.getMaxPagesToFetch() > 0) {
                                if(!shuttingDown && frontier.getProcessedURLs() >= config.getMaxPagesToFetch()) {
                                    logger.info("Reached maximum pages {}, terminating crawling ...", frontier.getProcessedURLs());
                                    shutdown();
                                }
                            }

                            if(!shuttingDown) {
                                logger.info("Current scrapped urls: {}, filtered urls: {}, scheduled urls: " + frontier.getQueueLength(),
                                        frontier.getProcessedURLs(), frontier.getSkippedURLs());
                            }

                            boolean someoneIsWorking = false;
                            for (int i = 0; i < threads.size(); i++) {
                                Thread thread = threads.get(i);
                                if (!thread.isAlive()) {
                                    if (!shuttingDown) {
                                        logger.info("Thread {} was dead, I'll recreate it", i);
                                        T crawler = crawlerFactory.newInstance();
                                        thread = new Thread(crawler, "Crawler " + (i + 1));
                                        threads.remove(i);
                                        threads.add(i, thread);
                                        crawler.setThread(thread);
                                        crawler.init(i + 1, controller);
                                        thread.start();
                                        crawlers.remove(i);
                                        crawlers.add(i, crawler);
                                    }
                                } else if (crawlers.get(i).isNotWaitingForNewURLs()) {
                                    someoneIsWorking = true;
                                }
                            }
                            boolean shut_on_empty = config.isShutdownOnEmptyQueue();
                            if (!someoneIsWorking && shut_on_empty) {
                                // Make sure again that none of the threads are alive.
                                logger.info("It looks like no thread is working, waiting for 5 seconds to make sure...");
                                sleep(5);

                                someoneIsWorking = false;
                                for (int i = 0; i < threads.size(); i++) {
                                    Thread thread = threads.get(i);
                                    if (thread.isAlive() && crawlers.get(i).isNotWaitingForNewURLs()) {
                                        someoneIsWorking = true;
                                    }
                                }
                                if (!someoneIsWorking) {
                                    if (!shuttingDown) {
                                        long queueLength = frontier.getQueueLength();
                                        if (queueLength > 0) {
                                            continue;
                                        }
                                        logger.info(
                                                "No thread is working and no more URLs are in queue waiting for another 5 seconds to make " +
                                                        "sure...");
                                        sleep(5);
                                        queueLength = frontier.getQueueLength();
                                        if (queueLength > 0) {
                                            continue;
                                        }
                                    }

                                    logger.info("All of the crawlers are stopped. Finishing the process...");
                                    // At this step, frontier notifies the threads that were waiting for new URLs and they should stop
                                    frontier.finish();
                                    for (T crawler : crawlers) {
                                        crawler.onBeforeExit();
                                        crawlersLocalData.add(crawler.getMyLocalData());
                                    }

                                    logger.info("Waiting for 5 seconds before final clean up...");
                                    sleep(5);

                                    frontier.shutDown();
                                    finished = true;

                                    return;
                                }
                            }
                        }
                    } catch (Exception e) {
                        logger.error("Unexpected Error", e);
                    }
                }
            });

            monitorThread.start();

            if (isBlocking) {
                waitUntilFinish();
            }

        } catch (Exception e) {
            logger.error("Error happened", e);
        }
    }

    private class DefaultWebCrawlerFactory<T extends WebCrawler> implements WebCrawlerFactory<T> {
        final Class<T> _c;

        DefaultWebCrawlerFactory(Class<T> _c) {
            this._c = _c;
        }

        @Override
        public T newInstance() throws Exception {
            return _c.getConstructor(Object.class).newInstance(customArg);
        }
    }

}
