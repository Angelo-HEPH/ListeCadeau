package be.couderiannello.dao;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;

import be.couderiannello.enumeration.StatutPriorite;
import be.couderiannello.models.Cadeau;
import be.couderiannello.models.Personne;
import be.couderiannello.models.Reservation;

public class ReservationDAO extends RestDAO<Reservation> {

    private static ReservationDAO instance = null;

    private ReservationDAO() { }

    public static ReservationDAO getInstance() {
        if (instance == null) {
            instance = new ReservationDAO();
        }
        return instance;
    }

    //Create
    @Override
    public int create(Reservation r) {

        JSONObject json = new JSONObject();
        json.put("amount", r.getAmount());
        json.put("cadeauId", r.getCadeau().getId());
        json.put("personneId", r.getPersonnes().get(0).getId());

        ClientResponse response = getResource()
                .path("reservation")
                .type(MediaType.APPLICATION_JSON)
                .post(ClientResponse.class, json.toString());

        int status = response.getStatus();
        String body = readBody(response);

        if (status == Status.CREATED.getStatusCode()) {
            return extractIdFromLocation(response, "reservation");
        }

        if (status == Status.BAD_REQUEST.getStatusCode()) {
            throw new IllegalArgumentException(body);
        }

        if (status == Status.CONFLICT.getStatusCode()) {
            throw new IllegalStateException(body);
        }

        throw new RuntimeException(body);
    }

    //Find
    @Override
    public Reservation find(int id) {
        return find(id, false, false);
    }

    public Reservation find(int id, boolean loadCadeau, boolean loadPersonnes) {

        ClientResponse response = getResource()
                .path("reservation")
                .path(String.valueOf(id))
                .queryParam("loadCadeau", String.valueOf(loadCadeau))
                .queryParam("loadPersonnes", String.valueOf(loadPersonnes))
                .accept(MediaType.APPLICATION_JSON)
                .get(ClientResponse.class);

        int status = response.getStatus();
        String body = readBody(response);

        if (status == Status.NOT_FOUND.getStatusCode()) {
            return null;
        }

        if (status != Status.OK.getStatusCode()) {
            if (status == Status.BAD_REQUEST.getStatusCode()) {
                throw new IllegalArgumentException(body);
            }
            if (status == Status.CONFLICT.getStatusCode()) {
                throw new IllegalStateException(body);
            }
            throw new RuntimeException(body);
        }

        JSONObject json = new JSONObject(body);
        Reservation r = fromJsonReservation(json);

        if (loadCadeau && json.has("cadeau") && !json.isNull("cadeau")) {
            r.setCadeau(parseCadeau(json.getJSONObject("cadeau")));
        }

        if (loadPersonnes && json.has("personnes") && !json.isNull("personnes")) {
            r.setPersonnes(parsePersonnes(json.getJSONArray("personnes")));
        }

        return r;
    }

    //FindAll
    @Override
    public List<Reservation> findAll() {
        return findAll(false, false);
    }

    public List<Reservation> findAll(boolean loadCadeau, boolean loadPersonnes) {

        ClientResponse response = getResource()
                .path("reservation")
                .queryParam("loadCadeau", String.valueOf(loadCadeau))
                .queryParam("loadPersonnes", String.valueOf(loadPersonnes))
                .accept(MediaType.APPLICATION_JSON)
                .get(ClientResponse.class);

        int status = response.getStatus();
        String body = readBody(response);

        if (status != Status.OK.getStatusCode()) {
            if (status == Status.BAD_REQUEST.getStatusCode()) {
                throw new IllegalArgumentException(body);
            }
            if (status == Status.CONFLICT.getStatusCode()) {
                throw new IllegalStateException(body);
            }
            throw new RuntimeException(body);
        }

        JSONArray array = new JSONArray(body);
        ArrayList<Reservation> list = new ArrayList<>();

        for (int i = 0; i < array.length(); i++) {
            JSONObject json = array.getJSONObject(i);

            Reservation r = fromJsonReservation(json);

            if (loadCadeau && json.has("cadeau") && !json.isNull("cadeau")) {
                r.setCadeau(parseCadeau(json.getJSONObject("cadeau")));
            }

            if (loadPersonnes && json.has("personnes") && !json.isNull("personnes")) {
                r.setPersonnes(parsePersonnes(json.getJSONArray("personnes")));
            }

            list.add(r);
        }

        return list;
    }

    //Update
    @Override
    public boolean update(Reservation r) {

        JSONObject json = new JSONObject();
        json.put("amount", r.getAmount());
        json.put("cadeauId", r.getCadeau().getId());

        ClientResponse response = getResource()
                .path("reservation")
                .path(String.valueOf(r.getId()))
                .type(MediaType.APPLICATION_JSON)
                .put(ClientResponse.class, json.toString());

        int status = response.getStatus();
        String body = readBody(response);

        if (status == Status.NO_CONTENT.getStatusCode()) {
            return true;
        }

        if (status == Status.NOT_FOUND.getStatusCode()) {
            return false;
        }

        if (status == Status.SERVICE_UNAVAILABLE.getStatusCode()) {
            throw new IllegalStateException(body);
        }

        if (status == Status.BAD_REQUEST.getStatusCode()) {
            throw new IllegalArgumentException(body);
        }

        if (status == Status.CONFLICT.getStatusCode()) {
            throw new IllegalStateException(body);
        }

        throw new RuntimeException(body);
    }

    //Delete
    @Override
    public boolean delete(Reservation r) {

        ClientResponse response = getResource()
                .path("reservation")
                .path(String.valueOf(r.getId()))
                .delete(ClientResponse.class);

        int status = response.getStatus();
        String body = readBody(response);

        if (status == Status.NO_CONTENT.getStatusCode()) {
            return true;
        }

        if (status == Status.NOT_FOUND.getStatusCode()) {
            return false;
        }

        if (status == Status.BAD_REQUEST.getStatusCode()) {
            throw new IllegalArgumentException(body);
        }

        if (status == Status.CONFLICT.getStatusCode()) {
            throw new IllegalStateException(body);
        }

        throw new RuntimeException(body);
    }

    //JSON -> Model
    private Reservation fromJsonReservation(JSONObject json) {

        Reservation r = new Reservation();
        r.setId(json.getInt("id"));
        r.setAmount(json.getDouble("amount"));

        if (json.has("dateReservation") && !json.isNull("dateReservation")) {
            r.setDateReservation(LocalDate.parse(json.getString("dateReservation")));
        }

        if (json.has("cadeauId") && !json.isNull("cadeauId")) {
            Cadeau c = new Cadeau();
            c.setId(json.getInt("cadeauId"));
            r.setCadeau(c);
        }

        return r;
    }

    private Cadeau parseCadeau(JSONObject json) {
        Cadeau c = new Cadeau();
        c.setId(json.getInt("id"));

        c.setName(json.getString("name"));
        c.setDescription(json.getString("description"));
        c.setPrice(json.getDouble("price"));
        c.setPhoto(json.getString("photo"));
        c.setLinkSite(json.getString("linkSite"));
        c.setPriorite(StatutPriorite.valueOf(json.getString("priorite")));

        return c;
    }

    private List<Personne> parsePersonnes(JSONArray array) {
        List<Personne> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject json = array.getJSONObject(i);

            Personne p = new Personne();
            p.setId(json.getInt("id"));

            p.setName(json.getString("name"));
            p.setFirstName(json.getString("firstName"));
            p.setEmail(json.getString("email"));
            p.setAge(json.getInt("age"));
            p.setStreet(json.getString("street"));
            p.setCity(json.getString("city"));
            p.setStreetNumber(json.getString("streetNumber"));
            p.setPostalCode(json.getInt("postalCode"));

            list.add(p);
        }
        return list;
    }
}