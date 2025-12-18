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

    private ReservationDAO() {
        super();
    }

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

        if (r.getCadeau() == null) {
            throw new RuntimeException("Reservation.create : cadeau manquant");
        }
        json.put("cadeauId", r.getCadeau().getId());

        if (r.getPersonnes() == null || r.getPersonnes().isEmpty()) {
            throw new RuntimeException("Reservation.create : personne manquante");
        }
        json.put("personneId", r.getPersonnes().get(0).getId());

        ClientResponse response = getResource()
                .path("reservation")
                .type(MediaType.APPLICATION_JSON)
                .post(ClientResponse.class, json.toString());

        if (response.getStatus() == Status.CREATED.getStatusCode()) {
            String location = response.getHeaders().getFirst("Location");
            if (location == null || location.isBlank()) {
                throw new RuntimeException("Réponse création réservation sans header Location");
            }

            String[] parts = location.split("/");
            String last = parts[parts.length - 1];
            return Integer.parseInt(last);
        }

        throw new RuntimeException("Erreur API création réservation : " + response.getStatus());
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

        if (response.getStatus() != Status.OK.getStatusCode()) {
            return null;
        }

        JSONObject json = new JSONObject(response.getEntity(String.class));
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

        ArrayList<Reservation> list = new ArrayList<>();

        ClientResponse response = getResource()
                .path("reservation")
                .queryParam("loadCadeau", String.valueOf(loadCadeau))
                .queryParam("loadPersonnes", String.valueOf(loadPersonnes))
                .accept(MediaType.APPLICATION_JSON)
                .get(ClientResponse.class);

        if (response.getStatus() != Status.OK.getStatusCode()) {
            return list;
        }

        JSONArray array = new JSONArray(response.getEntity(String.class));

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

        return response.getStatus() == Status.NO_CONTENT.getStatusCode();
    }

    //Delete
    @Override
    public boolean delete(Reservation r) {

        ClientResponse response = getResource()
                .path("reservation")
                .path(String.valueOf(r.getId()))
                .delete(ClientResponse.class);

        return response.getStatus() == Status.NO_CONTENT.getStatusCode();
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
