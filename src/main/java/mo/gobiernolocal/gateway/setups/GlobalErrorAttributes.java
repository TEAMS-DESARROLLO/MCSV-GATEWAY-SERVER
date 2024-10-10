package mo.gobiernolocal.gateway.setups;

import java.util.Map;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;

@Component
public class GlobalErrorAttributes extends DefaultErrorAttributes{

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options ) {

        Map<String, Object> errorResponse = super.getErrorAttributes(request, options);
        String msg = (String) errorResponse.get("message");
        if(msg==null)
            msg="";
            
        request.attribute("message");
        
        //extract the status and put custom error message on the map
        HttpStatus status = HttpStatus.valueOf((Integer) errorResponse.get("status"));
    
        switch (status) {
            case BAD_REQUEST:
                errorResponse.put("message", "Parametros incorrectos :: " + msg);
                break;
            case UNAUTHORIZED:
                errorResponse.put("message", "Error en credenciales o token expirado:: " + msg);
                break;
            case FORBIDDEN:
                errorResponse.put("message", "Acceso denegado al recurso :: " + msg);
                break;
            case NOT_FOUND:
                errorResponse.put("message", "No existe el recurso :: " + msg);
                break;    
            // case :
            //     errorResponse.put("message", "No existe el recurso :: " + msg);
            //     break;                         
            default:
                //errorResponse.put("message", "Something went wrong!");
                errorResponse.put("message", request.attribute(msg));
        }

        return errorResponse;
    }

    @Override
    public void storeErrorInformation(Throwable error, ServerWebExchange exchange) {
        exchange.getAttributes().put("message", error.getMessage());
        super.storeErrorInformation(error, exchange);
    }

    
}
