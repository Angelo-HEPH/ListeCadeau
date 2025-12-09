package be.couderiannello.api;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import be.couderiannello.connection.ConnectionBdd;
import be.couderiannello.dao.PersonneDAO;
import be.couderiannello.models.Personne;

@Path("/personne")
public class PersonneAPI {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createPersonne(String personneJson) {
        try {
            JSONObject json = new JSONObject(personneJson);

            String name = json.getString("name");
            String firstName = json.getString("firstName");
            int age = json.getInt("age");
            String street = json.getString("street");
            String city = json.getString("city");
            String streetNumber = json.getString("streetNumber");
            int postalCode = json.getInt("postalCode");
            String email = json.getString("email");
            String password = json.getString("password");

            PersonneDAO dao = new PersonneDAO(ConnectionBdd.getInstance());

            Personne p = new Personne();
            p.setName(name);
            p.setFirstName(firstName);
            p.setAge(age);
            p.setStreet(street);
            p.setCity(city);
            p.setStreetNumber(streetNumber);
            p.setPostalCode(postalCode);
            p.setEmail(email);
            p.setPassword(password);

            int id = Personne.create(p, dao);
            p.setId(id);

            //Réponse JSON
            JSONObject responseJson = new JSONObject();
            responseJson.put("id", p.getId());
            responseJson.put("name", p.getName());
            responseJson.put("firstName", p.getFirstName());
            responseJson.put("email", p.getEmail());
            responseJson.put("password", p.getPassword());


            return Response
                    .status(Status.CREATED)
                    .header("Location", "personne/" + id)
                    .entity(responseJson.toString())
                    .build();

        } catch (JSONException e) {
            return Response
                    .status(Status.BAD_REQUEST)
                    .entity("JSON invalide ou champs manquants.")
                    .build();
        } catch (IllegalArgumentException e) {
            return Response
                    .status(Status.BAD_REQUEST)
                    .entity("Erreur : " + e.getMessage() + ".")
                    .build();
        } catch (Exception e) {
            return Response
                    .status(Status.BAD_REQUEST)
                    .entity("Erreur lors de la création de la personne." + e)
                    .build();
        }
    }
    
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findById(@PathParam("id") int id) {
        try {
            PersonneDAO dao = new PersonneDAO(ConnectionBdd.getInstance());

            Personne p = Personne.findById(id, dao);

            if (p == null) {
                return Response.status(Status.NOT_FOUND).build();
            }

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
            json.put("password", p.getPassword());

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
    public Response findAll() {
        try {
            PersonneDAO dao = new PersonneDAO(ConnectionBdd.getInstance());
            List<Personne> personnes = Personne.findAll(dao);

            JSONArray array = new JSONArray();

            for (Personne p : personnes) {
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
                json.put("password", p.getPassword());
                array.put(json);
            }

            return Response
                    .status(Status.OK)
                    .entity(array.toString())
                    .build();

        } catch (Exception e) {
            return Response
                    .status(Status.BAD_REQUEST)
                    .entity("Erreur lors de la récupération des personnes.")
                    .build();
        }
    }
    
    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deletePersonne(@PathParam("id") int id) {
        try {
            PersonneDAO dao = new PersonneDAO(ConnectionBdd.getInstance());
            Personne p = Personne.findById(id, dao);

            if (p == null) {
                return Response
                        .status(Status.NOT_FOUND)
                        .entity("Erreur : Personne non trouvée.")
                        .build();
            }

            boolean deleted = Personne.delete(p, dao);

            if (!deleted) {
                return Response
                        .status(Status.BAD_REQUEST)
                        .entity("Erreur : Impossible de supprimer cette personne.")
                        .build();
            }

            return Response.status(Status.OK).build();

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
            PersonneDAO dao = new PersonneDAO(ConnectionBdd.getInstance());

            Personne existing = Personne.findById(id,dao);
            if (existing == null) {
                return Response
                        .status(Status.NOT_FOUND)
                        .entity("Erreur : Personne non trouvée.")
                        .build();
            }

            JSONObject json = new JSONObject(personneJson);
            String name = json.getString("name");
            String firstName = json.getString("firstName");
            int age = json.getInt("age");
            String street = json.getString("street");
            String city = json.getString("city");
            String streetNumber = json.getString("streetNumber");
            int postalCode = json.getInt("postalCode");
            String email = json.getString("email");
            String password = json.getString("password");

            existing.setName(name);
            existing.setFirstName(firstName);
            existing.setAge(age);
            existing.setStreet(street);
            existing.setCity(city);
            existing.setStreetNumber(streetNumber);
            existing.setPostalCode(postalCode);
            existing.setEmail(email);
            existing.setPassword(password);

            boolean updated = Personne.update(existing, dao);

            if (!updated) {
                return Response
                        .status(Status.BAD_REQUEST)
                        .entity("Erreur : La mise à jour a échoué.")
                        .build();
            }

            JSONObject responseJson = new JSONObject();
            responseJson.put("id", existing.getId());
            responseJson.put("name", existing.getName());
            responseJson.put("firstName", existing.getFirstName());
            responseJson.put("age", existing.getAge());
            responseJson.put("street", existing.getStreet());
            responseJson.put("city", existing.getCity());
            responseJson.put("streetNumber", existing.getStreetNumber());
            responseJson.put("postalCode", existing.getPostalCode());
            responseJson.put("email", existing.getEmail());

            return Response
                    .status(Status.OK)
                    .entity(responseJson.toString())
                    .build();

        } catch (JSONException e) {
            return Response
                    .status(Status.BAD_REQUEST)
                    .entity("Erreur : JSON invalide.")
                    .build();
        } catch (IllegalArgumentException e) {
            return Response
                    .status(Status.BAD_REQUEST)
                    .entity("Erreur : " + e.getMessage() + ".")
                    .build();
        } catch (Exception e) {
            return Response
                    .status(Status.BAD_REQUEST)
                    .entity("Erreur lors de la mise à jour de la personne.")
                    .build();
        }
    }
    
    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(String loginJson) {
        try {
            JSONObject json = new JSONObject(loginJson);

            String email = json.getString("email");
            String password = json.getString("password"); 

            PersonneDAO dao = new PersonneDAO(ConnectionBdd.getInstance());
            Personne p = Personne.authenticate(email, password,dao);

            if (p == null) {
                return Response
                        .status(Status.UNAUTHORIZED)
                        .entity("Email ou mot de passe incorrect.")
                        .build();
            }

            JSONObject responseJson = new JSONObject();
            responseJson.put("id", p.getId());
            responseJson.put("name", p.getName());
            responseJson.put("firstName", p.getFirstName());
            responseJson.put("email", p.getEmail());

            return Response
                    .status(Status.OK)
                    .entity(responseJson.toString())
                    .build();

        } catch (JSONException e) {
            return Response
                    .status(Status.BAD_REQUEST)
                    .entity("JSON invalide.")
                    .build();
        } catch (Exception e) {
            return Response
                    .status(Status.BAD_REQUEST)
                    .entity("Erreur lors de la connexion.")
                    .build();
        }
    }

}
