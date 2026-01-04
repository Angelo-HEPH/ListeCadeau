package be.couderiannello.api;

import java.net.URI;
import java.util.List;

import javax.ws.rs.*;
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
            json.remove("id");

            ListeCadeau l = new ListeCadeau();
            l.parse(json);

            int id = l.create(getDao());

            return Response.status(Status.CREATED)
                    .location(URI.create("listeCadeau/" + id))
                    .build();

        } catch (JSONException e) {
            return Response.status(Status.BAD_REQUEST)
                    .entity("Erreur : JSON invalide ou champs manquants.")
                    .build();
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.status(Status.BAD_REQUEST)
                    .entity("Erreur : " + e.getMessage())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Erreur : Erreur lors de la création de la liste.")
                    .build();
        }
    }

    //Add invite
    @POST
    @Path("/{id}/invites")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addInvite(@PathParam("id") int listeId, String body) {
        try {
            ListeCadeau l = ListeCadeau.findById(listeId, getDao(), true, true, false);
            if (l == null) {
                return Response.status(Status.NOT_FOUND)
                        .entity("Erreur : Liste introuvable.")
                        .build();
            }

            JSONObject json = new JSONObject(body);
            int personneId = json.getInt("personneId");

            if (l.getCreator() != null && l.getCreator().getId() == personneId) {
                return Response.status(Status.BAD_REQUEST)
                        .entity("Erreur : Le créateur ne peut pas s'inviter lui-même.")
                        .build();
            }

            if (l.getInvites() != null && l.getInvites().stream().anyMatch(x -> x.getId() == personneId)) {
                return Response.status(Status.CONFLICT)
                        .entity("Erreur : Cette personne est déjà invitée.")
                        .build();
            }

            Personne p = new Personne();
            p.setId(personneId);

            l.addInvite(p, getDao());

            return Response.status(Status.CREATED).build();

        } catch (JSONException e) {
            return Response.status(Status.BAD_REQUEST)
                    .entity("Erreur : JSON invalide ou champs manquants.")
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Status.BAD_REQUEST)
                    .entity("Erreur : " + e.getMessage())
                    .build();
        } catch (IllegalStateException e) {
            return Response.status(Status.BAD_REQUEST)
                    .entity("Erreur : " + e.getMessage())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Erreur : Erreur lors de l'invitation.")
                    .build();
        }
    }

    //Remove invite
    @DELETE
    @Path("/{id}/invites/{personneId}")
    public Response removeInvite(@PathParam("id") int listeId,
                                 @PathParam("personneId") int personneId) {
        try {
            ListeCadeau l = ListeCadeau.findById(listeId, getDao(), false, false, false);
            if (l == null) {
                return Response.status(Status.NOT_FOUND)
                        .entity("Erreur : Liste introuvable.")
                        .build();
            }

            l.removeInvite(personneId, getDao());

            return Response.status(Status.NO_CONTENT).build();

        } catch (IllegalStateException e) {
            return Response.status(Status.NOT_FOUND)
                    .entity("Erreur : " + e.getMessage())
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Status.BAD_REQUEST)
                    .entity("Erreur : " + e.getMessage())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Erreur : Erreur lors de la suppression de l'invitation.")
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
            ListeCadeau l = ListeCadeau.findById(id, getDao(), loadCreator, loadInvites, loadCadeaux);
            if (l == null) {
                return Response.status(Status.NOT_FOUND).build();
            }

            JSONObject json = toJson(l, loadCreator, loadInvites, loadCadeaux);

            return Response.status(Status.OK)
                    .entity(json.toString())
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Erreur : Erreur lors de la récupération de la liste.")
                    .build();
        }
    }

    //FindAll
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findAll(@QueryParam("loadCreator") @DefaultValue("false") boolean loadCreator,
                            @QueryParam("loadInvites") @DefaultValue("false") boolean loadInvites,
                            @QueryParam("loadCadeaux") @DefaultValue("false") boolean loadCadeaux,
                            @QueryParam("invitedPersonneId") Integer invitedPersonneId) {
        try {
            List<ListeCadeau> listes;

            if (invitedPersonneId != null) {
                listes = ListeCadeau.getInvitedByPersonneId(
                        invitedPersonneId.intValue(),
                        getDao(),
                        loadCreator,
                        loadInvites,
                        loadCadeaux
                );
            } else {
                listes = ListeCadeau.findAll(getDao(), loadCreator, loadInvites, loadCadeaux);
            }

            JSONArray array = new JSONArray();
            for (ListeCadeau l : listes) {
                array.put(toJson(l, loadCreator, loadInvites, loadCadeaux));
            }

            return Response.status(Status.OK)
                    .entity(array.toString())
                    .build();

        } catch (IllegalArgumentException e) {
            return Response.status(Status.BAD_REQUEST)
                    .entity("Erreur : " + e.getMessage())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Erreur : Erreur lors de la récupération des listes.")
                    .build();
        }
    }

    //Update
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("id") int id, String body) {
        try {
            ListeCadeau existing = ListeCadeau.findById(id, getDao());
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

            json.put("id", id);
            json.remove("creatorId");

            existing.parse(json);

            boolean updated = existing.update(getDao());
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
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.status(Status.BAD_REQUEST)
                    .entity("Erreur : " + e.getMessage())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Erreur : Erreur lors de la mise à jour de la liste.")
                    .build();
        }
    }

    //Delete
    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") int id) {
        try {
            ListeCadeau l = ListeCadeau.findById(id, getDao());
            if (l == null) {
                return Response.status(Status.NOT_FOUND).build();
            }

            boolean deleted = ListeCadeau.delete(l, getDao());
            if (!deleted) {
                return Response.status(Status.BAD_REQUEST)
                        .entity("Erreur : Impossible de supprimer cette liste.")
                        .build();
            }

            return Response.status(Status.NO_CONTENT).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Erreur : Erreur lors de la suppression.")
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
