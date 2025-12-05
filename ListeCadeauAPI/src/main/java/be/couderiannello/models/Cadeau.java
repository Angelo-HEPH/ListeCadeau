package be.couderiannello.models;

import java.util.ArrayList;
import java.util.List;

import be.couderiannello.enumeration.StatutCadeau;
import be.couderiannello.enumeration.StatutPriorite;

public class Cadeau {

	//Attributs
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
	}
	
	public Cadeau(int id, String name, String description, double price, String photo, String linkSite,
			StatutPriorite priorite, StatutCadeau statutCadeau) {
		this();
		setId(id);
		setName(name);
		setDescription(description);
		setPrice(price);
		setPhoto(photo);
		setLinkSite(linkSite);
		setPriorite(priorite);
		setStatutCadeau(statutCadeau);
	}
	
	//Getters - Setters
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	
	public String getPhoto() {
		return photo;
	}
	public void setPhoto(String photo) {
		this.photo = photo;
	}
	
	public String getLinkSite() {
		return linkSite;
	}
	public void setLinkSite(String linkSite) {
		this.linkSite = linkSite;
	}
	
	public StatutPriorite getPriorite() {
		return priorite;
	}
	public void setPriorite(StatutPriorite priorite) {
		this.priorite = priorite;
	}
	
	public StatutCadeau getStatutCadeau() {
		return statutCadeau;
	}
	public void setStatutCadeau(StatutCadeau statutCadeau) {
		this.statutCadeau = statutCadeau;
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
}
