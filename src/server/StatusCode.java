package server;

public enum StatusCode {
    OK(200),
    NO_BODY(400),
    NO_FOUND(404),
    UNKNOWN(405);

    private int code;

    StatusCode (int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
