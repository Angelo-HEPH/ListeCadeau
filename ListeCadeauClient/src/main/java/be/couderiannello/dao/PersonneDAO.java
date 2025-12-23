package be.couderiannello.dao;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;

import be.couderiannello.models.Cadeau;
import be.couderiannello.models.ListeCadeau;
import be.couderiannello.models.Notification;
import be.couderiannello.models.Personne;
import be.couderiannello.models.Reservation;

public class PersonneDAO extends RestDAO<Personne> {

    private static PersonneDAO instance = null;

    private PersonneDAO() { }

    public static PersonneDAO getInstance() {
        if (instance == null) {
            instance = new PersonneDAO();
        }
        return instance;
    }

    //Create
    @Override
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

        if (response.getStatus() != Status.CREATED.getStatusCode()) {
            throw new RuntimeException(
                    "Erreur API création personne (status=" 
                    		+ response.getStatus() + ", body=" 
                    		+ readBody(response) + ")"
            );
        }

        return extractIdFromLocation(response, "personne");
    }

    //Find
    @Override
    public Personne find(int id) {
        return find(id, false, false, false, false);
    }

    public Personne find(int id,
                         boolean loadNotifications,
                         boolean loadCreatedLists,
                         boolean loadInvitedLists,
                         boolean loadReservations) {

        ClientResponse response = getResource()
                .path("personne")
                .path(String.valueOf(id))
                .queryParam("loadNotifications", String.valueOf(loadNotifications))
                .queryParam("loadCreatedLists", String.valueOf(loadCreatedLists))
                .queryParam("loadInvitedLists", String.valueOf(loadInvitedLists))
                .queryParam("loadReservations", String.valueOf(loadReservations))
                .accept(MediaType.APPLICATION_JSON)
                .get(ClientResponse.class);

        if (response.getStatus() == Status.NOT_FOUND.getStatusCode()) {
            return null;
        }

        if (response.getStatus() != Status.OK.getStatusCode()) {
            throw new RuntimeException(
                    "Erreur API find personne (status=" 
                    		+ response.getStatus() + ", body=" 
                    		+ readBody(response) + ")"
            );
        }

        JSONObject json = new JSONObject(readBody(response));
        Personne p = fromJsonPersonne(json);

        if (loadNotifications && json.has("notifications")) {
            p.setNotifications(parseNotifications(json.getJSONArray("notifications")));
        }

        if (loadCreatedLists && json.has("createdLists")) {
            p.setListeCadeauCreator(parseListes(json.getJSONArray("createdLists")));
        }

        if (loadInvitedLists && json.has("invitedLists")) {
            p.setListeCadeauInvitations(parseListes(json.getJSONArray("invitedLists")));
        }

        if (loadReservations && json.has("reservations")) {
            p.setReservations(parseReservations(json.getJSONArray("reservations")));
        }

        return p;
    }

    public Personne findByEmail(String email) {

        ClientResponse response = getResource()
                .path("personne")
                .path("byEmail")
                .queryParam("email", email)
                .accept(MediaType.APPLICATION_JSON)
                .get(ClientResponse.class);

        if (response.getStatus() == Status.NOT_FOUND.getStatusCode()) {
            return null;
        }

        if (response.getStatus() != Status.OK.getStatusCode()) {
            throw new RuntimeException(
                    "Erreur API findByEmail (status=" + response.getStatus() +
                    ", body=" + readBody(response) + ")"
            );
        }

        JSONObject json = new JSONObject(readBody(response));
        return fromJsonPersonne(json);
    }

    //FindAll
    @Override
    public List<Personne> findAll() {
        return findAll(false, false, false, false);
    }

    public List<Personne> findAll(boolean loadNotifications,
                                  boolean loadCreatedLists,
                                  boolean loadInvitedLists,
                                  boolean loadReservations) {

        ClientResponse response = getResource()
                .path("personne")
                .queryParam("loadNotifications", String.valueOf(loadNotifications))
                .queryParam("loadCreatedLists", String.valueOf(loadCreatedLists))
                .queryParam("loadInvitedLists", String.valueOf(loadInvitedLists))
                .queryParam("loadReservations", String.valueOf(loadReservations))
                .accept(MediaType.APPLICATION_JSON)
                .get(ClientResponse.class);

        if (response.getStatus() != Status.OK.getStatusCode()) {
            throw new RuntimeException(
                    "Erreur API findAll personne (status=" 
                    		+ response.getStatus() + ", body=" 
                    		+ readBody(response) + ")"
            );
        }

        JSONArray array = new JSONArray(readBody(response));

        ArrayList<Personne> list = new ArrayList<>();

        for (int i = 0; i < array.length(); i++) {
            JSONObject json = array.getJSONObject(i);

            Personne p = fromJsonPersonne(json);

            if (loadNotifications && json.has("notifications")) {
                p.setNotifications(parseNotifications(json.getJSONArray("notifications")));
            }

            if (loadCreatedLists && json.has("createdLists")) {
                p.setListeCadeauCreator(parseListes(json.getJSONArray("createdLists")));
            }

            if (loadInvitedLists && json.has("invitedLists")) {
                p.setListeCadeauInvitations(parseListes(json.getJSONArray("invitedLists")));
            }

            if (loadReservations && json.has("reservations")) {
                p.setReservations(parseReservations(json.getJSONArray("reservations")));
            }

            list.add(p);
        }

        return list;
    }

    //Update
    @Override
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

        if (p.getPassword() != null && !p.getPassword().isBlank()) {
            json.put("password", p.getPassword());
        }

        ClientResponse response = getResource()
                .path("personne")
                .path(String.valueOf(p.getId()))
                .type(MediaType.APPLICATION_JSON)
                .put(ClientResponse.class, json.toString());

        if (response.getStatus() != Status.NO_CONTENT.getStatusCode()) {
            throw new RuntimeException(
                    "Erreur API update personne (status=" 
                    		+ response.getStatus() + ", body=" 
                    		+ readBody(response) + ")"
            );
        }

        return true;
    }

    //Delete
    @Override
    public boolean delete(Personne p) {

        ClientResponse response = getResource()
                .path("personne")
                .path(String.valueOf(p.getId()))
                .delete(ClientResponse.class);

        if (response.getStatus() != Status.NO_CONTENT.getStatusCode()) {
            throw new RuntimeException(
                    "Erreur API delete personne (status=" 
                    		+ response.getStatus() + ", body=" 
                    		+ readBody(response) + ")"
            );
        }

        return true;
    }

    public Personne authenticate(String email, String password) {

        JSONObject request = new JSONObject();
        request.put("email", email);
        request.put("password", password);

        ClientResponse response = getResource()
                .path("login")
                .type(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .post(ClientResponse.class, request.toString());

        if (response.getStatus() == Status.UNAUTHORIZED.getStatusCode()) {
            return null;
        }

        if (response.getStatus() != Status.CREATED.getStatusCode()) {
            throw new RuntimeException(
                    "Erreur API login (status=" 
                    		+ response.getStatus() + ", body=" 
                    		+ readBody(response) + ")"
            );
        }

        JSONObject body = new JSONObject(readBody(response));

        Personne p = new Personne();
        p.setId(body.getInt("id"));
        p.setName(body.getString("name"));
        p.setFirstName(body.getString("firstName"));
        p.setEmail(body.getString("email"));
        return p;
    }

    //JSON -> Model
    private Personne fromJsonPersonne(JSONObject json) {
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

    private List<Notification> parseNotifications(JSONArray arr) {
        List<Notification> list = new ArrayList<>();
        for (int i = 0; i < arr.length(); i++) {
            JSONObject json = arr.getJSONObject(i);

            Notification n = new Notification();
            n.setId(json.getInt("id"));
            n.setMessage(json.getString("message"));
            n.setSendDate(LocalDate.parse(json.getString("sendDate")));
            n.setRead(json.getBoolean("read"));

            list.add(n);
        }
        return list;
    }

    private List<ListeCadeau> parseListes(JSONArray arr) {
        List<ListeCadeau> list = new ArrayList<>();
        for (int i = 0; i < arr.length(); i++) {
            JSONObject json = arr.getJSONObject(i);

            ListeCadeau l = new ListeCadeau();
            l.setId(json.getInt("id"));
            l.setTitle(json.getString("title"));
            l.setEvenement(json.getString("evenement"));
            l.initCreationDate(LocalDate.parse(json.getString("creationDate")));
            l.setExpirationDate(LocalDate.parse(json.getString("expirationDate")));
            l.setStatut(json.getBoolean("statut"));

            if (json.has("shareLink") && !json.isNull("shareLink")) {
                l.setShareLink(json.getString("shareLink"));
            } else {
                l.setShareLink("https://default");
            }

            if (json.has("creatorId") && !json.isNull("creatorId")) {
                Personne creator = new Personne();
                creator.setId(json.getInt("creatorId"));
                l.setCreator(creator);
            }

            list.add(l);
        }
        return list;
    }

    private List<Reservation> parseReservations(JSONArray arr) {
        List<Reservation> list = new ArrayList<>();
        for (int i = 0; i < arr.length(); i++) {
            JSONObject json = arr.getJSONObject(i);

            Reservation r = new Reservation();
            r.setId(json.getInt("id"));
            r.setAmount(json.getDouble("amount"));
            r.setDateReservation(LocalDate.parse(json.getString("dateReservation")));

            if (json.has("cadeauId") && !json.isNull("cadeauId")) {
                Cadeau c = new Cadeau();
                c.setId(json.getInt("cadeauId"));
                r.setCadeau(c);
            }

            list.add(r);
        }
        return list;
    }
}
