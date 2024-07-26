package mo.gobiernolocal.gateway.setups;


import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.JsonNode;


import lombok.extern.slf4j.Slf4j;



@Slf4j
@Component
public class AuthenticationFiltering extends AbstractGatewayFilterFactory < AuthenticationFiltering.Config> {

    private final WebClient.Builder webClientBuilder;


    public AuthenticationFiltering(WebClient.Builder webClientBuilder){
        super(Config.class);
        this.webClientBuilder = webClientBuilder;
    }
    
    public static class Config {
    }


    @Override
    public GatewayFilter apply(Config config) {


            return new OrderedGatewayFilter((exchange,chain) -> {
                if ( !exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION))  {
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No existe autorizacio en la cabecera");
                    
                }
                @SuppressWarnings("null")
                String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
                String[] parts = authHeader.split(" ");
                if(parts.length != 2 || !"Bearer".equals(parts[0]) ){
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Bad authorization struct");
                }    
                String url = "http://mcsv-auth-server/auth/isValidToken";        
                //HttpHeaders headers = new HttpHeaders();    
                //headers.add("Authorization", authHeader);
                
                return webClientBuilder.baseUrl(url).build()
                    .get()
                    .uri(url) .header(HttpHeaders.AUTHORIZATION, authHeader)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .doOnNext( o -> {
                        System.out.println(o);
                    })
                    .map(response -> {
                        log.info("see objects " + response);
                        return exchange;
                    })
                    .onErrorMap( WebClientResponseException.class,error -> {
                        HttpStatus status = (HttpStatus) error.getStatusCode();
                        String msg = error.getHeaders().get("message_custom") == null? error.getMessage(): error.getHeaders().get("message_custom").get(0).toString();

                        throw new ResponseStatusException(status, msg, error.getCause());
                    })
                    .flatMap(chain::filter);

            }, 1);

            // return (exchange,chain) -> {
            
            //    if ( !exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION))  {
            //         throw new RuntimeException("No existe informacion de AUTORIZACION-");
            //     }

    
            //     @SuppressWarnings("null")
            //     String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
    
            //     String[] parts = authHeader.split(" ");
            //     if(parts.length != 2 || !"Bearer".equals(parts[0]) ){
            //         throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No existe autorizacio en la cabecera");
                    
            //     }
            //     //String url = "http://mcsv-auth-server/auth/isValidToken/token=" + parts[1];
            //     String url = "http://mcsv-auth-server/auth/isValidToken";

            //     //HttpHeaders headers = new HttpHeaders();
            //     //headers.add("Content-Type", "application/json");
            //     //headers.add("Authorization", authHeader);                
    
               
            //     Mono<Void> rtn = webClientBuilder.baseUrl(url).build()
            //         .get()
            //         .uri(url ).header(HttpHeaders.AUTHORIZATION, parts[1])
            //         //.headers(h -> h.addAll(headers))
            //         .retrieve()
            //         .bodyToMono( JsonNode.class)
            //         .map(dto ->  {
                        
            //             exchange.getRequest()
            //             .mutate()
            //             .header("x-auth-user-id", "retorno");
                        
            //             return exchange;
                        
            //         })

            //         .flatMap(chain::filter) ;

            //      return rtn;
     
            };


            

    
    
}
