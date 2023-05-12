package cz.cvut.fit.bap.parser.controller.scrapper;

import org.jsoup.nodes.Document;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public abstract class AbstractScrapper{
    protected Document document;

    public AbstractScrapper(Document document){
        this.document = document;
    }

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
        Pattern pattern = Pattern.compile("/p:.*?/");
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
}
