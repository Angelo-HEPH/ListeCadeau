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
import be.couderiannello.dao.ListeCadeauDAO;
import be.couderiannello.models.Cadeau;
import be.couderiannello.models.ListeCadeau;
import be.couderiannello.models.Personne;

@Path("/listeCadeau")
public class ListeCadeauAPI {

    private ListeCadeauDAO getDao() {
        return new ListeCadeauDAO(ConnectionBdd.getInstance());
    }

    //Create
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(String jsonBody) {
        try {
            JSONObject json = new JSONObject(jsonBody);

            ListeCadeau l = new ListeCadeau();
            l.parse(json);

            int id = l.create(getDao());

            return Response.status(Status.CREATED)
                    .location(URI.create("listeCadeau/" + id))
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
                    .entity("Erreur lors de la création de la liste.")
                    .build();
        }
    }

    //FindById
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findById(@PathParam("id") int id,
                             @QueryParam("loadCreator") @DefaultValue("false") boolean loadCreator,
                             @QueryParam("loadInvites") @DefaultValue("false") boolean loadInvites,
                             @QueryParam("loadCadeaux") @DefaultValue("false") boolean loadCadeaux) {
        try {
            ListeCadeauDAO dao = getDao();

            ListeCadeau l = ListeCadeau.findById(id, dao, loadCreator, loadInvites, loadCadeaux);
            if (l == null) {
                return Response.status(Status.NOT_FOUND).build();
            }

            JSONObject json = toJson(l, loadCreator, loadInvites, loadCadeaux);

            return Response.status(Status.OK)
                    .entity(json.toString())
                    .build();

        } catch (Exception e) {
            return Response.status(Status.BAD_REQUEST)
                    .entity("Erreur lors de la récupération de la liste.")
                    .build();
        }
    }

    //FindAll
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findAll(@QueryParam("loadCreator") @DefaultValue("false") boolean loadCreator,
                            @QueryParam("loadInvites") @DefaultValue("false") boolean loadInvites,
                            @QueryParam("loadCadeaux") @DefaultValue("false") boolean loadCadeaux) {
        try {
            ListeCadeauDAO dao = getDao();

            List<ListeCadeau> listes = ListeCadeau.findAll(dao, loadCreator, loadInvites, loadCadeaux);

            JSONArray array = new JSONArray();
            for (ListeCadeau l : listes) {
                array.put(toJson(l, loadCreator, loadInvites, loadCadeaux));
            }

            return Response.status(Status.OK)
                    .entity(array.toString())
                    .build();

        } catch (Exception e) {
            return Response.status(Status.BAD_REQUEST)
                    .entity("Erreur lors de la récupération des listes.")
                    .build();
        }
    }

    //Update
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("id") int id, String body) {
        try {
            ListeCadeauDAO dao = getDao();

            ListeCadeau existing = ListeCadeau.findById(id, dao);
            if (existing == null) {
                return Response.status(Status.NOT_FOUND)
                        .entity("Erreur : ListeCadeau non trouvée.")
                        .build();
            }

            JSONObject json = new JSONObject(body);

            if (json.has("creationDate")) {
                return Response.status(Status.BAD_REQUEST)
                        .entity("Erreur : La date de création ne peut pas être modifiée.")
                        .build();
            }

            existing.parse(json);

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
                    .entity("Erreur lors de la mise à jour de la liste.")
                    .build();
        }
    }

    //Delete
    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") int id) {
        try {
            ListeCadeauDAO dao = getDao();
            ListeCadeau l = ListeCadeau.findById(id, dao);

            if (l == null) {
                return Response.status(Status.NOT_FOUND).build();
            }

            boolean deleted = ListeCadeau.delete(l, dao);

            if (!deleted) {
                return Response.status(Status.BAD_REQUEST)
                        .entity("Erreur : Impossible de supprimer cette liste.")
                        .build();
            }

            return Response.status(Status.NO_CONTENT).build();

        } catch (Exception e) {
            return Response.status(Status.BAD_REQUEST)
                    .entity("Erreur lors de la suppression.")
                    .build();
        }
    }

    private JSONObject toJson(ListeCadeau l, boolean includeCreator, boolean includeInvites, boolean includeCadeaux) {

        JSONObject json = l.unparse();

        if (includeCreator && l.getCreator() != null) {
            json.put("creator", l.getCreator().unparse());
        }

        if (includeInvites) {
            JSONArray array = new JSONArray();
            for (Personne p : l.getInvites()) {
                array.put(p.unparse());
            }
            json.put("invites", array);
        }

        if (includeCadeaux) {
            JSONArray array = new JSONArray();
            for (Cadeau c : l.getCadeaux()) {
                array.put(c.unparse());
            }
            json.put("cadeaux", array);
        }

        return json;
    }
}
