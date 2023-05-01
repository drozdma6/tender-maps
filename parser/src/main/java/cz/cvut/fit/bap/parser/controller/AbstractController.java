package cz.cvut.fit.bap.parser.controller;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
    Abstract controller with some service
 */
public abstract class AbstractController<S>{
    protected final S service;

    public AbstractController(S service){
        this.service = service;
    }

    /**
     * Removes URL parameters starting with "/p:" and continuing until the next "/" character,
     * if the /p: parameter is at the end it stays.
     *
     * @param url the URL string to modify
     * @return the modified URL string with "/p:" parameters removed or
     * returns the original URL string if no parameters found
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