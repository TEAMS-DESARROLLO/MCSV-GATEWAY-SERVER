package mo.gobiernolocal.gateway.setups;

import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientException;

public class CustomClientException extends WebClientException{
    private final HttpStatus status;
    private final ErrorDetails details;

    CustomClientException(HttpStatus status, ErrorDetails details) {
        super(status.getReasonPhrase());
        this.status = status;
        this.details = details;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public ErrorDetails getDetails() {
        return details;
    }
    
}
