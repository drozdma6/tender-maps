package cz.cvut.fit.bap.parser.controller;

/*
    Abstract controller with some service
 */
public abstract class AbstractController<S>{
    protected final S service;

    protected AbstractController(S service){
        this.service = service;
    }
}