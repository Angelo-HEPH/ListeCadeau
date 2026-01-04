package be.couderiannello.dao;

import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import be.couderiannello.config.AppConfig;

public abstract class RestDAO<T> implements DAO<T> {

    private static final Client client;
    static {
        ClientConfig config = new DefaultClientConfig();
        client = Client.create(config);
    }

    protected URI getBaseUri() {
        String baseUrl = AppConfig.get("api.base.url");
        return UriBuilder.fromUri(baseUrl).build();
    }

    protected WebResource getResource() {
        return client.resource(getBaseUri());
    }

    protected String readBody(ClientResponse response) {
        try {
            return response.getEntity(String.class);
        } catch (Exception e) {
            return null;
        }
    }

    protected int extractIdFromLocation(ClientResponse response, String resourceName) {
        String location = response.getHeaders().getFirst("Location");

        if (location == null || location.isBlank()) {
            throw new RuntimeException(
                "Création " + resourceName + " : header Location manquant"
            );
        }

        String[] parts = location.split("/");
        return Integer.parseInt(parts[parts.length - 1]);
    }
}