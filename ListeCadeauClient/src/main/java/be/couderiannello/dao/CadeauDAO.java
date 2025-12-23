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

        if (c.getListeCadeau() == null) {
            throw new IllegalArgumentException("listeCadeau obligatoire pour créer un cadeau.");
        }
        json.put("listeCadeauId", c.getListeCadeau().getId());

        ClientResponse response = getResource()
                .path("cadeau")
                .type(MediaType.APPLICATION_JSON)
                .post(ClientResponse.class, json.toString());

        if (response.getStatus() != Status.CREATED.getStatusCode()) {
            throw new RuntimeException("Erreur API création cadeau (status="
                    + response.getStatus() + ", body=" 
            		+ readBody(response) + ")");
        }

        return extractIdFromLocation(response, "cadeau");
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

        if (response.getStatus() == Status.NOT_FOUND.getStatusCode()) {
            return null;
        }

        if (response.getStatus() != Status.OK.getStatusCode()) {
            throw new RuntimeException(
                    "Erreur API find cadeau (status=" 
                    		+ response.getStatus() + ", body=" 
                    		+ readBody(response) + ")"
            );
        }

        JSONObject json = new JSONObject(readBody(response));
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

        if (response.getStatus() != Status.OK.getStatusCode()) {
            throw new RuntimeException(
                    "Erreur API findAll cadeau (status=" 
                    		+ response.getStatus() + ", body=" 
                    		+ readBody(response) + ")"
            );
        }

        JSONArray array = new JSONArray(readBody(response));

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

        ClientResponse response = getResource()
                .path("cadeau")
                .path(String.valueOf(c.getId()))
                .type(MediaType.APPLICATION_JSON)
                .put(ClientResponse.class, json.toString());

        if (response.getStatus() != Status.NO_CONTENT.getStatusCode()) {
            throw new RuntimeException(
                    "Erreur API update cadeau (status=" 
                    		+ response.getStatus() + ", body=" 
                    		+ readBody(response) + ")"
            );
        }

        return true;
    }

    //Delete
    @Override
    public boolean delete(Cadeau c) {

        ClientResponse response = getResource()
                .path("cadeau")
                .path(String.valueOf(c.getId()))
                .delete(ClientResponse.class);

        if (response.getStatus() != Status.NO_CONTENT.getStatusCode()) {
            throw new RuntimeException(
                    "Erreur API delete cadeau (status=" 
                    		+ response.getStatus() + ", body=" 
                    		+ readBody(response) + ")"
            );
        }

        return true;
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

        if (!json.has("listeCadeauId")) {
            throw new RuntimeException(
                    "Réponse API invalide : listeCadeauId manquant pour le cadeau " + c.getId());
        }

        ListeCadeau l = new ListeCadeau();
        l.setId(json.getInt("listeCadeauId"));
        c.setListeCadeau(l);

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

        Personne p = new Personne();
        p.setId(json.getInt("creatorId"));
        l.setCreator(p);

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
