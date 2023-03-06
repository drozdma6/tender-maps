package cz.cvut.fit.bap.parser.scrapper;

public class MissingHtmlElementException extends RuntimeException{
    public MissingHtmlElementException(){
    }

    public MissingHtmlElementException(String s){
        super(s);
    }

    public MissingHtmlElementException(RuntimeException e){
        super(e.getMessage());
    }
}
