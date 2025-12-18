package be.couderiannello.dao;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;

import be.couderiannello.models.Notification;
import be.couderiannello.models.Personne;

public class NotificationDAO extends RestDAO<Notification> {

    private static NotificationDAO instance = null;

    private NotificationDAO() {
        super();
    }

    public static NotificationDAO getInstance() {
        if (instance == null) {
            instance = new NotificationDAO();
        }
        return instance;
    }

    //Create
    @Override
    public int create(Notification n) {

        JSONObject json = new JSONObject();
        json.put("message", n.getMessage());

        if (n.getPersonne() == null) {
            throw new IllegalArgumentException("personne obligatoire pour créer une notification.");
        }
        json.put("personneId", n.getPersonne().getId());

        ClientResponse response = getResource()
                .path("notification")
                .type(MediaType.APPLICATION_JSON)
                .post(ClientResponse.class, json.toString());

        if (response.getStatus() == Status.CREATED.getStatusCode()) {
            String location = response.getHeaders().getFirst("Location");
            if (location == null || location.isBlank()) {
                throw new RuntimeException("Réponse création notification sans header Location");
            }
            String[] parts = location.split("/");
            String last = parts[parts.length - 1];
            return Integer.parseInt(last);
        }

        throw new RuntimeException("Erreur API création notification : " + response.getStatus());
    }

    //Find
    @Override
    public Notification find(int id) {
        return find(id, false);
    }

    public Notification find(int id, boolean loadPersonne) {

        ClientResponse response = getResource()
                .path("notification")
                .path(String.valueOf(id))
                .queryParam("loadPersonne", String.valueOf(loadPersonne))
                .accept(MediaType.APPLICATION_JSON)
                .get(ClientResponse.class);

        if (response.getStatus() != Status.OK.getStatusCode()) {
            return null;
        }

        JSONObject json = new JSONObject(response.getEntity(String.class));
        return fromJsonNotification(json, loadPersonne);
    }

    //FindAll
    @Override
    public List<Notification> findAll() {
        return findAll(false);
    }

    public List<Notification> findAll(boolean loadPersonne) {

        ArrayList<Notification> list = new ArrayList<>();

        ClientResponse response = getResource()
                .path("notification")
                .queryParam("loadPersonne", String.valueOf(loadPersonne))
                .accept(MediaType.APPLICATION_JSON)
                .get(ClientResponse.class);

        if (response.getStatus() != Status.OK.getStatusCode()) {
            return list;
        }

        JSONArray array = new JSONArray(response.getEntity(String.class));
        for (int i = 0; i < array.length(); i++) {
            JSONObject json = array.getJSONObject(i);
            list.add(fromJsonNotification(json, loadPersonne));
        }

        return list;
    }

    //Update
    @Override
    public boolean update(Notification n) {

        JSONObject json = new JSONObject();

        json.put("read", n.isRead());

        ClientResponse response = getResource()
                .path("notification")
                .path(String.valueOf(n.getId()))
                .type(MediaType.APPLICATION_JSON)
                .put(ClientResponse.class, json.toString());

        return response.getStatus() == Status.NO_CONTENT.getStatusCode();
    }

    //Delete
    @Override
    public boolean delete(Notification n) {

        ClientResponse response = getResource()
                .path("notification")
                .path(String.valueOf(n.getId()))
                .delete(ClientResponse.class);

        return response.getStatus() == Status.NO_CONTENT.getStatusCode();
    }

    //JSON -> Model
    private Notification fromJsonNotification(JSONObject json, boolean loadPersonne) {

        Notification n = new Notification();
        n.setId(json.getInt("id"));
        n.setMessage(json.getString("message"));
        n.setSendDate(LocalDate.parse(json.getString("sendDate")));
        n.setRead(json.getBoolean("read"));

        //Relation personne
        if (loadPersonne && json.has("personne") && !json.isNull("personne")) {
            n.setPersonne(parsePersonne(json.getJSONObject("personne")));
        } else if (json.has("personneId") && !json.isNull("personneId")) {
            Personne p = new Personne();
            p.setId(json.getInt("personneId"));
            n.setPersonne(p);
        }

        return n;
    }

    private Personne parsePersonne(JSONObject json) {
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

        return p;
    }
}
