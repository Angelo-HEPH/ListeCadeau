package be.couderiannello.models;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import be.couderiannello.dao.DAO;
import be.couderiannello.dao.ReservationDAO;

public class Reservation implements Serializable {

	//Attributs
	private static final long serialVersionUID = 1L;

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
	    this.dateReservation = LocalDate.now();
		
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
		if(id < 0) {
            throw new IllegalArgumentException("L'id ne peut pas être plus petit que 0.");
		}
		
		this.id = id;
	}
	
	public double getAmount() {
		return amount;
	}
	
	public void setAmount(double amount) {
		if(amount <= 0) {
            throw new IllegalArgumentException("Le montant doit être supérieur à 0.");
		}
		
		this.amount = amount;
	}
	
	public LocalDate getDateReservation() {
		return dateReservation;
	}
	
	public void setDateReservation(LocalDate dateReservation) {
		if(dateReservation == null) {
            throw new IllegalArgumentException("La date de réservation ne peut pas être null.");
		}
		
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
	
	// ToString – HashCode – Equals 

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", amount=" + amount +
                ", dateReservation=" + dateReservation +
                ", cadeauId=" + (cadeau != null ? cadeau.getId() : null) +
                ", personnesCount=" + personnes.size() +
                '}';
    }

    @Override
    public int hashCode() {
        return toString().hashCode(); 
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null || obj.getClass() != this.getClass())
            return false;

        Reservation r = (Reservation) obj;

        return r.getId() == this.id &&
               r.getAmount() == this.amount &&
               r.getDateReservation().equals(this.dateReservation) &&
               ((r.getCadeau() == null && this.cadeau == null) ||
                (r.getCadeau() != null && this.cadeau != null &&
                 r.getCadeau().getId() == this.cadeau.getId()));
    }
    
    //DAO
    public int create(DAO<Reservation> dao) {
        int id = dao.create(this);
        this.setId(id);
        return id;
    }
    
    public static Reservation findById(int id, DAO<Reservation> dao) {
    	return dao.find(id);
    }
    
    public static Reservation findById(int id, ReservationDAO dao, boolean loadCadeau, boolean loadPersonnes) {
    	return dao.find(id, loadCadeau, loadPersonnes);
    }
    
    public static List<Reservation> findAll(DAO<Reservation> dao){
    	return dao.findAll();
    }
    
    public static List<Reservation> findAll(ReservationDAO dao, boolean loadCadeau, boolean loadPersonnes){
    	return dao.findAll(loadCadeau, loadPersonnes);
    }
	
    public static boolean delete(Reservation r, DAO<Reservation> dao) {
    	return dao.delete(r);
    }
    
    public boolean update(DAO<Reservation> dao) {
    	return dao.update(this);
    }
}
