package be.couderiannello.dao;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import javax.ws.rs.core.MediaType;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;

import be.couderiannello.enumeration.StatutPriorite;
import be.couderiannello.models.Cadeau;
import be.couderiannello.models.ListeCadeau;
import be.couderiannello.models.Personne;

public class ListeCadeauDAO extends RestDAO<ListeCadeau> {

    private static ListeCadeauDAO instance = null;

    private ListeCadeauDAO() { }

    public static ListeCadeauDAO getInstance() {
        if (instance == null) {
            instance = new ListeCadeauDAO();
        }
        return instance;
    }

    //Create
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

        if (response.getStatus() != Status.CREATED.getStatusCode()) {
            throw new RuntimeException(
                    "Erreur API création listeCadeau (status=" 
                    		+ response.getStatus() + ", body=" 
                    		+ readBody(response) + ")"
            );
        }

        return extractIdFromLocation(response, "listeCadeau");
    }

    //Find
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

        if (response.getStatus() == Status.NOT_FOUND.getStatusCode()) {
            return null;
        }

        if (response.getStatus() != Status.OK.getStatusCode()) {
            throw new RuntimeException(
                    "Erreur API find listeCadeau (status=" 
                    		+ response.getStatus() + ", body=" 
                    		+ readBody(response) + ")"
            );
        }

        JSONObject json = new JSONObject(readBody(response));
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

    //FindAll
    @Override
    public List<ListeCadeau> findAll() {
        return findAll(false, false, false);
    }

    public List<ListeCadeau> findAll(boolean loadCreator, boolean loadInvites, boolean loadCadeaux) {

        ClientResponse response = getResource()
                .path("listeCadeau")
                .queryParam("loadCreator", String.valueOf(loadCreator))
                .queryParam("loadInvites", String.valueOf(loadInvites))
                .queryParam("loadCadeaux", String.valueOf(loadCadeaux))
                .accept(MediaType.APPLICATION_JSON)
                .get(ClientResponse.class);

        if (response.getStatus() != Status.OK.getStatusCode()) {
            throw new RuntimeException(
                    "Erreur API findAll listeCadeau (status=" 
                    		+ response.getStatus() + ", body=" 
                    		+ readBody(response) + ")"
            );
        }

        JSONArray array = new JSONArray(readBody(response));

        ArrayList<ListeCadeau> list = new ArrayList<>();
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

    //Update
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

        if (response.getStatus() != Status.NO_CONTENT.getStatusCode()) {
            throw new RuntimeException(
                    "Erreur API update listeCadeau (status=" 
                    		+ response.getStatus() + ", body=" 
                    		+ readBody(response) + ")"
            );
        }

        return true;
    }

    //Delete
    @Override
    public boolean delete(ListeCadeau l) {

        ClientResponse response = getResource()
                .path("listeCadeau")
                .path(String.valueOf(l.getId()))
                .delete(ClientResponse.class);

        if (response.getStatus() != Status.NO_CONTENT.getStatusCode()) {
            throw new RuntimeException(
                    "Erreur API delete listeCadeau (status=" 
                    		+ response.getStatus() + ", body=" 
                    		+ readBody(response) + ")"
            );
        }

        return true;
    }

    //JSON -> Model
    private ListeCadeau fromJsonListeCadeau(JSONObject json) {

        ListeCadeau l = new ListeCadeau();

        l.setId(json.getInt("id"));
        l.setTitle(json.getString("title"));
        l.setEvenement(json.getString("evenement"));

        if (json.has("creationDate") && !json.isNull("creationDate")) {
            l.initCreationDate(LocalDate.parse(json.getString("creationDate")));
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

        if (json.has("creatorId") && !json.isNull("creatorId")) {
            Personne creator = new Personne();
            creator.setId(json.getInt("creatorId"));
            l.setCreator(creator);
        }

        return l;
    }

    private Personne parsePersonne(JSONObject json) {
        Personne p = new Personne();
        p.parse(json);
        return p;
    }

    private Personne parseCreator(JSONObject json) {
        return parsePersonne(json);
    }

    private List<Personne> parseInvites(JSONArray array) {
        List<Personne> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject json = array.getJSONObject(i);
            list.add(parsePersonne(json));
        }
        return list;
    }

    private List<Cadeau> parseCadeaux(JSONArray array, ListeCadeau owner) {
        List<Cadeau> list = new ArrayList<>();

        for (int i = 0; i < array.length(); i++) {

            JSONObject json = array.getJSONObject(i);

            Cadeau c = new Cadeau();
            c.setId(json.getInt("id"));
            c.setName(json.getString("name"));
            c.setDescription(json.getString("description"));
            c.setPrice(json.getDouble("price"));
            c.setPhoto(json.getString("photo"));
            c.setLinkSite(json.getString("linkSite"));
            c.setPriorite(StatutPriorite.valueOf(json.getString("priorite")));

            c.setListeCadeau(owner);

            list.add(c);
        }

        return list;
    }
    
    public boolean addInvite(int listeId, int personneId) {

        JSONObject json = new JSONObject();
        json.put("personneId", personneId);

        ClientResponse response = getResource()
                .path("listeCadeau")
                .path(String.valueOf(listeId))
                .path("invites")
                .type(MediaType.APPLICATION_JSON)
                .post(ClientResponse.class, json.toString());

        int status = response.getStatus();

        if (status == Status.CREATED.getStatusCode()) {
            return true;
        }

        if (status == Status.CONFLICT.getStatusCode()) {
            throw new IllegalArgumentException("Cette personne est déjà invitée à cette liste.");
        }

        if (status == Status.BAD_REQUEST.getStatusCode()) {
            throw new IllegalStateException("Le créateur ne peut pas s'inviter lui-même.");
        }

        if (status == Status.NOT_FOUND.getStatusCode()) {
            throw new IllegalArgumentException("La liste de cadeaux n'existe pas.");
        }

        throw new RuntimeException("Erreur lors de l'appel à l'API (status=" + status + ")");
    }

    public boolean removeInvite(int listeId, int personneId) {

        ClientResponse response = getResource()
                .path("listeCadeau")
                .path(String.valueOf(listeId))
                .path("invites")
                .path(String.valueOf(personneId))
                .delete(ClientResponse.class);

        int status = response.getStatus();

        if (status == Status.NO_CONTENT.getStatusCode()) {
            return true;
        }

        if (status == Status.NOT_FOUND.getStatusCode()) {
            throw new NoSuchElementException();
        }

        if (status == Status.BAD_REQUEST.getStatusCode()) {
            throw new IllegalArgumentException("Paramètres invalides.");
        }

        throw new RuntimeException("Erreur API removeInvite (status=" + status + ")");
    }
}
