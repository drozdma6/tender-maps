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
     * Removes URL parameters starting with "/p:" and continuing until the next "/" character or end of string
     *
     * @param url the URL string to modify
     * @return the modified URL string with "/p:" parameters removed or
     * returns the original URL string if no parameters found
     */
    protected String removeUrlParameters(String url){
        String patternString = "/p:[^/]*";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(url);
        if(matcher.find()){
            int startIndex = matcher.start();
            int endIndex = matcher.end();
            return url.substring(0, startIndex) + url.substring(endIndex);
        }
        return url;
    }
}