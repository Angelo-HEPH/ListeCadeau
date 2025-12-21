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
import be.couderiannello.enumeration.StatutPriorite;
import be.couderiannello.models.Cadeau;
import be.couderiannello.models.Reservation;

@Path("/cadeau")
public class CadeauAPI {

    private CadeauDAO getDao() {
        return new CadeauDAO(ConnectionBdd.getInstance());
    }

    //Create
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(String jsonBody) {
        try {
            JSONObject json = new JSONObject(jsonBody);

            
            Cadeau c = new Cadeau();
            c.parse(json);
            
            int id = c.create(getDao());
            

            return Response.status(Status.CREATED)
                    .location(URI.create("cadeau/" + id))
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
                    .entity("Erreur lors de la création du cadeau.")
                    .build();
        }
    }

    //FindById
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findById(@PathParam("id") int id,
                             @QueryParam("loadListeCadeau") @DefaultValue("false") boolean loadListeCadeau,
                             @QueryParam("loadReservations") @DefaultValue("false") boolean loadReservations) {
        try {
            CadeauDAO dao = getDao();

            Cadeau c = Cadeau.findById(id, dao, loadListeCadeau, loadReservations);
            if (c == null) {
                return Response.status(Status.NOT_FOUND)
                		.build();
            }

            JSONObject json = toJson(c, loadListeCadeau, loadReservations);

            return Response.status(Status.OK)
                    .entity(json.toString())
                    .build();

        } catch (Exception e) {
            return Response.status(Status.BAD_REQUEST)
                    .entity("Erreur lors de la récupération du cadeau.")
                    .build();
        }
    }

    //FindAll
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findAll(@QueryParam("loadListeCadeau") @DefaultValue("false") boolean loadListeCadeau,
                            @QueryParam("loadReservations") @DefaultValue("false") boolean loadReservations) {
        try {
            CadeauDAO dao = getDao();

            List<Cadeau> cadeaux = Cadeau.findAll(dao, loadListeCadeau, loadReservations);

            JSONArray array = new JSONArray();
            for (Cadeau c : cadeaux) {
                array.put(toJson(c, loadListeCadeau, loadReservations));
            }

            return Response.status(Status.OK)
                    .entity(array.toString())
                    .build();

        } catch (Exception e) {
            return Response.status(Status.BAD_REQUEST)
                    .entity("Erreur lors de la récupération des cadeaux.")
                    .build();
        }
    }

    //Update
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("id") int id, String body) {
        try {
            CadeauDAO dao = getDao();

            Cadeau existing = Cadeau.findById(id, dao);
            if (existing == null) {
                return Response.status(Status.NOT_FOUND)
                        .entity("Erreur : Cadeau non trouvé.")
                        .build();
            }

            JSONObject json = new JSONObject(body);

            existing.setName(json.getString("name"));
            existing.setDescription(json.getString("description"));
            existing.setPrice(json.getDouble("price"));
            existing.setPhoto(json.getString("photo"));
            existing.setLinkSite(json.getString("linkSite"));
            existing.setPriorite(StatutPriorite.valueOf(json.getString("priorite")));

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
                    .entity("Erreur lors de la mise à jour du cadeau.")
                    .build();
        }
    }

    //Delete
    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") int id) {
        try {
            CadeauDAO dao = getDao();
            Cadeau c = Cadeau.findById(id, dao);

            if (c == null) {
                return Response.status(Status.NOT_FOUND).build();
            }

            boolean deleted = Cadeau.delete(c, dao);

            if (!deleted) {
                return Response.status(Status.BAD_REQUEST)
                        .entity("Erreur : Impossible de supprimer ce cadeau.")
                        .build();
            }

            return Response.status(Status.NO_CONTENT).build();

        } catch (Exception e) {
            return Response.status(Status.BAD_REQUEST)
                    .entity("Erreur lors de la suppression.")
                    .build();
        }
    }

    //Méthode privé
    private JSONObject toJson(Cadeau c, boolean includeListeCadeau, boolean includeReservations) {

        JSONObject json = c.unparse();

        if (includeListeCadeau) {
            json.put("listeCadeau", c.getListeCadeau().unparse());
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