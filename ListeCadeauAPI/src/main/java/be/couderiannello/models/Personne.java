package be.couderiannello.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import be.couderiannello.dao.PersonneDAO;

public class Personne implements Serializable {

	//Attributs
	private static final long serialVersionUID = 1L;

	private int id;
	private String name;
	private String firstName;
	private int age;
	private String street;
	private String city;
	private String streetNumber;
	private int postalCode;
	private String email;
	private String password;
	
	//Relations
	private List<Reservation> reservations;
	private List<Notification> notifications;
	private List<ListeCadeau> listeCadeauInvitations;
	private List<ListeCadeau> listeCadeauCreator;
	
	//Constructeurs
	public Personne() {
        reservations = new ArrayList<>();
        notifications = new ArrayList<>();
        listeCadeauInvitations = new ArrayList<>();
        listeCadeauCreator = new ArrayList<>();
	}
	
	public Personne(int id, String name, String firstName, int age, String street, String city, String streetNumber,
			int postalCode, String email, String password) {
		this();
		setId(id);
		setName(name);
		setFirstName(firstName);
		setAge(age);
		setStreet(street);
		setCity(city);
		setStreetNumber(streetNumber);
		setPostalCode(postalCode);
		setEmail(email);
		setPassword(password);
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
		if(name == null || name.isBlank()) {
            throw new IllegalArgumentException("Le nom ne peut pas être vide.");
		}
		if (name.length() > 75) {
		    throw new IllegalArgumentException("Name trop long (max 75 caractères).");
		}
		
		this.name = name;
	}
	
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		if(firstName == null || firstName.isBlank()) {
			throw new IllegalArgumentException("Le prénom ne peut pas être vide.");
		}
		if (firstName.length() > 75) {
			throw new IllegalArgumentException("FirstName trop long (max 75 caractères).");
		}
		 
		this.firstName = firstName;
	}
	
	public int getAge() {
		return age;
	}
	
	public void setAge(int age) {
		if(age < 0 || age > 120) {
            throw new IllegalArgumentException("L'âge est invalide.");
		}
		
		this.age = age;
	}
	
	public String getStreet() {
		return street;
	}
	
	public void setStreet(String street) {
		if(street == null || street.isBlank()) {
			throw new IllegalArgumentException("La rue ne peut pas être vide.");
		}
		if (street.length() > 150) {
			throw new IllegalArgumentException("Le nom de rue est trop long (max 150 caractères).");
		}
		 
		this.street = street;
	}
	
	public String getCity() {
		return city;
	}
	
	public void setCity(String city) {
		if(city == null || street.isBlank()) {
            throw new IllegalArgumentException("La ville ne peut pas être vide.");
		}
		if (city.length() > 100) {
			throw new IllegalArgumentException("La ville est trop long (max 100 caractères).");
		}
		
		this.city = city;
	}
	
	public String getStreetNumber() {
		return streetNumber;
	}
	
	public void setStreetNumber(String streetNumber) {
		if(streetNumber == null || streetNumber.isBlank()) {
            throw new IllegalArgumentException("Le numéro de rue ne peut pas être vide.");
		}
		if (streetNumber.length() > 20) {
			throw new IllegalArgumentException("Le numéro de rue est trop long (max 20 caractères).");
		}
		
		this.streetNumber = streetNumber;
	}
	
	public int getPostalCode() {
		return postalCode;
	}
	
	public void setPostalCode(int postalCode) {
		if(postalCode <= 0) {
            throw new IllegalArgumentException("Le code postal est invalide.");
		}
		
		this.postalCode = postalCode;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		if(email == null || email.isBlank()) {
            throw new IllegalArgumentException("L'email ne peut pas être vide.");
		}
        if(!email.contains("@") || !email.contains(".")) {
            throw new IllegalArgumentException("L'email n'est pas valide.");
        }
        
		this.email = email;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		if(password == null || password.isBlank()) {
            throw new IllegalArgumentException("Le mot de passe ne peut pas être vide.");
		}
        if(password.length() < 6) {
            throw new IllegalArgumentException("Le mot de passe doit contenir au moins 6 caractères.");
        }
        
		this.password = password;
	}
	
	//Getters - Setters - Relations
	public List<Reservation> getReservations() {
		return reservations;
	}

	public void setReservations(List<Reservation> reservations) {
		 if(reservations == null) {
			 throw new IllegalArgumentException("La liste des réservations ne peut pas être null.");
		 }
		 
		this.reservations = reservations;
	}

	public List<Notification> getNotifications() {
		return notifications;
	}

	public void setNotifications(List<Notification> notifications) {
		if(notifications == null) {
            throw new IllegalArgumentException("La liste des notifications ne peut pas être null.");
		}
		
		this.notifications = notifications;
	}

	public List<ListeCadeau> getListeCadeauInvitations() {
		return listeCadeauInvitations;
	}

	public void setListeCadeauInvitations(List<ListeCadeau> listeCadeauInvitations) {
		if(listeCadeauInvitations == null) {
            throw new IllegalArgumentException("La liste des invitations ne peut pas être null.");
		}
		
		this.listeCadeauInvitations = listeCadeauInvitations;
	}

	public List<ListeCadeau> getListeCadeauCreator() {
		return listeCadeauCreator;
	}

	public void setListeCadeauCreator(List<ListeCadeau> listeCadeauCreator) {
		 if(listeCadeauCreator == null) {
	            throw new IllegalArgumentException("La liste des créations ne peut pas être null.");
		 }
		 
		this.listeCadeauCreator = listeCadeauCreator;
	}
	
	//Méthodes
	public void addReservation(Reservation r) {
        if (r == null) {
            throw new IllegalArgumentException("La reservation ne peut pas être null.");
        }
        
        if (reservations.contains(r)) {
            throw new IllegalArgumentException("Cette réservation est déjà associée à cette personne.");
        }
        
        reservations.add(r);
        if (!r.getPersonnes().contains(this)) {
            r.getPersonnes().add(this);
        }
    }

    public void removeReservation(Reservation r) {
        if (r == null)
            throw new IllegalArgumentException("Reservation ne peut pas être null.");
        if (!reservations.contains(r))
            throw new IllegalArgumentException("Cette réservation n’est pas associée à cette personne.");
        
        reservations.remove(r);
        if (r.getPersonnes().contains(this)) {
            r.getPersonnes().remove(this);
        }
    }

    public void addNotification(Notification n) {
        if (n == null)
            throw new IllegalArgumentException("Notification ne peut pas être null.");
        if (notifications.contains(n))
            throw new IllegalArgumentException("Cette notification est déjà associée à cette personne.");
        
        notifications.add(n);
    }

    public void removeNotification(Notification n) {
        if (n == null)
            throw new IllegalArgumentException("Notification ne peut pas être null.");
        if (!notifications.contains(n))
            throw new IllegalArgumentException("Cette notification n’est pas associée à cette personne.");
        
        notifications.remove(n);
    }

    public void addListeCadeauInvitation(ListeCadeau l) {
        if (l == null)
            throw new IllegalArgumentException("Liste cadeau ne peut pas être null.");
        if (listeCadeauInvitations.contains(l))
            throw new IllegalArgumentException("Cette liste de cadeau est déjà en invitation.");
        
        listeCadeauInvitations.add(l);
        if (!l.getInvites().contains(this)) {
            l.getInvites().add(this);
        }
    }

    public void removeListeCadeauInvitation(ListeCadeau l) {
        if (l == null) {
            throw new IllegalArgumentException("Liste cadeau ne peut pas être null.");
        }
        
        if (!listeCadeauInvitations.contains(l)) {
            throw new IllegalArgumentException("Cette liste n’est pas dans les invitations.");
        }
        
        listeCadeauInvitations.remove(l);
        if (l.getInvites().contains(this)) {
            l.getInvites().remove(this);
        }
    }

    public void addListeCadeauCreator(ListeCadeau l) {
        if (l == null) {
            throw new IllegalArgumentException("Liste cadeau ne peut pas être null.");
        }
        
        if (listeCadeauCreator.contains(l)) {
            throw new IllegalArgumentException("Cette liste de cadeaux est déjà créée par cette personne.");
        }
        
        listeCadeauCreator.add(l);
        if (l.getCreator() != this) {
            l.setCreator(this);
        }
    }

    public void removeListeCadeauCreator(ListeCadeau l) {
        if (l == null) {
            throw new IllegalArgumentException("Liste cadeau ne peut pas être null.");
        }
        
        if (!listeCadeauCreator.contains(l)) {
            throw new IllegalArgumentException("Cette liste de cadeaux n’est pas créée par cette personne.");
        }
        
        listeCadeauCreator.remove(l);
        if (l.getCreator() == this) {
            l.setCreator(null);
        }
    }
    
    // ToString - Equals - HashCode 
    @Override
    public String toString() {
        return "Personne{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", firstName='" + firstName + '\'' +
                ", age=" + age +
                ", street='" + street + '\'' +
                ", city='" + city + '\'' +
                ", streetNumber='" + streetNumber + '\'' +
                ", postalCode=" + postalCode +
                ", email='" + email + '\'' +
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

        Personne p = (Personne) obj;

        return p.getId() == this.id &&
               p.getName().equals(this.name) &&
               p.getFirstName().equals(this.firstName) &&
               p.getAge() == this.age &&
               p.getStreet().equals(this.street) &&
               p.getCity().equals(this.city) &&
               p.getStreetNumber().equals(this.streetNumber) &&
               p.getPostalCode() == this.postalCode &&
               p.getEmail().equals(this.email);
    }
    
    
    //DAO
    public static int create(Personne p, PersonneDAO dao) {
        int id = dao.create(p);
        p.setId(id);
        return id;
    }
    
    public static Personne findById(int id, PersonneDAO dao) {
    	return dao.find(id);
    }
    
    public static List<Personne> findAll(PersonneDAO dao){
    	return dao.findAll();
    }
    
    public static boolean delete(Personne p, PersonneDAO dao) {
    	return dao.delete(p);
    }
    
    public static boolean update(Personne p, PersonneDAO dao) {
    	return dao.update(p);
    }
}
