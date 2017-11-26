package crawler;

/**
 * Created by qinyu on 2017-11-25.
 */
public class RealtorCrawler extends WebCrawler {
    private CustomArg customArg;

    public RealtorCrawler(Object customArg) throws Exception {
        this.customArg = (CustomArg) customArg;
    }
}
