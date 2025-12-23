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

    //Create
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createPersonne(String personneJson) {
        try {
            JSONObject json = new JSONObject(personneJson);

            json.remove("id");
            Personne p = new Personne();
            p.parse(json);

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
            e.printStackTrace();
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Erreur lors de la création de la personne.")
                    .build();
        }
    }

    //FindById
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findById(@PathParam("id") int id,
                             @QueryParam("loadNotifications") @DefaultValue("false") boolean loadNotifications,
                             @QueryParam("loadCreatedLists") @DefaultValue("false") boolean loadCreatedLists,
                             @QueryParam("loadInvitedLists") @DefaultValue("false") boolean loadInvitedLists,
                             @QueryParam("loadReservations") @DefaultValue("false") boolean loadReservations) {
        try {
            Personne p = Personne.findById(
                    id, getDao(),
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
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Erreur lors de la récupération de la personne.")
                    .build();
        }
    }

    //FindByEmail
    @GET
    @Path("/byEmail")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findByEmail(@QueryParam("email") String email) {
        try {
            Personne p = Personne.findByEmail(email.trim().toLowerCase(), getDao());
            if (p == null) {
                return Response.status(Status.NOT_FOUND).build();
            }

            JSONObject json = toJson(p, false, false, false, false);

            return Response.status(Status.OK)
                    .entity(json.toString())
                    .build();

        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Erreur lors de la récupération de la personne par email.")
                    .build();
        }
    }

    //FindAll
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findAll(@QueryParam("loadNotifications") @DefaultValue("false") boolean loadNotifications,
                            @QueryParam("loadCreatedLists") @DefaultValue("false") boolean loadCreatedLists,
                            @QueryParam("loadInvitedLists") @DefaultValue("false") boolean loadInvitedLists,
                            @QueryParam("loadReservations") @DefaultValue("false") boolean loadReservations) {
        try {
            List<Personne> personnes = Personne.findAll(
            		getDao(),
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
            e.printStackTrace();
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Erreur lors de la récupération des personnes.")
                    .build();
        }
    }

    //Delete
    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deletePersonne(@PathParam("id") int id) {
        try {
            Personne p = Personne.findById(id, getDao());

            if (p == null) {
                return Response.status(Status.NOT_FOUND).build();
            }

            boolean deleted = Personne.delete(p, getDao());

            if (!deleted) {
                return Response.status(Status.BAD_REQUEST)
                        .entity("Erreur : Impossible de supprimer cette personne.")
                        .build();
            }

            return Response.status(Status.NO_CONTENT).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Erreur lors de la suppression.")
                    .build();
        }
    }

    //Update
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updatePersonne(@PathParam("id") int id, String personneJson) {
        try {
            Personne existing = Personne.findById(id, getDao());
            if (existing == null) {
                return Response.status(Status.NOT_FOUND)
                        .entity("Erreur : Personne non trouvée.")
                        .build();
            }

            JSONObject json = new JSONObject(personneJson);
            json.put("id", id);
            existing.parse(json);

            boolean updated = existing.update(getDao());
            if (!updated) {
                return Response.status(Status.SERVICE_UNAVAILABLE)
                        .entity("Erreur : La mise à jour a échoué.")
                        .build();
            }

            return Response.status(Status.NO_CONTENT)
            		.build();

        } catch (JSONException e) {
            return Response.status(Status.BAD_REQUEST)
                    .entity("Erreur : JSON invalide.")
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Status.BAD_REQUEST)
                    .entity("Erreur : " + e.getMessage() + ".")
                    .build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Erreur lors de la mise à jour de la personne.")
                    .build();
        }
    }

    private JSONObject toJson(Personne p,
                              boolean includeNotifications,
                              boolean includeCreatedLists,
                              boolean includeInvitedLists,
                              boolean includeReservations) {

        JSONObject json = p.unparse();

        if (includeNotifications) {
            JSONArray array = new JSONArray();
            for (Notification n : p.getNotifications()) {
                array.put(n.unparse());
            }
            json.put("notifications", array);
        }

        if (includeCreatedLists) {
            JSONArray array = new JSONArray();
            if (p.getListeCadeauCreator() != null) {
                for (ListeCadeau l : p.getListeCadeauCreator()) {
                    array.put(l.unparse());
                }
            }
            json.put("createdLists", array);
        }

        if (includeInvitedLists) {
            JSONArray array = new JSONArray();
            for (ListeCadeau l : p.getListeCadeauInvitations()) {
                array.put(l.unparse());
            }
            json.put("invitedLists", array);
        }

        if (includeReservations) {
            JSONArray array = new JSONArray();
            for (Reservation r : p.getReservations()) {
                array.put(r.unparse());
            }
            json.put("reservations", array);
        }

        return json;
    }
}
