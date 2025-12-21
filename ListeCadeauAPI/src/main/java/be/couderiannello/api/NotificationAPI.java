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
import be.couderiannello.dao.NotificationDAO;
import be.couderiannello.models.Notification;

@Path("/notification")
public class NotificationAPI {

    private NotificationDAO getDao() {
        return new NotificationDAO(ConnectionBdd.getInstance());
    }

    //Create
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(String jsonBody) {
        try {
            JSONObject json = new JSONObject(jsonBody);

            Notification n = new Notification();
            n.parse(json, true);

            int id = n.create(getDao());

            return Response.status(Status.CREATED)
                    .location(URI.create("notification/" + id))
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
            return Response.status(Status.BAD_REQUEST)
                    .entity("Erreur lors de la création de la notification.")
                    .build();
        }
    }

    //FindById
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findById(@PathParam("id") int id,
                             @QueryParam("loadPersonne") @DefaultValue("false") boolean loadPersonne) {
        try {
            NotificationDAO dao = getDao();

            Notification n = Notification.findById(id, dao, loadPersonne);
            if (n == null) {
                return Response.status(Status.NOT_FOUND).build();
            }

            JSONObject json = toJson(n, loadPersonne);

            return Response.status(Status.OK)
                    .entity(json.toString())
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Status.BAD_REQUEST)
                    .entity("Erreur lors de la récupération de la notification.")
                    .build();
        }
    }

    //FindAll
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findAll(@QueryParam("loadPersonne") @DefaultValue("false") boolean loadPersonne) {
        try {
            NotificationDAO dao = getDao();

            List<Notification> notifications = Notification.findAll(dao, loadPersonne);

            JSONArray array = new JSONArray();
            for (Notification n : notifications) {
                array.put(toJson(n, loadPersonne));
            }

            return Response.status(Status.OK)
                    .entity(array.toString())
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Status.BAD_REQUEST)
                    .entity("Erreur lors de la récupération des notifications.")
                    .build();
        }
    }

    //Update
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("id") int id, String body) {
        try {
            NotificationDAO dao = getDao();

            Notification existing = Notification.findById(id, dao, false);
            if (existing == null) {
                return Response.status(Status.NOT_FOUND)
                        .entity("Erreur : Notification non trouvée.")
                        .build();
            }

            JSONObject json = new JSONObject(body);

            if (json.has("sendDate")) {
                return Response.status(Status.BAD_REQUEST)
                        .entity("Erreur : La date d'envoi ne peut pas être modifiée.")
                        .build();
            }
            if (json.has("personneId")) {
                return Response.status(Status.BAD_REQUEST)
                        .entity("Erreur : La personne d'une notification ne peut pas être modifiée.")
                        .build();
            }

            existing.parse(json, false);

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
            e.printStackTrace();
            return Response.status(Status.BAD_REQUEST)
                    .entity("Erreur lors de la mise à jour de la notification.")
                    .build();
        }
    }

    //Delete
    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") int id) {
        try {
            NotificationDAO dao = getDao();
            Notification n = Notification.findById(id, dao, false);

            if (n == null) {
                return Response.status(Status.NOT_FOUND).build();
            }

            boolean deleted = Notification.delete(n, dao);

            if (!deleted) {
                return Response.status(Status.BAD_REQUEST)
                        .entity("Erreur : Impossible de supprimer cette notification.")
                        .build();
            }

            return Response.status(Status.NO_CONTENT).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Status.BAD_REQUEST)
                    .entity("Erreur lors de la suppression.")
                    .build();
        }
    }

    private JSONObject toJson(Notification n, boolean includePersonne) {

        JSONObject json = n.unparse();

        if (includePersonne && n.getPersonne() != null) {
            json.put("personne", n.getPersonne().unparse());
        }

        return json;
    }
}
