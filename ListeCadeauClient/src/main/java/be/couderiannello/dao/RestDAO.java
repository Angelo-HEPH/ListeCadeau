package be.couderiannello.dao;

import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

public abstract class RestDAO<T> implements DAO<T> {
    private static URI getBaseURI() {
        return UriBuilder.fromUri("http://localhost:8080/ListeCadeauAPI/api/") //Si possible mettre dans web.xml
                .build();
    }

    public WebResource getResource() {
        ClientConfig config = new DefaultClientConfig();
        Client client = Client.create(config);
        return client.resource(getBaseURI());
    }
    
}
