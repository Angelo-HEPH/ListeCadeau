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
import be.couderiannello.dao.CadeauDAO;
import be.couderiannello.models.Cadeau;
import be.couderiannello.models.ListeCadeau;
import be.couderiannello.models.Reservation;

@Path("/cadeau")
public class CadeauAPI {

    private CadeauDAO getDao() {
        return new CadeauDAO(ConnectionBdd.getInstance());
    }

    // Create
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(String jsonBody) {
        try {
            JSONObject json = new JSONObject(jsonBody);
            json.remove("id");

            Cadeau c = new Cadeau();
            c.parse(json);

            int id = c.create(getDao());

            return Response.status(Status.CREATED)
                    .location(URI.create("cadeau/" + id))
                    .build();

        } catch (JSONException e) {
            return Response.status(Status.BAD_REQUEST)
                    .entity("Erreur : JSON invalide ou champs manquants.")
                    .build();

        } catch (IllegalArgumentException e) {
            return Response.status(Status.BAD_REQUEST)
                    .entity("Erreur : " + e.getMessage())
                    .build();

        } catch (IllegalStateException e) {
            return Response.status(Status.CONFLICT)
                    .entity("Erreur : " + e.getMessage())
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Erreur : Erreur interne.")
                    .build();
        }
    }

    // FindById
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findById(@PathParam("id") int id,
                             @QueryParam("loadListeCadeau") @DefaultValue("false") boolean loadListeCadeau,
                             @QueryParam("loadReservations") @DefaultValue("false") boolean loadReservations) {
        try {
            Cadeau c = Cadeau.findById(id, getDao(), loadListeCadeau, loadReservations);
            if (c == null) {
                return Response.status(Status.NOT_FOUND).build();
            }

            JSONObject json = toJson(c, loadListeCadeau, loadReservations);

            return Response.status(Status.OK)
                    .entity(json.toString())
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Erreur : Erreur interne.")
                    .build();
        }
    }

    // FindAll
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findAll(@QueryParam("loadListeCadeau") @DefaultValue("false") boolean loadListeCadeau,
                            @QueryParam("loadReservations") @DefaultValue("false") boolean loadReservations) {
        try {
            List<Cadeau> cadeaux = Cadeau.findAll(getDao(), loadListeCadeau, loadReservations);

            JSONArray array = new JSONArray();
            for (Cadeau c : cadeaux) {
                array.put(toJson(c, loadListeCadeau, loadReservations));
            }

            return Response.status(Status.OK)
                    .entity(array.toString())
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Erreur : Erreur interne.")
                    .build();
        }
    }

    // Update
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("id") int id, String body) {
        try {
            Cadeau existing = Cadeau.findById(id, getDao());
            if (existing == null) {
                return Response.status(Status.NOT_FOUND).build();
            }

            JSONObject json = new JSONObject(body);
            json.put("id", id);

            json.remove("listeCadeauId");

            existing.parse(json);

            boolean ok = existing.update(getDao());
            if (!ok) {
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
                    .entity("Erreur : " + e.getMessage())
                    .build();

        } catch (IllegalStateException e) {
            return Response.status(Status.CONFLICT)
                    .entity("Erreur : " + e.getMessage())
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Erreur : Erreur interne.")
                    .build();
        }
    }

    // Delete
    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") int id) {
        try {
            Cadeau c = Cadeau.findById(id, getDao());
            if (c == null) {
                return Response.status(Status.NOT_FOUND).build();
            }

            boolean deleted = Cadeau.delete(c, getDao());
            if (!deleted) {
                return Response.status(Status.BAD_REQUEST)
                        .entity("Erreur : Impossible de supprimer ce cadeau.")
                        .build();
            }

            return Response.status(Status.NO_CONTENT).build();

        } catch (IllegalArgumentException e) {
            return Response.status(Status.BAD_REQUEST)
                    .entity("Erreur : " + e.getMessage())
                    .build();

        } catch (IllegalStateException e) {
            return Response.status(Status.CONFLICT)
                    .entity("Erreur : " + e.getMessage())
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Erreur : Erreur interne.")
                    .build();
        }
    }

    private JSONObject toJson(Cadeau c, boolean includeListeCadeau, boolean includeReservations) {
        JSONObject json = c.unparse();

        if (includeListeCadeau && c.getListeCadeau() != null) {
            ListeCadeau l = c.getListeCadeau();

            JSONObject lcJson = new JSONObject();
            lcJson.put("id", l.getId());
            lcJson.put("title", l.getTitle());
            lcJson.put("evenement", l.getEvenement());
            lcJson.put("creationDate", l.getCreationDate());
            lcJson.put("expirationDate", l.getExpirationDate());
            lcJson.put("statut", l.isStatut());
            lcJson.put("shareLink", l.getShareLink());

            if (l.getCreator() != null) {
                lcJson.put("creatorId", l.getCreator().getId());
            }

            json.put("listeCadeau", lcJson);
        }

        if (includeReservations) {
            JSONArray array = new JSONArray();
            for (Reservation r : c.getReservations()) {
                array.put(r.unparse());
            }
            json.put("reservations", array);
        }

        return json;
    }
}
