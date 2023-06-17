package cz.cvut.fit.bap.parser.controller.geocoder;

/**
 * RunTime exception signalizing error occurred while geocoding.
 */
public class GeocodingException extends RuntimeException{
    public GeocodingException(String s){
        super(s);
    }

    public GeocodingException(Exception e){
        super(e);
    }
}