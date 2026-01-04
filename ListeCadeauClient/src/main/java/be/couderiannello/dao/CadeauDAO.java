package be.couderiannello.dao;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;

import be.couderiannello.enumeration.StatutCadeau;
import be.couderiannello.enumeration.StatutPriorite;
import be.couderiannello.models.Cadeau;
import be.couderiannello.models.ListeCadeau;
import be.couderiannello.models.Personne;
import be.couderiannello.models.Reservation;

public class CadeauDAO extends RestDAO<Cadeau> {

    private static CadeauDAO instance = null;

    private CadeauDAO() {
        super();
    }

    public static CadeauDAO getInstance() {
        if (instance == null) {
            instance = new CadeauDAO();
        }
        return instance;
    }

    //Create
    @Override
    public int create(Cadeau c) {

        JSONObject json = new JSONObject();
        json.put("name", c.getName());
        json.put("description", c.getDescription());
        json.put("price", c.getPrice());
        json.put("photo", c.getPhoto());
        json.put("linkSite", c.getLinkSite());
        json.put("priorite", c.getPriorite().name());
        json.put("statutCadeau", c.getStatutCadeau().name());
        json.put("listeCadeauId", c.getListeCadeau().getId());

        ClientResponse response = getResource()
                .path("cadeau")
                .type(MediaType.APPLICATION_JSON)
                .post(ClientResponse.class, json.toString());

        int status = response.getStatus();
        String body = readBody(response);

        if (status == Status.CREATED.getStatusCode()) {
            return extractIdFromLocation(response, "cadeau");
        }

        if (status == Status.BAD_REQUEST.getStatusCode()) {
            throw new IllegalArgumentException(body);
        }

        if (status == Status.CONFLICT.getStatusCode()) {
            throw new IllegalStateException(body);
        }

        if (body == null || body.isBlank()) {
            throw new RuntimeException("Erreur : Erreur API (" + status + ").");
        }
        throw new RuntimeException(body);
    }

    //Find
    @Override
    public Cadeau find(int id) {
        return find(id, false, false);
    }

    public Cadeau find(int id, boolean loadListeCadeau, boolean loadReservations) {

        ClientResponse response = getResource()
                .path("cadeau")
                .path(String.valueOf(id))
                .queryParam("loadListeCadeau", String.valueOf(loadListeCadeau))
                .queryParam("loadReservations", String.valueOf(loadReservations))
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
            if (body == null || body.isBlank()) {
                throw new RuntimeException("Erreur : Erreur API (" + status + ").");
            }
            throw new RuntimeException(body);
        }

        JSONObject json = new JSONObject(body);
        Cadeau c = fromJsonCadeau(json);

        if (loadListeCadeau && json.has("listeCadeau")) {
            c.setListeCadeau(parseListeCadeau(json.getJSONObject("listeCadeau")));
        }

        if (loadReservations && json.has("reservations")) {
            c.setReservations(parseReservations(json.getJSONArray("reservations")));
        }

        return c;
    }

    //FindAll
    @Override
    public List<Cadeau> findAll() {
        return findAll(false, false);
    }

    public List<Cadeau> findAll(boolean loadListeCadeau, boolean loadReservations) {

        ClientResponse response = getResource()
                .path("cadeau")
                .queryParam("loadListeCadeau", String.valueOf(loadListeCadeau))
                .queryParam("loadReservations", String.valueOf(loadReservations))
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
            if (body == null || body.isBlank()) {
                throw new RuntimeException("Erreur : Erreur API (" + status + ").");
            }
            throw new RuntimeException(body);
        }

        JSONArray array = new JSONArray(body);

        List<Cadeau> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject json = array.getJSONObject(i);
            Cadeau c = fromJsonCadeau(json);

            if (loadListeCadeau && json.has("listeCadeau")) {
                c.setListeCadeau(parseListeCadeau(json.getJSONObject("listeCadeau")));
            }

            if (loadReservations && json.has("reservations")) {
                c.setReservations(parseReservations(json.getJSONArray("reservations")));
            }

            list.add(c);
        }

        return list;
    }

    //Update
    @Override
    public boolean update(Cadeau c) {

        JSONObject json = new JSONObject();
        json.put("name", c.getName());
        json.put("description", c.getDescription());
        json.put("price", c.getPrice());
        json.put("photo", c.getPhoto());
        json.put("linkSite", c.getLinkSite());
        json.put("priorite", c.getPriorite().name());
        json.put("statutCadeau", c.getStatutCadeau().name());

        ClientResponse response = getResource()
                .path("cadeau")
                .path(String.valueOf(c.getId()))
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

        if (status == Status.BAD_REQUEST.getStatusCode()) {
            throw new IllegalArgumentException(body);
        }

        if (status == Status.CONFLICT.getStatusCode()) {
            throw new IllegalStateException(body);
        }

        if (body == null || body.isBlank()) {
            throw new RuntimeException("Erreur : Erreur API (" + status + ").");
        }
        throw new RuntimeException(body);
    }

    //Delete
    @Override
    public boolean delete(Cadeau c) {

        ClientResponse response = getResource()
                .path("cadeau")
                .path(String.valueOf(c.getId()))
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

        if (body == null || body.isBlank()) {
            throw new RuntimeException("Erreur : Erreur API (" + status + ").");
        }
        throw new RuntimeException(body);
    }

    //JSON -> Model
    private Cadeau fromJsonCadeau(JSONObject json) {

        Cadeau c = new Cadeau();
        c.setId(json.getInt("id"));
        c.setName(json.getString("name"));
        c.setDescription(json.getString("description"));
        c.setPrice(json.getDouble("price"));
        c.setPhoto(json.getString("photo"));
        c.setLinkSite(json.getString("linkSite"));
        c.setPriorite(StatutPriorite.valueOf(json.getString("priorite")));

        if (json.has("statutCadeau") && !json.isNull("statutCadeau")) {
            c.setStatutCadeau(StatutCadeau.valueOf(json.getString("statutCadeau")));
        } else {
            c.setStatutCadeau(StatutCadeau.DISPONIBLE);
        }

        if (json.has("listeCadeauId") && !json.isNull("listeCadeauId")) {
            ListeCadeau l = new ListeCadeau();
            l.setId(json.getInt("listeCadeauId"));
            c.setListeCadeau(l);
        }

        return c;
    }

    private ListeCadeau parseListeCadeau(JSONObject json) {

        ListeCadeau l = new ListeCadeau();

        l.setId(json.getInt("id"));
        l.setTitle(json.getString("title"));
        l.setEvenement(json.getString("evenement"));

        l.initCreationDate(LocalDate.parse(json.getString("creationDate")));

        if (!json.isNull("expirationDate")) {
            l.setExpirationDate(LocalDate.parse(json.getString("expirationDate")));
        }

        l.setStatut(json.getBoolean("statut"));
        l.setShareLink(json.getString("shareLink"));

        if (json.has("creatorId") && !json.isNull("creatorId")) {
            Personne p = new Personne();
            p.setId(json.getInt("creatorId"));
            l.setCreator(p);
        }

        return l;
    }

    private List<Reservation> parseReservations(JSONArray array) {
        List<Reservation> list = new ArrayList<>();

        for (int i = 0; i < array.length(); i++) {
            JSONObject json = array.getJSONObject(i);

            Reservation r = new Reservation();
            r.setId(json.getInt("id"));
            r.setAmount(json.getDouble("amount"));
            r.setDateReservation(LocalDate.parse(json.getString("dateReservation")));

            list.add(r);
        }
        return list;
    }
}
