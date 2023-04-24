package cz.cvut.fit.bap.parser.controller;

/*
    Abstract controller with some service
 */
public class AbstractController<S>{
    protected final S service;

    public AbstractController(S service){
        this.service = service;
    }
}