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

    private PersonneDAO() {
        super();
    }

    public static PersonneDAO getInstance() {
        if (instance == null) {
            instance = new PersonneDAO();
        }
        return instance;
    }

    // CREATE
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

        if (response.getStatus() == Status.CREATED.getStatusCode()) {

            String location = response.getHeaders().getFirst("Location");
            if (location == null || location.isBlank()) {
                throw new RuntimeException("Réponse création personne sans header Location");
            }

            String[] parts = location.split("/");
            String last = parts[parts.length - 1];
            return Integer.parseInt(last);
        }

        throw new RuntimeException("Erreur API création personne : " + response.getStatus());
    }

    // FIND (par défaut : aucune relation)
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

        if (response.getStatus() != Status.OK.getStatusCode()) {
            return null;
        }

        JSONObject json = new JSONObject(response.getEntity(String.class));
        Personne p = fromJsonPersonne(json);

        // Relations optionnelles
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

    // FINDALL
    @Override
    public List<Personne> findAll() {
        return findAll(false, false, false, false);
    }

    public List<Personne> findAll(boolean loadNotifications,
                                  boolean loadCreatedLists,
                                  boolean loadInvitedLists,
                                  boolean loadReservations) {

        ArrayList<Personne> list = new ArrayList<>();

        ClientResponse response = getResource()
                .path("personne")
                .queryParam("loadNotifications", String.valueOf(loadNotifications))
                .queryParam("loadCreatedLists", String.valueOf(loadCreatedLists))
                .queryParam("loadInvitedLists", String.valueOf(loadInvitedLists))
                .queryParam("loadReservations", String.valueOf(loadReservations))
                .accept(MediaType.APPLICATION_JSON)
                .get(ClientResponse.class);

        if (response.getStatus() != Status.OK.getStatusCode()) {
            return list;
        }

        JSONArray array = new JSONArray(response.getEntity(String.class));

        for (int i = 0; i < array.length(); i++) {
            JSONObject json = array.getJSONObject(i);

            Personne p = fromJsonPersonne(json);

            // Relations optionnelles
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

    //UPDATE
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

        return response.getStatus() == Status.NO_CONTENT.getStatusCode();
    }

    //DELETE
    @Override
    public boolean delete(Personne p) {

        ClientResponse response = getResource()
                .path("personne")
                .path(String.valueOf(p.getId()))
                .delete(ClientResponse.class);

        return response.getStatus() == Status.NO_CONTENT.getStatusCode();
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
            throw new RuntimeException("Erreur API login : " + response.getStatus());
        }

        JSONObject body = new JSONObject(response.getEntity(String.class));

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
            l.setCreationDate(LocalDate.parse(json.getString("creationDate")));
            l.setExpirationDate(LocalDate.parse(json.getString("expirationDate")));
            l.setStatut(json.getBoolean("statut"));
            l.setShareLink(json.getString("shareLink"));

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
                int cadeauId = json.getInt("cadeauId");
                Cadeau c = new Cadeau();
                c.setId(cadeauId);
                r.setCadeau(c);
            }

            list.add(r);
        }
        return list;
    }
}
