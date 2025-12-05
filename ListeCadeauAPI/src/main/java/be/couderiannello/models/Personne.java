package be.couderiannello.models;

import java.util.ArrayList;
import java.util.List;

public class Personne {

	//Attributs
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
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	
	public String getStreet() {
		return street;
	}
	public void setStreet(String street) {
		this.street = street;
	}
	
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	
	public String getStreetNumber() {
		return streetNumber;
	}
	public void setStreetNumber(String streetNumber) {
		this.streetNumber = streetNumber;
	}
	
	public int getPostalCode() {
		return postalCode;
	}
	public void setPostalCode(int postalCode) {
		this.postalCode = postalCode;
	}
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	//Getters - Setters - Relations
	public List<Reservation> getReservations() {
		return reservations;
	}

	public void setReservations(List<Reservation> reservations) {
		this.reservations = reservations;
	}

	public List<Notification> getNotifications() {
		return notifications;
	}

	public void setNotifications(List<Notification> notifications) {
		this.notifications = notifications;
	}

	public List<ListeCadeau> getListeCadeauInvitations() {
		return listeCadeauInvitations;
	}

	public void setListeCadeauInvitations(List<ListeCadeau> listeCadeauInvitations) {
		this.listeCadeauInvitations = listeCadeauInvitations;
	}

	public List<ListeCadeau> getListeCadeauCreator() {
		return listeCadeauCreator;
	}

	public void setListeCadeauCreator(List<ListeCadeau> listeCadeauCreator) {
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
}
