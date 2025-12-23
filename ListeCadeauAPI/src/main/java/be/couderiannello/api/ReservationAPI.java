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
import be.couderiannello.dao.ReservationDAO;
import be.couderiannello.models.Cadeau;
import be.couderiannello.models.Personne;
import be.couderiannello.models.Reservation;

@Path("/reservation")
public class ReservationAPI {

    private ReservationDAO getDao() {
        return new ReservationDAO(ConnectionBdd.getInstance());
    }

    //Create
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(String jsonBody) {
        try {
            JSONObject json = new JSONObject(jsonBody);

            json.remove("id");
            Reservation r = new Reservation();
            r.parse(json);

            int id = r.create(getDao());

            return Response.status(Status.CREATED)
                    .location(URI.create("reservation/" + id))
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
                    .entity("Erreur lors de la création de la réservation.")
                    .build();
        }
    }

    //FindById
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findById(@PathParam("id") int id,
                             @QueryParam("loadCadeau") @DefaultValue("false") boolean loadCadeau,
                             @QueryParam("loadPersonnes") @DefaultValue("false") boolean loadPersonnes) {
        try {
            Reservation r = Reservation.findById(id, getDao(), loadCadeau, loadPersonnes);
            if (r == null) {
                return Response.status(Status.NOT_FOUND).build();
            }

            JSONObject json = toJson(r, loadCadeau, loadPersonnes);

            return Response.status(Status.OK)
                    .entity(json.toString())
                    .build();

        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Erreur lors de la récupération de la réservation.")
                    .build();
        }
    }

    //FindAll
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findAll(@QueryParam("loadCadeau") @DefaultValue("false") boolean loadCadeau,
                            @QueryParam("loadPersonnes") @DefaultValue("false") boolean loadPersonnes) {
        try {
            List<Reservation> reservations = Reservation.findAll(getDao(), loadCadeau, loadPersonnes);

            JSONArray array = new JSONArray();
            for (Reservation r : reservations) {
                array.put(toJson(r, loadCadeau, loadPersonnes));
            }

            return Response.status(Status.OK)
                    .entity(array.toString())
                    .build();

        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Erreur lors de la récupération des réservations.")
                    .build();
        }
    }

    //Update
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("id") int id, String body) {
        try {
            Reservation existing = Reservation.findById(id, getDao(), true, true);
            if (existing == null) {
                return Response.status(Status.NOT_FOUND)
                        .entity("Erreur : Réservation non trouvée.")
                        .build();
            }

            JSONObject json = new JSONObject(body);
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
                    .entity("Erreur lors de la mise à jour de la réservation.")
                    .build();
        }
    }

    //Delete
    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") int id) {
        try {
            Reservation r = Reservation.findById(id, getDao());

            if (r == null) {
                return Response.status(Status.NOT_FOUND).build();
            }

            boolean deleted = Reservation.delete(r, getDao());

            if (!deleted) {
                return Response.status(Status.BAD_REQUEST)
                        .entity("Erreur : Impossible de supprimer cette réservation.")
                        .build();
            }

            return Response.status(Status.NO_CONTENT)
            		.build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Erreur lors de la suppression.")
                    .build();
        }
    }

    private JSONObject toJson(Reservation r, boolean includeCadeau, boolean includePersonnes) {

        JSONObject json = r.unparse();

        if (includeCadeau && r.getCadeau() != null) {
            Cadeau c = r.getCadeau();

            JSONObject cadeauJson = new JSONObject();
            cadeauJson.put("id", c.getId());
            cadeauJson.put("name", c.getName());
            cadeauJson.put("description", c.getDescription());
            cadeauJson.put("price", c.getPrice());
            cadeauJson.put("photo", c.getPhoto());
            cadeauJson.put("linkSite", c.getLinkSite());
            cadeauJson.put("priorite", c.getPriorite().name());

            if (c.getListeCadeau() != null) {
                cadeauJson.put("listeCadeauId", c.getListeCadeau().getId());
            }

            json.put("cadeau", cadeauJson);
        }

        if (includePersonnes) {
            JSONArray array = new JSONArray();
            for (Personne p : r.getPersonnes()) {
                array.put(p.unparse());
            }
            json.put("personnes", array);
        }

        return json;
    }

}
