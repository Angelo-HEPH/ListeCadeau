package be.couderiannello.dao;

import java.util.ArrayList;

import javax.ws.rs.core.MediaType;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;

import be.couderiannello.models.Personne;

public class PersonneDAO extends DAO {

    private static PersonneDAO instance = null;

    private PersonneDAO() {
        super();
    }

    public static PersonneDAO getInstance() {
        if (instance == null) {
            instance = new PersonneDAO();
        }
        return instance;
    }

    public int create(Personne p) {

        JSONObject json = new JSONObject();
        json.put("name", p.getName());
        json.put("firstName", p.getFirstName());
        json.put("age", p.getAge());
        json.put("street", p.getStreet());
        json.put("city", p.getCity());
        json.put("streetNumber", p.getStreetNumber());
        json.put("postalCode", p.getPostalCode());
        json.put("email", p.getEmail());
        json.put("password", p.getPassword());

        ClientResponse response = getResource()
                .path("personne")
                .type(MediaType.APPLICATION_JSON)
                .post(ClientResponse.class, json.toString());

        if (response.getStatus() == Status.CREATED.getStatusCode()) {
            JSONObject body = new JSONObject(response.getEntity(String.class));
            return body.getInt("id");
        }

        throw new RuntimeException("Erreur API création personne : " + response.getStatus());
    }

    public Personne find(int id) {

        ClientResponse response = getResource()
                .path("personne")
                .path(String.valueOf(id))
                .accept(MediaType.APPLICATION_JSON)
                .get(ClientResponse.class);

        if (response.getStatus() != Status.OK.getStatusCode()) {
            return null;
        }

        JSONObject json = new JSONObject(response.getEntity(String.class));

        Personne p = new Personne();
        p.setId(json.getInt("id"));
        p.setName(json.getString("name"));
        p.setFirstName(json.getString("firstName"));
        p.setAge(json.getInt("age"));
        p.setStreet(json.getString("street"));
        p.setCity(json.getString("city"));
        p.setStreetNumber(json.getString("streetNumber"));
        p.setPostalCode(json.getInt("postalCode"));
        p.setEmail(json.getString("email"));
        p.setPassword(json.getString("password"));

        return p;
    }

    public ArrayList<Personne> findAll() {

        ArrayList<Personne> list = new ArrayList<>();

        ClientResponse response = getResource()
                .path("personne")
                .accept(MediaType.APPLICATION_JSON)
                .get(ClientResponse.class);

        if (response.getStatus() != Status.OK.getStatusCode()) {
            return list;
        }

        JSONArray array = new JSONArray(response.getEntity(String.class));

        for (int i = 0; i < array.length(); i++) {
            JSONObject json = array.getJSONObject(i);

            Personne p = new Personne();
            p.setId(json.getInt("id"));
            p.setName(json.getString("name"));
            p.setFirstName(json.getString("firstName"));
            p.setAge(json.getInt("age"));
            p.setStreet(json.getString("street"));
            p.setCity(json.getString("city"));
            p.setStreetNumber(json.getString("streetNumber"));
            p.setPostalCode(json.getInt("postalCode"));
            p.setEmail(json.getString("email"));
            p.setPassword(json.getString("password"));


            list.add(p);
        }

        return list;
    }

    public boolean update(Personne p) {

        JSONObject json = new JSONObject();
        json.put("name", p.getName());
        json.put("firstName", p.getFirstName());
        json.put("age", p.getAge());
        json.put("street", p.getStreet());
        json.put("city", p.getCity());
        json.put("streetNumber", p.getStreetNumber());
        json.put("postalCode", p.getPostalCode());
        json.put("email", p.getEmail());
        json.put("password", p.getPassword());

        ClientResponse response = getResource()
                .path("personne")
                .path(String.valueOf(p.getId()))
                .type(MediaType.APPLICATION_JSON)
                .put(ClientResponse.class, json.toString());

        return response.getStatus() == Status.OK.getStatusCode();
    }

    public boolean delete(Personne p) {

        ClientResponse response = getResource()
                .path("personne")
                .path(String.valueOf(p.getId()))
                .delete(ClientResponse.class);

        return response.getStatus() == Status.OK.getStatusCode();
    }
    
    public Personne authenticate(String email, String password) {

        JSONObject json = new JSONObject();
        json.put("email", email);
        json.put("password", password);

        ClientResponse response = getResource()
                .path("personne")
                .path("login")
                .type(MediaType.APPLICATION_JSON)
                .post(ClientResponse.class, json.toString());

        if (response.getStatus() != Status.OK.getStatusCode()) {
            return null;
        }

        JSONObject body = new JSONObject(response.getEntity(String.class));

        Personne p = new Personne();
        p.setId(body.getInt("id"));
        p.setName(body.getString("name"));
        p.setFirstName(body.getString("firstName"));
        p.setEmail(body.getString("email"));

        return p;
    }

}
