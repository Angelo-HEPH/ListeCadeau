package be.couderiannello.dao;

import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;


public class DAO {

    private WebResource resource;
    private static URI getBaseURI() {
        return UriBuilder.fromUri("http://localhost:8080/ListeCadeauAPI/api/") //Si possible mettre dans web.xml
                .build();
    }
    
    public DAO() {
        ClientConfig config = new DefaultClientConfig();
        Client client = Client.create(config);
        resource  = client.resource(getBaseURI());
    }
    
    public WebResource getResource() {
        return resource;
    }
    
}