package com.microcompany.oauth2apiclient.controller;

import com.microcompany.oauth2apiclient.model.Account;
import com.microcompany.oauth2apiclient.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient;

@RestController
public class AccountController {
    @Autowired
    private WebClient webClient;

    @GetMapping(value = "/accounts-view")
    public List<Account> getAccounts(
            @RegisteredOAuth2AuthorizedClient("accounts-client-authorization-code") OAuth2AuthorizedClient authorizedClient
    ) {
        return this.webClient
                .get()
                .uri("http://127.0.0.1:9900/accounts")
                .attributes(oauth2AuthorizedClient(authorizedClient))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Account>>() {
                })
                .block();
    }

    @GetMapping(value = "/accounts-addBalance")
    public Account addBalance(
            @RegisteredOAuth2AuthorizedClient("accounts-client-authorization-code") OAuth2AuthorizedClient authorizedClient
    ) {

        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("id", "1");
        uriVariables.put("ownerId", "1");
        String amount = "100";

        String url = UriComponentsBuilder.fromUriString("http://127.0.0.1:9900/accounts/{id}/owner/{ownerId}/addBalance")
                .queryParam("amount", amount)
                .buildAndExpand(uriVariables)
                .toUriString();

        return this.webClient
                .put()
                .uri(url)
                .attributes(oauth2AuthorizedClient(authorizedClient))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Account>() {
                })
                .block();
    }

    @GetMapping(value = "/accounts-withdrawBalance")
    public Account withdrawBalance(
            @RegisteredOAuth2AuthorizedClient("accounts-client-authorization-code") OAuth2AuthorizedClient authorizedClient
    ) {

        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("id", "1");
        uriVariables.put("ownerId", "1");
        String amount = "200";

        String url = UriComponentsBuilder.fromUriString("http://127.0.0.1:9900/accounts/{id}/owner/{ownerId}/withdrawBalance")
                .queryParam("amount", amount)
                .buildAndExpand(uriVariables)
                .toUriString();

        return this.webClient
                .put()
                .uri(url)
                .attributes(oauth2AuthorizedClient(authorizedClient))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Account>() {
                })
                .block();
    }
}