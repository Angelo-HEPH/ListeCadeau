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
import be.couderiannello.models.ListeCadeau;
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

    //CREATE
    @Override
    public int create(Cadeau c) {


        JSONObject json = new JSONObject();
        json.put("name", c.getName());
        json.put("description", c.getDescription());
        json.put("price", c.getPrice());
        json.put("photo", c.getPhoto());
        json.put("linkSite", c.getLinkSite());
        json.put("priorite", c.getPriorite().name());
        json.put("listeCadeauId", c.getListeCadeau().getId());

        ClientResponse response = getResource()
                .path("cadeau")
                .type(MediaType.APPLICATION_JSON)
                .post(ClientResponse.class, json.toString());

        if (response.getStatus() == Status.CREATED.getStatusCode()) {

            String location = response.getHeaders().getFirst("Location");
            if (location == null || location.isBlank()) {
                throw new RuntimeException("Réponse création cadeau sans header Location");
            }

            String[] parts = location.split("/");
            String last = parts[parts.length - 1];
            return Integer.parseInt(last);
        }

        throw new RuntimeException("Erreur API création cadeau : " + response.getStatus());
    }

    //FIND
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

        if (response.getStatus() != Status.OK.getStatusCode()) {
            return null;
        }

        JSONObject json = new JSONObject(response.getEntity(String.class));
        Cadeau c = fromJsonCadeau(json);

        if (loadListeCadeau && json.has("listeCadeau")) {
            c.setListeCadeau(parseListeCadeau(json.getJSONObject("listeCadeau")));
        }

        if (loadReservations && json.has("reservations")) {
            c.setReservations(parseReservations(json.getJSONArray("reservations")));
        }

        return c;
    }

    //FINDALL
    @Override
    public List<Cadeau> findAll() {
        return findAll(false, false);
    }

    public List<Cadeau> findAll(boolean loadListeCadeau, boolean loadReservations) {

        ArrayList<Cadeau> list = new ArrayList<>();

        ClientResponse response = getResource()
                .path("cadeau")
                .queryParam("loadListeCadeau", String.valueOf(loadListeCadeau))
                .queryParam("loadReservations", String.valueOf(loadReservations))
                .accept(MediaType.APPLICATION_JSON)
                .get(ClientResponse.class);

        if (response.getStatus() != Status.OK.getStatusCode()) {
            return list;
        }

        JSONArray array = new JSONArray(response.getEntity(String.class));

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

    //UPDATE
    @Override
    public boolean update(Cadeau c) {

        JSONObject json = new JSONObject();
        json.put("name", c.getName());
        json.put("description", c.getDescription());
        json.put("price", c.getPrice());
        json.put("photo", c.getPhoto());
        json.put("linkSite", c.getLinkSite());
        json.put("priorite", c.getPriorite().name());

        ClientResponse response = getResource()
                .path("cadeau")
                .path(String.valueOf(c.getId()))
                .type(MediaType.APPLICATION_JSON)
                .put(ClientResponse.class, json.toString());

        return response.getStatus() == Status.NO_CONTENT.getStatusCode();
    }

    // DELETE
    @Override
    public boolean delete(Cadeau c) {

        ClientResponse response = getResource()
                .path("cadeau")
                .path(String.valueOf(c.getId()))
                .delete(ClientResponse.class);

        return response.getStatus() == Status.NO_CONTENT.getStatusCode();
    }

    // JSON -> Model
    private Cadeau fromJsonCadeau(JSONObject json) {

        Cadeau c = new Cadeau();
        c.setId(json.getInt("id"));
        c.setName(json.getString("name"));
        c.setDescription(json.getString("description"));
        c.setPrice(json.getDouble("price"));
        c.setPhoto(json.getString("photo"));
        c.setLinkSite(json.getString("linkSite"));
        c.setPriorite(StatutPriorite.valueOf(json.getString("priorite")));

        if (!json.has("listeCadeauId")) {
            throw new RuntimeException("Réponse API invalide : listeCadeauId manquant pour le cadeau " + c.getId());
        }

        ListeCadeau l = new ListeCadeau();
        l.setId(json.getInt("listeCadeauId"));
        c.setListeCadeau(l);

        return c;
    }

    private ListeCadeau parseListeCadeau(JSONObject json) {
        ListeCadeau l = new ListeCadeau();
        l.setId(json.getInt("id"));
        if (json.has("title") && !json.isNull("title")) {
            l.setTitle(json.getString("title"));
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
