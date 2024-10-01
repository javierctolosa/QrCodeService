package qrcodeapi;

import com.fasterxml.jackson.annotation.JsonProperty;

public class QrErrorHandler {
    @JsonProperty("error")
    private String errorMessage;

    public QrErrorHandler(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
