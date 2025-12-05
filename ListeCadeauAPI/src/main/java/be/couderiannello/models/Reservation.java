package be.couderiannello.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Reservation {
	
	//Attributs
	private int id;
	private double amount;
	private LocalDate dateReservation;
	
	//Relation
	private Cadeau cadeau;
	private List<Personne> personnes;
	
	//Constructeur
	public Reservation() {
		personnes = new ArrayList<Personne>();
	}
	
	public Reservation(int id, double amount, LocalDate dateReservation, Cadeau cadeau, Personne personne) {
		this();
		setId(id);
		setAmount(amount);
		setDateReservation(dateReservation);
		
        if (cadeau == null) {
            throw new IllegalArgumentException("Cadeau ne peut pas être null.");
        }
        
        if (personne == null) {
            throw new IllegalArgumentException("Personne ne peut pas être null.");
        }

        setCadeau(cadeau);
        addPersonne(personne);
	}
	
	//Getters - Setters
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	
	public LocalDate getDateReservation() {
		return dateReservation;
	}
	public void setDateReservation(LocalDate dateReservation) {
		this.dateReservation = dateReservation;
	}

	//Getters - Setters - Relations
	public Cadeau getCadeau() {
		return cadeau;
	}

    public void setCadeau(Cadeau cadeau) {
        if (this.cadeau == cadeau) {
            return;
        }

        Cadeau old = this.cadeau;
        this.cadeau = cadeau;

        if (old != null) {
            old.getReservations().remove(this);
        }
        if (cadeau != null && !cadeau.getReservations().contains(this)) {
            cadeau.getReservations().add(this);
        }
    }

	public List<Personne> getPersonnes() {
		return personnes;
	}

	public void setPersonnes(List<Personne> personnes) {
		if (personnes == null) {
			throw new IllegalArgumentException("La liste de personnes ne peut pas être null.");
		}
		
		this.personnes = personnes;
	}
	
	//Méthodes
	public void addPersonne(Personne p) {
		if (p == null) {
			throw new IllegalArgumentException("Personne ne peut pas être null.");
		}
		
		if (personnes.contains(p)) {
			throw new IllegalArgumentException("Cette personne participe déjà à la réservation.");
		}
		
		personnes.add(p);
        if (!p.getReservations().contains(this)) {
            p.getReservations().add(this);
        }
	}
	
	public void removePersonne(Personne p) {
		if (p == null) {
			throw new IllegalArgumentException("Personne ne peut pas être null.");
		}
		
		if (!personnes.contains(p)) {
			throw new IllegalArgumentException("Cette personne n’est pas liée à cette réservation.");
		}
		
		personnes.remove(p);
        if (p.getReservations().contains(this)) {
            p.getReservations().remove(this);
        }
	}
}
