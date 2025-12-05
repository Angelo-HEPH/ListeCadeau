package be.couderiannello.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ListeCadeau {

	//Attributs
	private int id;
	private String title;
	private String evenement;
	private LocalDate creationDate;
	private LocalDate expirationDate;
	private boolean Statut;
	private String shareLink;
	
	//Relations
	private Personne creator;
	private List<Personne> invites;
	private List<Cadeau> cadeaux;
	
	//Constructor
	public ListeCadeau() {
		invites = new ArrayList<Personne>();
		cadeaux = new ArrayList<Cadeau>();
	}
	
	public ListeCadeau(int id, String title, String evenement, LocalDate creationDate, LocalDate expirationDate,
			boolean statut, String shareLink, Personne creator, Cadeau cadeau) {
		this();
		setId(id);
		setTitle(title);
		setEvenement(evenement);
		setCreationDate(creationDate);
		setExpirationDate(expirationDate);
		setStatut(statut);
		setShareLink(shareLink);
		setCreator(creator);
		
        if (creator == null) {
            throw new IllegalArgumentException("Le créateur ne peut pas être null.");
        }
        
        if (cadeau == null) {
            throw new IllegalArgumentException("Le cadeau ne peut pas être null.");
        }

        setCreator(creator);
        addCadeau(cadeau);
	}
	
	//Getters - Setters
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getEvenement() {
		return evenement;
	}
	public void setEvenement(String evenement) {
		this.evenement = evenement;
	}
	
	public LocalDate getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(LocalDate creationDate) {
		this.creationDate = creationDate;
	}
	
	public LocalDate getExpirationDate() {
		return expirationDate;
	}
	public void setExpirationDate(LocalDate expirationDate) {
		this.expirationDate = expirationDate;
	}
	
	public boolean isStatut() {
		return Statut;
	}
	public void setStatut(boolean statut) {
		Statut = statut;
	}
	
	public String getShareLink() {
		return shareLink;
	}
	public void setShareLink(String shareLink) {
		this.shareLink = shareLink;
	}

	//Getters - Setters - Relations
	public Personne getCreator() {
		return creator;
	}

    public void setCreator(Personne creator) {
        if (this.creator == creator) {
            return;
        }

        Personne old = this.creator;
        this.creator = creator;

        if (old != null) {
            old.getListeCadeauCreator().remove(this);
        }
        if (creator != null && !creator.getListeCadeauCreator().contains(this)) {
            creator.getListeCadeauCreator().add(this);
        }
    }

	public List<Personne> getInvites() {
		return invites;
	}

	public void setInvites(List<Personne> invites) {
		if (invites == null) {
            throw new IllegalArgumentException("La liste des invités ne peut pas être null.");
		}
        
		this.invites = invites;
	}

	public List<Cadeau> getCadeaux() {
		return cadeaux;
	}

	public void setCadeaux(List<Cadeau> cadeaux) {
		 if (cadeaux == null) {
	            throw new IllegalArgumentException("La liste des cadeaux ne peut pas être null.");
		 }
	        
		this.cadeaux = cadeaux;
	}
	
	//Méthodes
    public void addInvite(Personne p) {
        if (p == null) {
            throw new IllegalArgumentException("L'invité ne peut pas être null.");
        }
        
        if (invites.contains(p)) {
            throw new IllegalArgumentException("Cette personne est déjà invitée.");
        }
        
        invites.add(p);
        if (!p.getListeCadeauInvitations().contains(this)) {
            p.getListeCadeauInvitations().add(this);
        }
    }

    public void removeInvite(Personne p) {
        if (p == null) {
            throw new IllegalArgumentException("L'invité ne peut pas être null.");
        }
        
        if (!invites.contains(p)) {
            throw new IllegalArgumentException("Cette personne n'est pas invitée à cette liste.");
        }
        
        invites.remove(p);
        if (p.getListeCadeauInvitations().contains(this)) {
            p.getListeCadeauInvitations().remove(this);
        }
    }

    public void addCadeau(Cadeau c) {
        if (c == null) {
            throw new IllegalArgumentException("Le cadeau ne peut pas être null.");
        }
        
        if (cadeaux.contains(c)) {
            throw new IllegalArgumentException("Ce cadeau est déjà dans la liste.");
        }
        
        cadeaux.add(c);
        if (c.getListeCadeau() != this) {
            c.setListeCadeau(this);
        }
    }

    public void removeCadeau(Cadeau c) {
        if (c == null) {
            throw new IllegalArgumentException("Le cadeau ne peut pas être null.");
        }
        
        if (!cadeaux.contains(c)) {
            throw new IllegalArgumentException("Ce cadeau n'est pas dans cette liste.");
        }
        
        cadeaux.remove(c);
        if (c.getListeCadeau() == this) {
            c.setListeCadeau(null);
        }
    }
}
