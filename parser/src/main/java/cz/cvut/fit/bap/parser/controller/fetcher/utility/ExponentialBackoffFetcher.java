package cz.cvut.fit.bap.parser.controller.fetcher.utility;

import cz.cvut.fit.bap.parser.controller.fetcher.FailedFetchException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Utility class implementing exponential backoff with @Retryable.
 */
@Component
public class ExponentialBackoffFetcher {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExponentialBackoffFetcher.class);

    /**
     * Fetches document from url with exponential backoff and jitter. IOException is transformed to unchecked FailedFetchException.
     * maxDelay in @Backoff must be set to override default of 30sec.
     * Functions with @Retryable can not be static or private methods.
     *
     * @param url which is supposed to be fetched
     * @return fetched document
     * @see <a href="https://aws.amazon.com/blogs/architecture/exponential-backoff-and-jitter/">Backoff with jitter</a>
     */
    @Retryable(retryFor = FailedFetchException.class,
            maxAttempts = 10,
            backoff = @Backoff(delay = 1000, multiplier = 2, maxDelay = 1_024_000, random = true))
    public Document getDocumentWithRetry(String url) {
        try {
            long initialTime = System.currentTimeMillis();
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36")
                    .timeout(60000)
                    .get();
            LOGGER.debug("Fetch in {} ms for {}", System.currentTimeMillis() - initialTime, url);
            return doc;
        } catch (IOException e) {
            throw new FailedFetchException(e);
        }
    }

    /**
     * Function is called if all retries in @Retryable function fail. Must be public to work properly.
     *
     * @param e   exception with which the @Retryable function failed
     * @param url parameter of retryable function
     * @return function throws FailedFetchException, which ends scrapping.
     */
    @Recover
    public Document recover(FailedFetchException e, String url) {
        LOGGER.info("Failed to fetch url after all retries: {}", url);
        throw new FailedFetchException(e, url);
    }
}
