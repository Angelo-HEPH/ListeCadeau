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
import be.couderiannello.models.Personne;

public class ListeCadeauDAO extends RestDAO<ListeCadeau> {

    private static ListeCadeauDAO instance = null;

    private ListeCadeauDAO() {
        super();
    }

    public static ListeCadeauDAO getInstance() {
        if (instance == null) {
            instance = new ListeCadeauDAO();
        }
        return instance;
    }

    // CREATE
    @Override
    public int create(ListeCadeau l) {

        JSONObject json = new JSONObject();
        json.put("title", l.getTitle());
        json.put("evenement", l.getEvenement());
        json.put("expirationDate", l.getExpirationDate().toString());
        json.put("statut", l.isStatut());
        json.put("shareLink", l.getShareLink());

        if (l.getCreator() == null) {
            throw new IllegalArgumentException("Creator obligatoire pour créer une ListeCadeau.");
        }
        json.put("creatorId", l.getCreator().getId());

        ClientResponse response = getResource()
                .path("listeCadeau")
                .type(MediaType.APPLICATION_JSON)
                .post(ClientResponse.class, json.toString());

        if (response.getStatus() == Status.CREATED.getStatusCode()) {

            String location = response.getHeaders().getFirst("Location");
            if (location == null || location.isBlank()) {
                throw new RuntimeException("Réponse création listeCadeau sans header Location");
            }

            String[] parts = location.split("/");
            String last = parts[parts.length - 1];
            return Integer.parseInt(last);
        }

        throw new RuntimeException("Erreur API création listeCadeau : " + response.getStatus());
    }

    //FIND
    @Override
    public ListeCadeau find(int id) {
        return find(id, false, false, false);
    }

    public ListeCadeau find(int id, boolean loadCreator, boolean loadInvites, boolean loadCadeaux) {

        ClientResponse response = getResource()
                .path("listeCadeau")
                .path(String.valueOf(id))
                .queryParam("loadCreator", String.valueOf(loadCreator))
                .queryParam("loadInvites", String.valueOf(loadInvites))
                .queryParam("loadCadeaux", String.valueOf(loadCadeaux))
                .accept(MediaType.APPLICATION_JSON)
                .get(ClientResponse.class);

        if (response.getStatus() != Status.OK.getStatusCode()) {
            return null;
        }

        JSONObject json = new JSONObject(response.getEntity(String.class));
        ListeCadeau l = fromJsonListeCadeau(json);

        if (loadCreator && json.has("creator")) {
            l.setCreator(parseCreator(json.getJSONObject("creator")));
        }

        if (loadInvites && json.has("invites")) {
            l.setInvites(parseInvites(json.getJSONArray("invites")));
        }

        if (loadCadeaux && json.has("cadeaux")) {
            l.setCadeaux(parseCadeaux(json.getJSONArray("cadeaux"), l));
        }

        return l;
    }

    //FINDALL
    @Override
    public List<ListeCadeau> findAll() {
        return findAll(false, false, false);
    }

    public List<ListeCadeau> findAll(boolean loadCreator, boolean loadInvites, boolean loadCadeaux) {

        ArrayList<ListeCadeau> list = new ArrayList<>();

        ClientResponse response = getResource()
                .path("listeCadeau")
                .queryParam("loadCreator", String.valueOf(loadCreator))
                .queryParam("loadInvites", String.valueOf(loadInvites))
                .queryParam("loadCadeaux", String.valueOf(loadCadeaux))
                .accept(MediaType.APPLICATION_JSON)
                .get(ClientResponse.class);

        if (response.getStatus() != Status.OK.getStatusCode()) {
            return list;
        }

        JSONArray array = new JSONArray(response.getEntity(String.class));

        for (int i = 0; i < array.length(); i++) {
            JSONObject json = array.getJSONObject(i);

            ListeCadeau l = fromJsonListeCadeau(json);

            if (loadCreator && json.has("creator")) {
                l.setCreator(parseCreator(json.getJSONObject("creator")));
            }

            if (loadInvites && json.has("invites")) {
                l.setInvites(parseInvites(json.getJSONArray("invites")));
            }

            if (loadCadeaux && json.has("cadeaux")) {
                l.setCadeaux(parseCadeaux(json.getJSONArray("cadeaux"), l));
            }

            list.add(l);
        }

        return list;
    }

    //UPDATE
    @Override
    public boolean update(ListeCadeau l) {

        JSONObject json = new JSONObject();
        json.put("title", l.getTitle());
        json.put("evenement", l.getEvenement());
        json.put("expirationDate", l.getExpirationDate().toString());
        json.put("statut", l.isStatut());
        json.put("shareLink", l.getShareLink());

        ClientResponse response = getResource()
                .path("listeCadeau")
                .path(String.valueOf(l.getId()))
                .type(MediaType.APPLICATION_JSON)
                .put(ClientResponse.class, json.toString());

        return response.getStatus() == Status.NO_CONTENT.getStatusCode();
    }

    //DELETE
    @Override
    public boolean delete(ListeCadeau l) {

        ClientResponse response = getResource()
                .path("listeCadeau")
                .path(String.valueOf(l.getId()))
                .delete(ClientResponse.class);

        return response.getStatus() == Status.NO_CONTENT.getStatusCode();
    }

    //JSON -> Model

    private ListeCadeau fromJsonListeCadeau(JSONObject json) {

        ListeCadeau l = new ListeCadeau();

        l.setId(json.getInt("id"));
        l.setTitle(json.getString("title"));
        l.setEvenement(json.getString("evenement"));

        if (json.has("creationDate") && !json.isNull("creationDate")) {
            l.setCreationDate(LocalDate.parse(json.getString("creationDate")));
        }

        if (json.has("expirationDate") && !json.isNull("expirationDate")) {
            l.setExpirationDate(LocalDate.parse(json.getString("expirationDate")));
        }

        l.setStatut(json.getBoolean("statut"));

        String share = json.optString("shareLink", "");
        if (share != null && !share.isBlank()) {
            l.setShareLink(share);
        } else {
            l.setShareLink("https://default");
        }

        if (!json.has("creatorId")) {
            throw new RuntimeException("Réponse API invalide : creatorId manquant pour la liste " + l.getId());
        }

        if (!json.isNull("creatorId")) {
            Personne p = new Personne();
            p.setId(json.getInt("creatorId"));
            l.setCreator(p);
        }

        return l;
    }

    private Personne parseCreator(JSONObject json) {
        Personne p = new Personne();
        p.setId(json.getInt("id"));
        return p;
    }

    private List<Personne> parseInvites(JSONArray array) {
        List<Personne> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject json = array.getJSONObject(i);

            Personne p = new Personne();
            p.setId(json.getInt("id"));

            list.add(p);
        }
        return list;
    }

    private List<Cadeau> parseCadeaux(JSONArray array, ListeCadeau owner) {
        List<Cadeau> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject json = array.getJSONObject(i);

            Cadeau c = new Cadeau();
            c.setId(json.getInt("id"));

            if (json.has("name") && !json.isNull("name")) {
                c.setName(json.getString("name"));
            }

            c.setListeCadeau(owner);
            list.add(c);
        }
        return list;
    }
}
