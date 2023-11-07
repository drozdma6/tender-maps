package cz.cvut.fit.bap.parser.controller.scrapper;

import org.jsoup.nodes.Document;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Abstract class for scrappers
 *
 * @param <D> scrapped data from document
 */
public abstract class AbstractScrapper<D> {
    protected Document document;

    protected AbstractScrapper(Document document){
        this.document = document;
    }

    /**
     * Gets scrapped page data
     *
     * @return page data
     */
    public abstract D getPageData();

    protected String getNullIfEmpty(String str){
        if(str == null || str.isEmpty()){
            return null;
        }
        return str;
    }

    /**
     * Removes URL parameters starting with "/p:" and continuing until the next "/" character,
     * if the /p: parameter is at the end it stays.
     *
     * @param url the URL string to modify
     * @return the modified URL string with "/p:" parameters removed or
     * returns the original URL string if no such parameters found
     */
    protected String removeUrlParameters(String url){
        Pattern pattern = Pattern.compile("/p:[^/]*+/");
        Matcher matcher = pattern.matcher(url);
        StringBuilder modifiedUrl = new StringBuilder(url);

        while(matcher.find()){
            int startIndex = matcher.start();
            int endIndex = matcher.end();

            if(endIndex < modifiedUrl.length()){
                modifiedUrl.delete(startIndex, endIndex - 1);
                matcher.reset(modifiedUrl);
            }
        }
        return modifiedUrl.toString();
    }

    /**
     * Converts yes and no string into boolean. Yes is true, no is false.
     *
     * @param yesNoVal string value to be converted
     * @return true if yes, false if no, null otherwise
     */
    protected Boolean convertYesNoToBoolean(String yesNoVal) {
        if (Objects.equals(yesNoVal, "Yes")) {
            return true;
        }
        if (Objects.equals(yesNoVal, "No")) {
            return false;
        }
        return null;
    }

    /**
     * Gets BigDecimal from string.
     *
     * @param price which is supposed to be converted
     * @return BigDecimal or null if string can not be converted
     */
    protected BigDecimal getBigDecimalFromString(String price) {
        try {
            return new BigDecimal(formatPrice(price));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Format string price into format accepted by BigDecimal
     *
     * @param strPrice price which is supposed to by formatted
     * @return formatted price
     */
    protected String formatPrice(String strPrice) {
        return strPrice.replaceAll("\\s", "").replace(',', '.');
    }
}
