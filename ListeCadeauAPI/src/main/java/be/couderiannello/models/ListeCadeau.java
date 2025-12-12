package be.couderiannello.models;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import be.couderiannello.dao.DAO;
import be.couderiannello.dao.ListeCadeauDAO;

public class ListeCadeau implements Serializable {

    //Attributs
    private static final long serialVersionUID = 1L;

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
            boolean statut, Personne creator, Cadeau cadeau) {
        this();
        setId(id);
        setTitle(title);
        setEvenement(evenement);
        this.creationDate = LocalDate.now();
        setExpirationDate(expirationDate);
        setStatut(statut);
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
        if(id < 0) {
            throw new IllegalArgumentException("L'Id ne peut pas être plus petit que 0.");
        }
        
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        if(title == null || title.isBlank()) {
            throw new IllegalArgumentException("Le titre ne peut pas être vide.");
        }
        
        if(title.length() > 150) {
            throw new IllegalArgumentException("Le titre dépasse 150 caractères.");
        }
        
        this.title = title;
    }
    
    public String getEvenement() {
        return evenement;
    }
    
    public void setEvenement(String evenement) {
        if(evenement == null || evenement.isBlank()) {
            throw new IllegalArgumentException("L'événement ne peut pas être vide.");
        }
        
        this.evenement = evenement;
    }
    
    public LocalDate getCreationDate() {
        return creationDate;
    }
    
    public void setCreationDate(LocalDate creationDate) {
        if (this.creationDate != null) {
            throw new IllegalStateException("La date de création ne peut pas être modifiée.");
        }
        
        this.creationDate = creationDate;
    }

    
    public LocalDate getExpirationDate() {
        return expirationDate;
    }
    
    public void setExpirationDate(LocalDate expirationDate) {
        if(expirationDate == null) {
            throw new IllegalArgumentException("La date d'expiration est obligatoire.");
        }
        
        if(creationDate != null && expirationDate.isBefore(creationDate)) {
            throw new IllegalArgumentException("La date d'expiration ne peut pas être avant la date de création.");
        }
        
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
        if(shareLink == null || shareLink.isBlank()) {
            throw new IllegalArgumentException("Le lien de partage ne peut pas être vide.");
        }
        
        if(!shareLink.startsWith("http://") && !shareLink.startsWith("https://")) {
            throw new IllegalArgumentException("Le lien de partage doit être une URL valide.");
        }
        
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
    
    //ToString - HashCode - Equals
    @Override
    public String toString() {
        return "ListeCadeau{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", evenement='" + evenement + '\'' +
                ", creationDate=" + creationDate +
                ", expirationDate=" + expirationDate +
                ", statut=" + Statut +
                ", shareLink='" + shareLink + '\'' +
                ", creatorId=" + (creator != null ? creator.getId() : null) +
                ", invitesCount=" + invites.size() +
                ", cadeauxCount=" + cadeaux.size() +
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

        ListeCadeau lc = (ListeCadeau) obj;

        return lc.getId() == this.id &&
               lc.getTitle().equals(this.title) &&
               lc.getEvenement().equals(this.evenement) &&
               lc.getCreationDate().equals(this.creationDate) &&
               lc.getExpirationDate().equals(this.expirationDate) &&
               lc.isStatut() == this.Statut &&
               lc.getShareLink().equals(this.shareLink);
    }
    
    //DAO
    public int create(DAO<ListeCadeau> dao) {
        int id = dao.create(this);
        this.setId(id);
        return id;
    }
    
    public static ListeCadeau findById(int id, DAO<ListeCadeau> dao) {
    	return dao.find(id);
    }
    
    public static ListeCadeau findById(int id, ListeCadeauDAO dao, boolean loadCreator, boolean loadInvites, boolean loadCadeaux) {
    	return dao.find(id, loadCreator, loadInvites, loadCadeaux);
    }
    
    public static List<ListeCadeau> findAll(DAO<ListeCadeau> dao){
    	return dao.findAll();
    }
    public static List<ListeCadeau> findAll(ListeCadeauDAO dao, boolean loadCreator, boolean loadInvites, boolean loadCadeaux){
    	return dao.findAll(loadCreator, loadInvites, loadCadeaux);
    }
    
    public static boolean delete(ListeCadeau l, DAO<ListeCadeau> dao) {
    	return dao.delete(l);
    }
    
    public boolean update(DAO<ListeCadeau> dao) {
    	return dao.update(this);
    }
}
