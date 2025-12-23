package be.couderiannello.models;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import org.json.JSONObject;

import be.couderiannello.dao.DAO;
import be.couderiannello.dao.NotificationDAO;

public class Notification implements Serializable {

	//Attributs
	private static final long serialVersionUID = 1L;

	private int id;
	private String message;
	private LocalDate sendDate;
	private boolean read;
	
	//Relation
	private Personne personne;
	
	//Constructeurs
	public Notification() {
		setSendDate(LocalDate.now());
		setRead(false);
	}
	
	public Notification(int id, String message, Personne personne) {
		this();
		setId(id);
		setMessage(message);
		setPersonne(personne);
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
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		if(message == null || message.isBlank()) {
			throw new IllegalArgumentException("Le message ne peut pas être vide.");
	    }
		 
	    if(message.length() > 500) {
	    	throw new IllegalArgumentException("Le message dépasse 500 caractères.");
	    }
	     
		this.message = message;
	}
	
	public LocalDate getSendDate() {
		return sendDate;
	}
	
	public void setSendDate(LocalDate sendDate) {
		if(sendDate == null) {
            throw new IllegalArgumentException("La date d'envoi ne peut pas être null.");
        }
		
		this.sendDate = sendDate;
	}
	
	public boolean isRead() {
		return read;
	}
	
	public void setRead(boolean read) {
		this.read = read;
	}
	
	//Getters - Setters - Relations
	public Personne getPersonne() {
		return personne;
	}

	public void setPersonne(Personne personne) {
		if(personne == null) {
            throw new IllegalArgumentException("La personne associée à la notification ne peut pas être null.");
        }
		
		this.personne = personne;
	}
	
	// ToString - HashCode - Equals
    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", message='" + message + '\'' +
                ", sendDate=" + sendDate +
                ", read=" + read +
                ", personneId=" + (personne != null ? personne.getId() : null) +
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

        Notification n = (Notification) obj;

        return n.getId() == this.id &&
               n.getMessage().equals(this.message) &&
               n.getSendDate().equals(this.sendDate) &&
               n.isRead() == this.read;
    }
    
    //DAO
    public int create(DAO<Notification> dao) {
        int id = dao.create(this);
        this.setId(id);
        return id;
    }
    
    public static Notification findById(int id, DAO<Notification> dao) {
    	return dao.find(id);
    }
    
    public static Notification findById(int id, NotificationDAO dao, boolean loadPersonne) {
    	return dao.find(id, loadPersonne);
    }
    
    public static List<Notification> findAll(DAO<Notification> dao){
    	return dao.findAll();
    }
    
    public static List<Notification> findAll(NotificationDAO dao, boolean loadPersonne){
    	return dao.findAll(loadPersonne);
    }
    
    public static boolean delete(Notification p, DAO<Notification> dao) {
    	return dao.delete(p);
    }
    
    public boolean update(DAO<Notification> dao) {
    	return dao.update(this);
    }
    
    public void parse(JSONObject json, boolean isCreate) {

        if (json.has("id") && !json.isNull("id")) {
            setId(json.getInt("id"));
        }

        if (isCreate) {
            setMessage(json.getString("message"));
        } else if (json.has("message") && !json.isNull("message")) {
            setMessage(json.getString("message"));
        }

        if (json.has("sendDate") && !json.isNull("sendDate")) {
            setSendDate(LocalDate.parse(json.getString("sendDate")));
        }

        if (json.has("read") && !json.isNull("read")) {
            setRead(json.getBoolean("read"));
        }

        if (isCreate) {
            Personne p = new Personne();
            p.setId(json.getInt("personneId"));
            setPersonne(p);
        }
    }



    //Model -> JSON
    public JSONObject unparse() {
        JSONObject json = new JSONObject();

        json.put("id", getId());
        json.put("message", getMessage());
        json.put("sendDate", getSendDate().toString());
        json.put("read", isRead());
        json.put("personneId", getPersonne().getId());

        return json;
    }
}
