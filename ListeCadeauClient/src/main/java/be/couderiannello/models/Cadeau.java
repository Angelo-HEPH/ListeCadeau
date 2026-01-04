package be.couderiannello.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import be.couderiannello.dao.CadeauDAO;
import be.couderiannello.dao.DAO;
import be.couderiannello.enumeration.StatutCadeau;
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
	private StatutCadeau statutCadeau;

	//Relations
	private ListeCadeau listeCadeau;
	private List<Reservation> reservations;
	
	//Constructor
	public Cadeau() {
		reservations = new ArrayList<Reservation>();
		this.statutCadeau = StatutCadeau.DISPONIBLE;
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
			throw new IllegalArgumentException("L'id ne peut pas être plus petit que 0.");
		}
		this.id = id;
	}
	
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		if(name == null || name.isBlank()){
			throw new IllegalArgumentException("Le nom ne peut pas être vide.");
		}
		if (name.length() > 50) {
		    throw new IllegalArgumentException("Nom trop long (max 50 caractères).");
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
			throw new IllegalArgumentException("La photo est obligatoire");
		}
		this.photo = photo;
	}
	
	
	public String getLinkSite() {
		return linkSite;
	}
	
	public void setLinkSite(String linkSite) {
		if(linkSite == null || linkSite.isBlank()) {
			throw new IllegalArgumentException("Le lien du site est obligatoire.");
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
			throw new IllegalArgumentException("Une priorité est nécessaire.");
		}
		this.priorite = priorite;
	}

	
	public StatutCadeau getStatutCadeau() {
	    return statutCadeau;
	}

	public void setStatutCadeau(StatutCadeau statutCadeau) {
	    if (statutCadeau == null) {
	        throw new IllegalArgumentException("StatutCadeau est obligatoire.");
	    }
	    this.statutCadeau = statutCadeau;
	}
	
	
	//Relations
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
	
	
    public void addReservation(Reservation reservation) {
        if (reservation == null) {
            throw new IllegalArgumentException("La réservation ne peut pas être null.");
        }
        if (reservations.contains(reservation)) {
            throw new IllegalArgumentException("La réservation existe déja.");
        }
        reservations.add(reservation);
        if (reservation.getCadeau() != this) {
            reservation.setCadeau(this);
        }
    }
	
    public void removeReservation(Reservation reservation) {
        if (reservation == null || !reservations.contains(reservation)) {
            throw new IllegalArgumentException("La réservation est introuvable.");
        }
        reservations.remove(reservation);
        if (reservation.getCadeau() == this) {
            reservation.setCadeau(null);
        }
    }
	
    //Méthode logique
    public double getTotalContributed() {
        return Math.round(
            reservations.stream().mapToDouble(Reservation::getAmount).sum() * 100.0) / 100.0;
    }

    public double getRemainingAmount() {
        double remaining = price - getTotalContributed();
        remaining = Math.round(remaining * 100.0) / 100.0;
        return Math.max(0.0, remaining);
    }


    public boolean hasContributions() {
        return getTotalContributed() > 0;
    }

    public boolean isFullyReserved() {
        return getRemainingAmount() <= 0;
    }

    private void updateStatut() {
        if (isFullyReserved()) {
            statutCadeau = StatutCadeau.RESERVER;
        } else if (hasContributions()) {
            statutCadeau = StatutCadeau.PARTICIPATION;
        } else {
            statutCadeau = StatutCadeau.DISPONIBLE;
        }
    }

    public void contribuer(Personne personne, double amount) {
        if (personne == null) {
        	throw new IllegalArgumentException("Personne obligatoire.");
        }
        if (amount <= 0) {
        	throw new IllegalArgumentException("Montant invalide.");
        }
        if (amount > getRemainingAmount()) {
            throw new IllegalArgumentException("Montant trop élevé.");
        }

        Reservation r = new Reservation();
        r.setAmount(amount);
        r.addPersonne(personne);
        r.setCadeau(this);

        reservations.add(r);
        updateStatut();
    }

    public void retirerContribution(Reservation r) {
        if (!reservations.remove(r)) {
            throw new IllegalArgumentException("Réservation introuvable.");
        }

        r.setCadeau(null);
        updateStatut();
    }

    public void ensureCanBeModifiedOrDeleted() {

        if (listeCadeau != null) {
            listeCadeau.ensureCanManageCadeaux();
        }

        if (hasContributions() || isFullyReserved()) {
            throw new IllegalStateException("Impossible de modifier ou supprimer un cadeau avec des contributions ou déjà réservé.");
        }
    }

    public void ensureCanReceiveContribution() {
        if (listeCadeau != null) {
            listeCadeau.ensureCanReceiveContributions();
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

        if (json.has("id")) {
            setId(json.getInt("id"));
        }
        
        setName(json.getString("name"));
        setDescription(json.getString("description"));
        setPrice(json.getDouble("price"));
        setPhoto(json.getString("photo"));
        setLinkSite(json.getString("linkSite"));
        setPriorite(StatutPriorite.valueOf(json.getString("priorite")));

        if (json.has("listeCadeauId")) {
            ListeCadeau l = new ListeCadeau();
            l.setId(json.getInt("listeCadeauId"));
            setListeCadeau(l);
        }
        
        if (json.has("statutCadeau") && !json.isNull("statutCadeau")) {
            setStatutCadeau(StatutCadeau.valueOf(json.getString("statutCadeau")));
        }
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
        if (getListeCadeau() != null) {
            json.put("listeCadeauId", getListeCadeau().getId());
        }

        json.put("statutCadeau", getStatutCadeau().name());

        return json;
    }
}
