package cz.cvut.fit.bap.parser.controller.scrapper;

import org.jsoup.nodes.Document;


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
}
