package crawler.frontier;


import crawler.Configurable;
import crawler.CrawlConfig;
import crawler.url.WebURL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class Frontier extends Configurable {
    protected static final Logger logger = LoggerFactory.getLogger(Frontier.class);

    protected List<WebURL> workQueues;

    private Set<String> urlsScheduledSet;

    protected final Object waitingList = new Object();

    protected boolean isFinished = false;

    protected long skippedURLs = 0;

    protected long processedURLs = 0;

    private Object lock1 = new Object();
    private Object lock2 = new Object();

    public Frontier(CrawlConfig config) {
        super(config);

        workQueues = new ArrayList<WebURL>();
        urlsScheduledSet = new HashSet<String>();
    }

    public boolean schedule(WebURL webURL) {
        int maxPagesToFetch = config.getMaxPagesToFetch();
        boolean done = false;
        synchronized (workQueues) {
            if (maxPagesToFetch < 0 || processedURLs < maxPagesToFetch) {
                if(addURLToCache(webURL.getURL())) {
                    workQueues.add(webURL);
                    done = true;
                }
            }
        }

        synchronized (waitingList) {
            waitingList.notifyAll();
        }

        return done;
    }

    public boolean scheduleAllowDuplicate(WebURL webURL) {
        int maxPagesToFetch = config.getMaxPagesToFetch();
        boolean done = false;
        synchronized (workQueues) {
            if (maxPagesToFetch < 0 || processedURLs < maxPagesToFetch) {
                workQueues.add(webURL);
                done = true;
            }
        }

        synchronized (waitingList) {
            waitingList.notifyAll();
        }

        return done;
    }

    public void getNextURLs(int max, List<WebURL> result) {
        while (true) {
            synchronized (workQueues) {
                if (isFinished) {
                    return;
                }

                int count = 0;
                int initialSize = workQueues.size();
                // select urls from queue randomly
                for (int i = 0; i < max; i++) {
                    if ( (count < max) && !workQueues.isEmpty() ) {
                        WebURL webURL = workQueues.remove(new Random().nextInt(workQueues.size()));
                        result.add(webURL);
                        count++;
                    } else {
                        break;
                    }
                }
                if(count > 0) {
                    logger.debug("Before {} - deleted {} = Now {}", initialSize, count, workQueues.size());
                }

                if (result.size() > 0) {
                    return;
                }
            }

            try {
                synchronized (waitingList) {
                    waitingList.wait();
                }
            } catch (InterruptedException ignored) {
                // Do nothing
            }
            if (isFinished) {
                return;
            }
        }
    }

    public long getQueueLength() {
        return workQueues.size();
    }

    public boolean isScheduled(String url) {
        synchronized (urlsScheduledSet) {
            try {
                return urlsScheduledSet.contains(url);
            } catch (Exception e) {
                logger.error("Exception thrown while getting url", e);
                return false;
            }
        }
    }

    private boolean addURLToCache(String url) {
        synchronized (urlsScheduledSet) {
            try {
                return urlsScheduledSet.add(url);
            } catch (Exception e) {
                logger.error("Exception thrown while adding url", e);
                return false;
            }
        }
    }

    public long getTotalCrawledURLs() {
        return urlsScheduledSet.size();
    }

    public void increaseSkippedURLs() {
        synchronized(lock1) {
            skippedURLs++;
        }
    }

    public long getSkippedURLs() {
        return skippedURLs;
    }

    public void increaseProcessedURLs() {
        synchronized(lock2) {
            processedURLs++;
        }
    }

    public long getProcessedURLs() {
        return processedURLs;
    }

    public void shutDown() {
        if(urlsScheduledSet != null) {
            urlsScheduledSet.clear();
        }

        if(workQueues != null) {
            workQueues.clear();
        }
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void finish() {
        isFinished = true;
        synchronized (waitingList) {
            waitingList.notifyAll();
        }
    }

}