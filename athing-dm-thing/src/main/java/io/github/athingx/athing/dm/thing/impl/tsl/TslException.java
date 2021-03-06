package io.github.athingx.athing.dm.thing.impl.tsl;

/**
 * TSL异常
 */
public class TslException extends RuntimeException {

    public TslException(String message) {
        super(message);
    }

    public TslException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String getLocalizedMessage() {
        return String.format("%s because %s", getMessage(), getCause().getLocalizedMessage());
    }
}
