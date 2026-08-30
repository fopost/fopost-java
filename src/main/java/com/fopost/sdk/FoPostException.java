package com.fopost.sdk;

/**
 * Base class for every error the SDK raises.
 *
 * <p>The API answers failures with {@code {"error": "<code>", "message": "<explanation>"}},
 * which maps onto {@link #code()} and {@link #getMessage()}.
 */
public class FoPostException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final int status;
    private final String code;
    private final transient Object body;

    public FoPostException(String message, int status, String code, Object body) {
        super(message);
        this.status = status;
        this.code = code;
        this.body = body;
    }

    public FoPostException(String message, Throwable cause) {
        super(message, cause);
        this.status = 0;
        this.code = null;
        this.body = null;
    }

    /** HTTP status of the failed response, or {@code 0} for a transport or decoding failure. */
    public int status() {
        return status;
    }

    /** Machine-readable error code from the API, when it sent one. */
    public String code() {
        return code;
    }

    /** Decoded response body, for the context fields an individual error adds. */
    public Object body() {
        return body;
    }

    @Override
    public String toString() {
        String suffix = code == null ? "" : " (" + code + ")";
        return "[" + status + suffix + "] " + getMessage();
    }
}
