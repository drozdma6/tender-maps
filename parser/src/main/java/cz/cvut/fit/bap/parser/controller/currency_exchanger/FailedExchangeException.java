package cz.cvut.fit.bap.parser.controller.currency_exchanger;

public class FailedExchangeException extends RuntimeException {
    public FailedExchangeException() {
    }

    public FailedExchangeException(String s) {
        super(s);
    }
}
