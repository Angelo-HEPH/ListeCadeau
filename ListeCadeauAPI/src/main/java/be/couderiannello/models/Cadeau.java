package be.couderiannello.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import be.couderiannello.dao.CadeauDAO;
import be.couderiannello.dao.DAO;
import be.couderiannello.enumeration.StatutPriorite;

public class Cadeau implements Serializable {

	//Attributs
	private static final long serialVersionUID = 1L;

	private int id;
	private String name;
	private String description;
	private double price;
	private String photo;
	private String linkSite;
	private StatutPriorite priorite;
	
	//Relations
	private ListeCadeau listeCadeau;
	private List<Reservation> reservations;
	
	//Constructor
	public Cadeau() {
		reservations = new ArrayList<Reservation>();
	}
	
	public Cadeau(int id, String name, String description, double price, String photo,String linkSite,
			StatutPriorite priorite, ListeCadeau listeCadeau) {
		this();
		setId(id);
		setName(name);
		setDescription(description);
		setPrice(price);
		setPhoto(photo);
		setLinkSite(linkSite);
		setPriorite(priorite);
		setListeCadeau(listeCadeau);
	}
	
	//Getters - Setters
	public int getId() {
		return id;
	}
	public void setId(int id) {
		if(id < 0) {
			throw new IllegalArgumentException("L'id ne peut pas etre plus petit que 0.");
		}
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		if(name == null || name.isBlank()){
			throw new IllegalArgumentException("Name ne peut pas être vide.");
		}
		if (name.length() > 50) {
		    throw new IllegalArgumentException("Name trop long (max 50 caractères).");
		}
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		if(description == null || description.isBlank()) {
			throw new IllegalArgumentException("La description ne peut pas etre vide.");
		}
		this.description = description;
	}
	
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		if(price <= 0) {
			throw new IllegalArgumentException("Le prix ne peut pas etre plus petit ou égale à 0.");
		}
		this.price = price;
	}
	
	public String getPhoto() {
		return photo;
	}
	public void setPhoto(String photo) {
		if(photo == null || photo.isBlank()) {
			throw new IllegalArgumentException("La photo ne peut pas etre vide.");
		}
		this.photo = photo;
	}
	
	public String getLinkSite() {
		return linkSite;
	}
	public void setLinkSite(String linkSite) {
		if(linkSite == null || linkSite.isBlank()) {
			throw new IllegalArgumentException("Le lien du site ne peut pas etre vide.");
		}
		if (!linkSite.startsWith("http://") && !linkSite.startsWith("https://")) {
		    throw new IllegalArgumentException("Le lien doit commencer par http:// ou https://");
		}
		this.linkSite = linkSite;
	}
	
	public StatutPriorite getPriorite() {
		return priorite;
	}
	public void setPriorite(StatutPriorite priorite) {
		if(priorite == null) {
			throw new IllegalArgumentException("La priorite du cadeau ne peut pas etre null..");
		}
		this.priorite = priorite;
	}

	//Getters - Setters - Relation
	public ListeCadeau getListeCadeau() {
		return listeCadeau;
	}
    public void setListeCadeau(ListeCadeau listeCadeau) {
        if (this.listeCadeau == listeCadeau) {
            return;
        }

        ListeCadeau old = this.listeCadeau;
        this.listeCadeau = listeCadeau;

        if (old != null) {
            old.getCadeaux().remove(this);
        }
        if (listeCadeau != null && !listeCadeau.getCadeaux().contains(this)) {
            listeCadeau.getCadeaux().add(this);
        }
    }

    public List<Reservation> getReservations() {
        return reservations;
    }
    
	public void setReservations(List<Reservation> reservations) {
		if (reservations == null) {
			throw new IllegalArgumentException("Liste de réservations ne peut pas être null.");
		}
		
		this.reservations = reservations;
	}
	
	//Méthodes
	public void addReservation(Reservation r) {
		if (r == null) {
			throw new IllegalArgumentException("Réservation ne peut pas être null.");
		}
		
		if (reservations.contains(r)) {
			throw new IllegalArgumentException("Cette réservation est déjà associée à ce cadeau.");
		}
		
		reservations.add(r);
        if (r.getCadeau() != this) {
            r.setCadeau(this);
        }
	}
	
	public void removeReservation(Reservation r) {
		if (r == null) {
			throw new IllegalArgumentException("Réservation ne peut pas être null.");
		}
		
		if (!reservations.contains(r)) {
			throw new IllegalArgumentException("Cette réservation n’est pas associée à ce cadeau.");
		}
		
		reservations.remove(r);
        if (r.getCadeau() == this) {
            r.setCadeau(null);
        }
	}
	
	//ToString - hashCode - Equals
	@Override
	public String toString() {
	    return "Cadeau{" +
	            "id=" + id +
	            ", name='" + name + '\'' +
	            ", description='" + description + '\'' +
	            ", price=" + price +
	            ", photo='" + photo + '\'' +
	            ", linkSite='" + linkSite + '\'' +
	            ", priorite=" + priorite +
	            ", listeCadeauId=" + (listeCadeau != null ? listeCadeau.getId() : "null") +
	            ", reservationsCount=" + (reservations != null ? reservations.size() : 0) +
	            '}';
	}

	@Override
	public int hashCode() {
	    return toString().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
	    if (obj == null || obj.getClass() != this.getClass()) {
	        return false;
	    }

	    Cadeau c = (Cadeau) obj;

	    return c.getId() == this.getId() &&
	           c.getName().equals(this.getName()) &&
	           c.getDescription().equals(this.getDescription()) &&
	           c.getPrice() == this.getPrice() &&
	           c.getPhoto().equals(this.getPhoto()) &&
	           c.getLinkSite().equals(this.getLinkSite()) &&
	           c.getPriorite() == this.getPriorite();
	}
	
	
	//DAO
    public int create(DAO<Cadeau> dao) {
        int id = dao.create(this);
        this.setId(id);
        return id;
    }
    
    public static Cadeau findById(int id, DAO<Cadeau> dao) {
    	return dao.find(id);
    }
    
    public static Cadeau findById(int id, CadeauDAO dao, boolean loadListeCadeau, boolean loadReservations) {
    	return dao.find(id, loadListeCadeau, loadReservations);
    }
    
    public static List<Cadeau> findAll(DAO<Cadeau> dao){
    	return dao.findAll();
    }
    
    public static List<Cadeau> findAll(CadeauDAO dao, boolean loadListeCadeau, boolean loadReservations){
    	return dao.findAll(loadListeCadeau, loadReservations);
    }
    
    public static boolean delete(Cadeau d, DAO<Cadeau> dao) {
    	return dao.delete(d);
    }
    
    public boolean update(DAO<Cadeau> dao) {
    	return dao.update(this);
    }
    
    //JSON -> Model
    public void parse(JSONObject json) {

        setName(json.getString("name"));
        setDescription(json.getString("description"));
        setPrice(json.getDouble("price"));
        setPhoto(json.getString("photo"));
        setLinkSite(json.getString("linkSite"));
        setPriorite(StatutPriorite.valueOf(json.getString("priorite")));

        ListeCadeau l = new ListeCadeau();
        l.setId(json.getInt("listeCadeauId"));
        setListeCadeau(l);
    }

    //Model -> JSON
    public JSONObject unparse() {

        JSONObject json = new JSONObject();

        json.put("id", getId());
        json.put("name", getName());
        json.put("description", getDescription());
        json.put("price", getPrice());
        json.put("photo", getPhoto());
        json.put("linkSite", getLinkSite());
        json.put("priorite", getPriorite().name());
        json.put("listeCadeauId", getListeCadeau().getId());

        return json;
    }
}
