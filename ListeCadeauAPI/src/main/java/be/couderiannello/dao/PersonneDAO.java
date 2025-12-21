package be.couderiannello.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import be.couderiannello.models.*;

public class PersonneDAO extends JdbcDAO<Personne> {

    public PersonneDAO(Connection conn) {
        super(conn);
    }

    @Override
    public int create(Personne p) {

        final String SQL_INSERT = """
            INSERT INTO Personne 
            (ID, Nom, Prenom, Age, Rue, Ville, Numero, CodePostal, Email, MotDePasse)
            VALUES (SEQ_PERSONNE.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement st = connect.prepareStatement(SQL_INSERT)) {

            st.setString(1, p.getName());
            st.setString(2, p.getFirstName());
            st.setInt(3, p.getAge());
            st.setString(4, p.getStreet());
            st.setString(5, p.getCity());
            st.setString(6, p.getStreetNumber());
            st.setInt(7, p.getPostalCode());
            st.setString(8, p.getEmail());
            st.setString(9, p.getPassword());

            st.executeUpdate();

            final String SQL_GET_ID = "SELECT SEQ_PERSONNE.CURRVAL FROM dual";

            try (PreparedStatement stId = connect.prepareStatement(SQL_GET_ID);
                 ResultSet rs = stId.executeQuery()) {

                if (rs.next()) {
                    int generatedId = rs.getInt(1);
                    p.setId(generatedId);
                    return generatedId;
                } else {
                    throw new RuntimeException("Impossible de récupérer l'ID généré.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur PersonneDAO.create()", e);
        }
    }


    @Override
    public boolean delete(Personne p) {

        final String SQL_DELETE = "DELETE FROM Personne WHERE Id=?";

        try (PreparedStatement st = connect.prepareStatement(SQL_DELETE)) {
            st.setInt(1, p.getId());
            return st.executeUpdate() > 0;
        } 
        catch (SQLException e) {
            throw new RuntimeException("Erreur PersonneDAO.delete()", e);
        }
    }

    @Override
    public boolean update(Personne p) {

        final String SQL_UPDATE = """
            UPDATE Personne 
            SET Nom=?, Prenom=?, Age=?, Rue=?, Ville=?, Numero=?, 
                CodePostal=?, Email=?, MotDePasse=?
            WHERE Id=?
        """;

        try (PreparedStatement st = connect.prepareStatement(SQL_UPDATE)) {

            st.setString(1, p.getName());
            st.setString(2, p.getFirstName());
            st.setInt(3, p.getAge());
            st.setString(4, p.getStreet());
            st.setString(5, p.getCity());
            st.setString(6, p.getStreetNumber());
            st.setInt(7, p.getPostalCode());
            st.setString(8, p.getEmail());
            st.setString(9, p.getPassword());
            st.setInt(10, p.getId());

            return st.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur PersonneDAO.update()", e);
        }
    }

    @Override
    public Personne find(int id) {
        return find(id, true, true, true, true);
    }

    public Personne find(int id, boolean loadNotifications, boolean loadCreatedLists, boolean loadInvitedLists, 
    		boolean loadReservations) {

        final String SQL = "SELECT * FROM Personne WHERE Id=?";

        try (PreparedStatement st = connect.prepareStatement(SQL)) {

            st.setInt(1, id);

            try (ResultSet rs = st.executeQuery()) {

                if (!rs.next())
                    return null;

                Personne p = new Personne(
                    rs.getInt("Id"),
                    rs.getString("Nom"),
                    rs.getString("Prenom"),
                    rs.getInt("Age"),
                    rs.getString("Rue"),
                    rs.getString("Ville"),
                    rs.getString("Numero"),
                    rs.getInt("CodePostal"),
                    rs.getString("Email"),
                    rs.getString("MotDePasse")
                );

                if (loadNotifications) {
                	loadNotifications(p);
                }
                
                if (loadCreatedLists) {
                	loadCreatedLists(p);
                }
                
                if (loadInvitedLists) {
                	loadInvitedLists(p);
                }
                
                if (loadReservations) {
                	loadReservations(p);
                }

                return p;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur PersonneDAO.find()", e);
        }
    }

    @Override
    public List<Personne> findAll() {
        return findAll(true, true, true, true);
    }

    public List<Personne> findAll(boolean loadNotifications, boolean loadCreatedLists, boolean loadInvitedLists, 
    		boolean loadReservations) {

        final String SQL = "SELECT * FROM Personne ORDER BY Nom, Prenom";

        List<Personne> list = new ArrayList<>();

        try (PreparedStatement st = connect.prepareStatement(SQL);
             ResultSet rs = st.executeQuery()) {

            while (rs.next()) {

                Personne p = new Personne(
                    rs.getInt("Id"),
                    rs.getString("Nom"),
                    rs.getString("Prenom"),
                    rs.getInt("Age"),
                    rs.getString("Rue"),
                    rs.getString("Ville"),
                    rs.getString("Numero"),
                    rs.getInt("CodePostal"),
                    rs.getString("Email"),
                    rs.getString("MotDePasse")
                );

                if (loadNotifications) {
                	loadNotifications(p);
                }
                
                if (loadCreatedLists) {
                	loadCreatedLists(p);
                }
                
                if (loadInvitedLists) {
                	loadInvitedLists(p);
                }
                
                if (loadReservations) {
                	loadReservations(p);
                }

                list.add(p);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur PersonneDAO.findAll()", e);
        }

        return list;
    }

    private void loadNotifications(Personne p) {

        final String SQL = """
            SELECT Id, Message, DateEnvoie, Lu
            FROM Notification 
            WHERE PersonneId=?
        """;

        List<Notification> list = new ArrayList<>();

        try (PreparedStatement st = connect.prepareStatement(SQL)) {

            st.setInt(1, p.getId());

            try (ResultSet rs = st.executeQuery()) {

                while (rs.next()) {

                    Notification n = new Notification(
                        rs.getInt("Id"),
                        rs.getString("Message"),
                        p
                    );
                    
                    n.setSendDate(rs.getDate("DateEnvoie").toLocalDate());
                    n.setRead(rs.getInt("Lu") == 1);
                    list.add(n);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur loadNotifications()", e);
        }

        p.setNotifications(list);
    }
    
    public Personne authenticate(String email, String password) {

        final String SQL = """
            SELECT * 
            FROM Personne 
            WHERE Email = ? 
              AND MotDePasse = HASH_MD5(?)
        """;

        try (PreparedStatement st = connect.prepareStatement(SQL)) {

            st.setString(1, email);
            st.setString(2, password); 

            try (ResultSet rs = st.executeQuery()) {

                if (!rs.next()) {
                    return null;
                }

                Personne p = new Personne(
                    rs.getInt("Id"),
                    rs.getString("Nom"),
                    rs.getString("Prenom"),
                    rs.getInt("Age"),
                    rs.getString("Rue"),
                    rs.getString("Ville"),
                    rs.getString("Numero"),
                    rs.getInt("CodePostal"),
                    rs.getString("Email"),
                    rs.getString("MotDePasse") 
                );

                return p;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur PersonneDAO.authenticate()", e);
        }
    }

    private void loadCreatedLists(Personne p) {

        final String SQL = """
            SELECT * FROM ListeCadeau WHERE CreateurId=?
        """;

        List<ListeCadeau> list = new ArrayList<>();

        try (PreparedStatement st = connect.prepareStatement(SQL)) {

            st.setInt(1, p.getId());

            try (ResultSet rs = st.executeQuery()) {

                while (rs.next()) {

                    ListeCadeau l = new ListeCadeau(); 
                    
                    l.setId(rs.getInt("Id"));
                    l.setTitle(rs.getString("Titre"));
                    l.setEvenement(rs.getString("Evenement"));
                    l.initCreationDate(rs.getDate("DateCreation").toLocalDate());
                    l.setExpirationDate(rs.getDate("DateExpiration").toLocalDate());
                    l.setStatut(rs.getInt("Statut") == 1);
                    l.setCreator(p); 

                    list.add(l);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur loadCreatedLists()", e);
        }

        p.setListeCadeauCreator(list);
    }

    private void loadInvitedLists(Personne p) {

        final String SQL = """
            SELECT lc.* 
            FROM ListeCadeau_Invites li
            JOIN ListeCadeau lc ON lc.Id = li.ListeId
            WHERE li.PersonneId=?
        """;

        List<ListeCadeau> list = new ArrayList<>();

        try (PreparedStatement st = connect.prepareStatement(SQL)) {

            st.setInt(1, p.getId());

            try (ResultSet rs = st.executeQuery()) {

                while (rs.next()) {

                    ListeCadeau l = new ListeCadeau(); 

                    l.setId(rs.getInt("Id"));
                    l.setTitle(rs.getString("Titre"));
                    l.setEvenement(rs.getString("Evenement"));
                    l.initCreationDate(rs.getDate("DateCreation").toLocalDate());
                    l.setExpirationDate(rs.getDate("DateExpiration").toLocalDate());
                    l.setStatut(rs.getInt("Statut") == 1);
                    l.getInvites().add(p);       
                    p.getListeCadeauInvitations().add(l);

                    list.add(l);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur loadInvitedLists()", e);
        }

        p.setListeCadeauInvitations(list);
    }

    private void loadReservations(Personne p) {

        final String SQL = """
            SELECT r.*
            FROM Personne_Reservation pr
            JOIN Reservation r ON r.Id = pr.ReservationId
            WHERE pr.PersonneId = ?
        """;

        List<Reservation> list = new ArrayList<>();

        try (PreparedStatement st = connect.prepareStatement(SQL)) {

            st.setInt(1, p.getId());

            try (ResultSet rs = st.executeQuery()) {

                while (rs.next()) {

                    Reservation r = new Reservation(); 

                    r.setId(rs.getInt("Id"));
                    r.setAmount(rs.getDouble("Montant"));
                    r.setDateReservation(rs.getDate("DateReservation").toLocalDate());
                    r.getPersonnes().add(p);         
                    p.getReservations().add(r);      

                    list.add(r);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur loadReservations()", e);
        }

        p.setReservations(list);
    }

}
