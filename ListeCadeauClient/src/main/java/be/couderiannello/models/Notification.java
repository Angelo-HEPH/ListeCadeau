package be.couderiannello.models;

import java.time.LocalDate;

public class Notification {
	//Attributs
	private int id;
	private String message;
	private LocalDate sendDate;
	private boolean read;
	
	//Relation
	private Personne personne;
	
	//Constructeurs
	public Notification() {
		
	}
	
	public Notification(int id, String message, LocalDate sendDate, boolean read, Personne personne) {
		this();
		setId(id);
		setMessage(message);
		setSendDate(sendDate);
		setRead(read);
		setPersonne(personne);
	}
	
	//Getters - Setters
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	public LocalDate getSendDate() {
		return sendDate;
	}
	public void setSendDate(LocalDate sendDate) {
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
		this.personne = personne;
	}
}
