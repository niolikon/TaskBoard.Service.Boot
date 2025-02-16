package com.niolikon.taskboard.application.security.keycloak;

import com.niolikon.taskboard.application.exception.rest.BadGatewayRestException;
import lombok.extern.java.Log;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Log
@Component
public class KeycloakRestClient {

    public <T> ResponseEntity<T> postToKeycloak(String requestUrl, HttpEntity<MultiValueMap<String, String>> requestEntity, Class<T> responseType) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new FormHttpMessageConverter());

        ResponseEntity<T> responseEntity = restTemplate.postForEntity(
                requestUrl,
                requestEntity,
                responseType
        );
        log.finer(String.format("Response received: %s", responseEntity));

        if (! responseEntity.getStatusCode().is2xxSuccessful()) {
            throw new BadGatewayRestException("Logout request failed");
        }

        return responseEntity;
    }
}
