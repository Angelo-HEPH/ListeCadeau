package be.couderiannello.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONException;
import org.json.JSONObject;

import be.couderiannello.connection.ConnectionBdd;
import be.couderiannello.dao.PersonneDAO;
import be.couderiannello.models.Personne;

@Path("/login")
public class LoginAPI {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(String body) {
        try {
            JSONObject json = new JSONObject(body);
            String email = json.optString("email", "").trim();
            String password = json.optString("password", "");

            if (email.isBlank() || password.isBlank()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Erreur : Email et mot de passe requis.")
                        .build();
            }

            PersonneDAO dao = new PersonneDAO(ConnectionBdd.getInstance());
            Personne p = Personne.authenticate(email, password, dao);

            if (p == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("Email ou mot de passe incorrect.")
                        .build();
            }

            JSONObject responseJson = new JSONObject();
            responseJson.put("id", p.getId());
            responseJson.put("name", p.getName());
            responseJson.put("firstName", p.getFirstName());
            responseJson.put("email", p.getEmail());

            return Response.status(Response.Status.CREATED)
                    .entity(responseJson.toString())
                    .build();

        } catch (JSONException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Erreur : Requête invalide.")
                    .build();

        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Erreur : " + e.getMessage())
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erreur : Erreur interne.")
                    .build();
        }
    }
}
