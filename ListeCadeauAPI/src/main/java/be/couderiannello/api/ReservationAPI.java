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

            Reservation r = new Reservation();
            fillReservationFromJson(r, json);


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
            return Response.status(Status.BAD_REQUEST)
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
            ReservationDAO dao = getDao();

            Reservation r = Reservation.findById(id, dao, loadCadeau, loadPersonnes);
            if (r == null) {
                return Response.status(Status.NOT_FOUND).build();
            }

            JSONObject json = toJson(r, loadCadeau, loadPersonnes);

            return Response.status(Status.OK)
                    .entity(json.toString())
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Status.BAD_REQUEST)
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
            ReservationDAO dao = getDao();

            List<Reservation> reservations = Reservation.findAll(dao, loadCadeau, loadPersonnes);

            JSONArray array = new JSONArray();
            for (Reservation r : reservations) {
                array.put(toJson(r, loadCadeau, loadPersonnes));
            }

            return Response.status(Status.OK)
                    .entity(array.toString())
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Status.BAD_REQUEST)
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
            ReservationDAO dao = getDao();

            Reservation existing = Reservation.findById(id, dao, true, true);
            if (existing == null) {
                return Response.status(Status.NOT_FOUND)
                        .entity("Erreur : Réservation non trouvée.")
                        .build();
            }

            JSONObject json = new JSONObject(body);

            if (json.has("dateReservation")) {
                return Response.status(Status.BAD_REQUEST)
                        .entity("Erreur : La date de réservation ne peut pas être modifiée.")
                        .build();
            }

            if (json.has("personneId")) {
                return Response.status(Status.BAD_REQUEST)
                        .entity("Erreur : Les personnes d'une réservation ne peuvent pas être modifiées. Supprimez la réservation pour retirer la personne.")
                        .build();
            }

            if (json.has("amount")) {
                existing.setAmount(json.getDouble("amount"));
            }

            if (json.has("cadeauId")) {
                Cadeau c = new Cadeau();
                c.setId(json.getInt("cadeauId"));
                existing.setCadeau(c);
            }

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
                    .entity("Erreur lors de la mise à jour de la réservation.")
                    .build();
        }
    }

    //Delete
    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") int id) {
        try {
            ReservationDAO dao = getDao();
            Reservation r = Reservation.findById(id, dao);

            if (r == null) {
                return Response.status(Status.NOT_FOUND).build();
            }

            boolean deleted = Reservation.delete(r, dao);

            if (!deleted) {
                return Response.status(Status.BAD_REQUEST)
                        .entity("Erreur : Impossible de supprimer cette réservation.")
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

    //Méthode privé
    private void fillReservationFromJson(Reservation r, JSONObject json) {

        if (!json.has("cadeauId") || json.isNull("cadeauId")) {
            throw new IllegalArgumentException("cadeauId est obligatoire.");
        }

        if (!json.has("personneId") || json.isNull("personneId")) {
            throw new IllegalArgumentException("personneId est obligatoire.");
        }

        r.setAmount(json.getDouble("amount"));

        Cadeau c = new Cadeau();
        c.setId(json.getInt("cadeauId"));
        r.setCadeau(c);

        Personne p = new Personne();
        p.setId(json.getInt("personneId"));

        r.getPersonnes().clear();
        r.addPersonne(p);
    }

    private JSONObject toJson(Reservation r, boolean includeCadeau, boolean includePersonnes) {

        JSONObject json = new JSONObject();

        json.put("id", r.getId());
        json.put("amount", r.getAmount());

        if (r.getDateReservation() != null) {
            json.put("dateReservation", r.getDateReservation().toString());
        }

        if (r.getCadeau() != null) {
            json.put("cadeauId", r.getCadeau().getId());
        }

        if (includeCadeau && r.getCadeau() != null) {
            json.put("cadeau", toJsonCadeau(r.getCadeau()));
        }

        if (includePersonnes) {
            JSONArray array = new JSONArray();
            for (Personne p : r.getPersonnes()) {
                array.put(toJsonPersonne(p));
            }
            json.put("personnes", array);
        }

        return json;
    }

    private JSONObject toJsonCadeau(Cadeau c) {

        JSONObject json = new JSONObject();

        json.put("id", c.getId());
        json.put("name", c.getName());
        json.put("description", c.getDescription());
        json.put("price", c.getPrice());
        json.put("photo", c.getPhoto());
        json.put("linkSite", c.getLinkSite());

        if (c.getPriorite() != null) {
            json.put("priorite", c.getPriorite().name());
        }

        return json;
    }

    private JSONObject toJsonPersonne(Personne p) {

        JSONObject json = new JSONObject();

        json.put("id", p.getId());
        json.put("name", p.getName());
        json.put("firstName", p.getFirstName());
        json.put("email", p.getEmail());
        json.put("age", p.getAge());
        json.put("street", p.getStreet());
        json.put("city", p.getCity());
        json.put("streetNumber", p.getStreetNumber());
        json.put("postalCode", p.getPostalCode());

        return json;
    }

}
