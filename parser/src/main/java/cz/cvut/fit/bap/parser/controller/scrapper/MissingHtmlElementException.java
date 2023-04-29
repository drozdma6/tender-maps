package cz.cvut.fit.bap.parser.controller.scrapper;

/**
 * Unchecked exception signalizing missing html element
 */
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
