package be.couderiannello.api;

import java.net.URI;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import be.couderiannello.connection.ConnectionBdd;
import be.couderiannello.dao.PersonneDAO;
import be.couderiannello.models.ListeCadeau;
import be.couderiannello.models.Notification;
import be.couderiannello.models.Personne;
import be.couderiannello.models.Reservation;

@Path("/personne")
public class PersonneAPI {

    private PersonneDAO getDao() {
        return new PersonneDAO(ConnectionBdd.getInstance());
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createPersonne(String personneJson) {
        try {
            JSONObject json = new JSONObject(personneJson);

            Personne p = new Personne();
            fillPersonneFromJson(p, json);

            int id = p.create(getDao());

            return Response
                    .status(Status.CREATED)
                    .location(URI.create("personne/" + id))
                    .build();

        } catch (JSONException e) {
            return Response.status(Status.BAD_REQUEST)
                    .entity("JSON invalide ou champs manquants.")
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Status.BAD_REQUEST)
                    .entity("Erreur : " + e.getMessage() + ".")
                    .build();
        } catch (Exception e) {
            return Response.status(Status.BAD_REQUEST)
                    .entity("Erreur lors de la création de la personne : " + e)
                    .build();
        }
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findById(@PathParam("id") int id,
                             @QueryParam("loadNotifications") @DefaultValue("false") boolean loadNotifications,
                             @QueryParam("loadCreatedLists") @DefaultValue("false") boolean loadCreatedLists,
                             @QueryParam("loadInvitedLists") @DefaultValue("false") boolean loadInvitedLists,
                             @QueryParam("loadReservations") @DefaultValue("false") boolean loadReservations) {
        try {
            PersonneDAO dao = getDao();

            Personne p = Personne.findById(
                    id, dao,
                    loadNotifications,
                    loadCreatedLists,
                    loadInvitedLists,
                    loadReservations
            );

            if (p == null) {
                return Response.status(Status.NOT_FOUND).build();
            }

            JSONObject json = toJson(p,
                    loadNotifications,
                    loadCreatedLists,
                    loadInvitedLists,
                    loadReservations);

            return Response
                    .status(Status.OK)
                    .entity(json.toString())
                    .build();

        } catch (Exception e) {
            return Response
                    .status(Status.BAD_REQUEST)
                    .entity("Erreur lors de la récupération de la personne.")
                    .build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findAll(@QueryParam("loadNotifications") @DefaultValue("false") boolean loadNotifications,
                            @QueryParam("loadCreatedLists") @DefaultValue("false") boolean loadCreatedLists,
                            @QueryParam("loadInvitedLists") @DefaultValue("false") boolean loadInvitedLists,
                            @QueryParam("loadReservations") @DefaultValue("false") boolean loadReservations) {
        try {
            PersonneDAO dao = getDao();

            List<Personne> personnes = Personne.findAll(
                    dao,
                    loadNotifications,
                    loadCreatedLists,
                    loadInvitedLists,
                    loadReservations
            );

            JSONArray array = new JSONArray();
            for (Personne p : personnes) {
                array.put(toJson(p,
                        loadNotifications,
                        loadCreatedLists,
                        loadInvitedLists,
                        loadReservations));
            }

            return Response.status(Status.OK)
                    .entity(array.toString())
                    .build();

        } catch (Exception e) {
            return Response.status(Status.BAD_REQUEST)
                    .entity("Erreur lors de la récupération des personnes.")
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deletePersonne(@PathParam("id") int id) {
        try {
            PersonneDAO dao = getDao();
            Personne p = Personne.findById(id, dao);

            if (p == null) {
                return Response
                        .status(Status.NOT_FOUND)
                        .build();
            }

            boolean deleted = Personne.delete(p, dao);

            if (!deleted) {
                return Response
                        .status(Status.BAD_REQUEST)
                        .entity("Erreur : Impossible de supprimer cette personne.")
                        .build();
            }

            return Response.status(Status.NO_CONTENT).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response
                    .status(Status.BAD_REQUEST)
                    .entity("Erreur lors de la suppression.")
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updatePersonne(@PathParam("id") int id, String personneJson) {
        try {
            PersonneDAO dao = getDao();

            Personne existing = Personne.findById(id, dao);
            if (existing == null) {
                return Response.status(Status.NOT_FOUND)
                        .entity("Erreur : Personne non trouvée.")
                        .build();
            }

            JSONObject json = new JSONObject(personneJson);
            fillPersonneFromJson(existing, json);

            boolean updated = existing.update(dao);

            if (!updated) {
                return Response.status(Status.SERVICE_UNAVAILABLE)
                        .entity("Erreur : La mise à jour a échoué.")
                        .build();
            }

            return Response.status(Status.NO_CONTENT).build();

        } catch (JSONException e) {
            return Response.status(Status.BAD_REQUEST)
                    .entity("Erreur : JSON invalide.")
                    .build();

        } catch (IllegalArgumentException e) {
            return Response.status(Status.BAD_REQUEST)
                    .entity("Erreur : " + e.getMessage() + ".")
                    .build();

        } catch (Exception e) {
            return Response.status(Status.BAD_REQUEST)
                    .entity("Erreur lors de la mise à jour de la personne.")
                    .build();
        }
    }
    
    private void fillPersonneFromJson(Personne p, JSONObject json) {
        p.setName(json.getString("name"));
        p.setFirstName(json.getString("firstName"));
        p.setAge(json.getInt("age"));
        p.setStreet(json.getString("street"));
        p.setCity(json.getString("city"));
        p.setStreetNumber(json.getString("streetNumber"));
        p.setPostalCode(json.getInt("postalCode"));
        p.setEmail(json.getString("email"));
        p.setPassword(json.getString("password"));
    }
    
    private JSONObject toJson(Personne p) {
        return toJson(p, false, false, false, false);
    }

    private JSONObject toJson(Personne p,
                              boolean includeNotifications,
                              boolean includeCreatedLists,
                              boolean includeInvitedLists,
                              boolean includeReservations) {

        JSONObject json = new JSONObject();

        json.put("id", p.getId());
        json.put("name", p.getName());
        json.put("firstName", p.getFirstName());
        json.put("age", p.getAge());
        json.put("street", p.getStreet());
        json.put("city", p.getCity());
        json.put("streetNumber", p.getStreetNumber());
        json.put("postalCode", p.getPostalCode());
        json.put("email", p.getEmail());

        //Relations optionnelles
        if (includeNotifications) {
            JSONArray array = new JSONArray();
            for (Notification n : p.getNotifications()) {
                array.put(NotificationtoJson(n));
            }
            json.put("notifications", array);
        }

        if (includeCreatedLists) {
            JSONArray array = new JSONArray();
            for (ListeCadeau l : p.getListeCadeauCreator()) {
                array.put(toJsonListeCadeau(l));
            }
            json.put("createdLists", array);
        }

        if (includeInvitedLists) {
            JSONArray array = new JSONArray();
            for (ListeCadeau l : p.getListeCadeauInvitations()) {
                array.put(toJsonListeCadeau(l));
            }
            json.put("invitedLists", array);
        }

        if (includeReservations) {
            JSONArray arr = new JSONArray();
            for (Reservation r : p.getReservations()) {
                arr.put(toJsonReservation(r));
            }
            json.put("reservations", arr);
        }

        return json;
    }

    private JSONObject NotificationtoJson(Notification n) {
        JSONObject json = new JSONObject();
        json.put("id", n.getId());
        json.put("message", n.getMessage());
        json.put("sendDate", n.getSendDate().toString());
        json.put("read", n.isRead());
        return json;
    }

    private JSONObject toJsonListeCadeau(ListeCadeau l) {
        JSONObject json = new JSONObject();
        json.put("id", l.getId());
        json.put("title", l.getTitle());
        json.put("evenement", l.getEvenement());
        json.put("creationDate", l.getCreationDate().toString());
        json.put("expirationDate", l.getExpirationDate().toString());
        json.put("statut", l.isStatut());
        json.put("shareLink", l.getShareLink());
        return json;
    }

    private JSONObject toJsonReservation(Reservation r) {
        JSONObject json = new JSONObject();
        json.put("id", r.getId());
        json.put("amount", r.getAmount());
        json.put("dateReservation", r.getDateReservation().toString());

        if (r.getCadeau() != null) {
            json.put("cadeauId", r.getCadeau().getId());
        }

        return json;
    }
}
