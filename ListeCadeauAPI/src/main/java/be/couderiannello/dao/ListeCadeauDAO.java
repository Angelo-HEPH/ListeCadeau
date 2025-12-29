package be.couderiannello.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import be.couderiannello.enumeration.StatutCadeau;
import be.couderiannello.enumeration.StatutPriorite;
import be.couderiannello.models.*;

public class ListeCadeauDAO extends JdbcDAO<ListeCadeau> {

    public ListeCadeauDAO(Connection conn) {
        super(conn);
    }
    
    @Override
    public int create(ListeCadeau l) {

        final String SQL_INSERT = """
            INSERT INTO ListeCadeau 
            (Id, Titre, Evenement, DateCreation, DateExpiration, Statut, LienPartage, CreateurId)
            VALUES (SEQ_LISTECADEAU.NEXTVAL, ?, ?, ?, ?, ?, ?, ?)
        """;

        final String SQL_GET_ID = "SELECT SEQ_LISTECADEAU.CURRVAL FROM dual";

        final String SQL_ADD_INVITE = """
            INSERT INTO ListeCadeau_Invites (ListeId, PersonneId)
            VALUES (?, ?)
        """;

        int generatedId = -1;

        try (PreparedStatement st = connect.prepareStatement(SQL_INSERT)) {

            st.setString(1, l.getTitle());
            st.setString(2, l.getEvenement());
            st.setDate(3, Date.valueOf(l.getCreationDate()));
            st.setDate(4, Date.valueOf(l.getExpirationDate()));
            st.setInt(5, l.isStatut() ? 1 : 0);
            st.setString(6, l.getShareLink());
            st.setInt(7, l.getCreator().getId());

            st.executeUpdate();

            try (PreparedStatement stId = connect.prepareStatement(SQL_GET_ID);
                 ResultSet rs = stId.executeQuery()) {

                if (rs.next()) {
                    generatedId = rs.getInt(1);
                    l.setId(generatedId);
                } else {
                    throw new RuntimeException("Impossible de récupérer l'ID généré pour ListeCadeau.");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur ListeCadeauDAO.create()", e);
        }

        try (PreparedStatement st = connect.prepareStatement(SQL_ADD_INVITE)) {

            for (Personne p : l.getInvites()) {
                st.setInt(1, generatedId);
                st.setInt(2, p.getId());
                st.executeUpdate();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur ListeCadeauDAO.create() (invites)", e);
        }

        return generatedId;
    }



    @Override
    public boolean delete(ListeCadeau l) {

        final String SQL_DELETE_INVITES = "DELETE FROM ListeCadeau_Invites WHERE ListeId=?";
        final String SQL_DELETE_LISTE   = "DELETE FROM ListeCadeau WHERE Id=?";

        try {
            try (PreparedStatement st = connect.prepareStatement(SQL_DELETE_INVITES)) {
                st.setInt(1, l.getId());
                st.executeUpdate();
            }

            try (PreparedStatement st = connect.prepareStatement(SQL_DELETE_LISTE)) {
                st.setInt(1, l.getId());
                st.executeUpdate();
            }

            return true;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur ListeCadeauDAO.delete()", e);
        }
    }

    @Override
    public boolean update(ListeCadeau l) {

        final String SQL_UPDATE = """
            UPDATE ListeCadeau
            SET Titre=?, Evenement=?, DateExpiration=?, Statut=?, LienPartage=?, CreateurId=?
            WHERE Id=?
        """;

        final String SQL_CLEAR_INVITES = "DELETE FROM ListeCadeau_Invites WHERE ListeId=?";
        final String SQL_ADD_INVITE = """
            INSERT INTO ListeCadeau_Invites (ListeId, PersonneId)
            VALUES (?, ?)
        """;

        try {
            try (PreparedStatement st = connect.prepareStatement(SQL_UPDATE)) {
                st.setString(1, l.getTitle());
                st.setString(2, l.getEvenement());
                st.setDate(3, Date.valueOf(l.getExpirationDate()));
                st.setInt(4, l.isStatut() ? 1 : 0);
                st.setString(5, l.getShareLink());
                st.setInt(6, l.getCreator().getId());
                st.setInt(7, l.getId());
                st.executeUpdate();
            }

            try (PreparedStatement st = connect.prepareStatement(SQL_CLEAR_INVITES)) {
                st.setInt(1, l.getId());
                st.executeUpdate();
            }

            try (PreparedStatement st = connect.prepareStatement(SQL_ADD_INVITE)) {
                for (Personne p : l.getInvites()) {
                    st.setInt(1, l.getId());
                    st.setInt(2, p.getId());
                    st.executeUpdate();
                }
            }

            return true;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur ListeCadeauDAO.update()", e);
        }
    }

    @Override
    public ListeCadeau find(int id) {
        return find(id, true, true, true);
    }
    
    public ListeCadeau find(int id, boolean loadCreator, boolean loadInvites, boolean loadCadeaux) {

        final String SQL = "SELECT * FROM ListeCadeau WHERE Id=?";

        try (PreparedStatement st = connect.prepareStatement(SQL)) {

            st.setInt(1, id);

            try (ResultSet rs = st.executeQuery()) {

                if (!rs.next()) {
                    return null;
                }

                ListeCadeau l = new ListeCadeau(); 

                l.setId(rs.getInt("Id"));
                l.setTitle(rs.getString("Titre"));
                l.setEvenement(rs.getString("Evenement"));
                l.initCreationDate(rs.getDate("DateCreation").toLocalDate());
                l.setExpirationDate(rs.getDate("DateExpiration").toLocalDate());
                l.setStatut(rs.getInt("Statut") == 1);
                l.setShareLink(rs.getString("LienPartage"));

                Personne creator = new Personne();
                creator.setId(rs.getInt("CreateurId"));
                l.setCreator(creator);

                if (loadCreator) {
                	loadCreator(l);
                }
                
                if (loadInvites) {
                	loadInvites(l);
                }
                
                if (loadCadeaux) {
                	loadCadeaux(l);
                }

                return l;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur ListeCadeauDAO.find()", e);
        }
    }

    public List<ListeCadeau> findAll() {
        return findAll(true, true, true);
    }
    
    public List<ListeCadeau> findAll(boolean loadCreator, boolean loadInvites, boolean loadCadeaux) {

        final String SQL = "SELECT * FROM ListeCadeau ORDER BY DateCreation DESC";

        List<ListeCadeau> list = new ArrayList<>();

        try (PreparedStatement st = connect.prepareStatement(SQL);
             ResultSet rs = st.executeQuery()) {

            while (rs.next()) {

                ListeCadeau l = new ListeCadeau(); 

                l.setId(rs.getInt("Id"));
                l.setTitle(rs.getString("Titre"));
                l.setEvenement(rs.getString("Evenement"));
                l.initCreationDate(rs.getDate("DateCreation").toLocalDate());
                l.setExpirationDate(rs.getDate("DateExpiration").toLocalDate());
                l.setStatut(rs.getInt("Statut") == 1);                
                l.setShareLink(rs.getString("LienPartage"));

                Personne creator = new Personne();
                creator.setId(rs.getInt("CreateurId"));
                l.setCreator(creator);

                
                if (loadCreator) {
                	loadCreator(l);
                }
                
                if (loadInvites) {
                	loadInvites(l);
                }
                
                if (loadCadeaux) {
                	loadCadeaux(l);
                }

                list.add(l);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur ListeCadeauDAO.findAll()", e);
        }

        return list;
    }


    private void loadCreator(ListeCadeau l) {

        final String SQL = "SELECT * FROM Personne WHERE Id = (SELECT CreateurId FROM ListeCadeau WHERE Id=?)";

        try (PreparedStatement st = connect.prepareStatement(SQL)) {

            st.setInt(1, l.getId());

            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {

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

                    l.setCreator(p);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur loadCreator()", e);
        }
    }

    private void loadInvites(ListeCadeau l) {

        final String SQL = """
            SELECT p.* 
            FROM ListeCadeau_Invites li
            JOIN Personne p ON p.Id = li.PersonneId
            WHERE li.ListeId=?
        """;

        List<Personne> invites = new ArrayList<>();

        try (PreparedStatement st = connect.prepareStatement(SQL)) {

            st.setInt(1, l.getId());

            try (ResultSet rs = st.executeQuery()) {

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

                    invites.add(p);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur loadInvites()", e);
        }

        l.setInvites(invites);
    }

    private void loadCadeaux(ListeCadeau l) {

        final String SQL = "SELECT * FROM Cadeau WHERE ListeId=?";

        List<Cadeau> cadeaux = new ArrayList<>();

        try (PreparedStatement st = connect.prepareStatement(SQL)) {

            st.setInt(1, l.getId());

            try (ResultSet rs = st.executeQuery()) {

                while (rs.next()) {

                    Cadeau c = new Cadeau(
                        rs.getInt("Id"),
                        rs.getString("Nom"),
                        rs.getString("Description"),
                        rs.getDouble("Prix"),
                        rs.getString("Photo"),
                        rs.getString("LienSite"),
                        StatutPriorite.valueOf(rs.getString("Priorite")),
                        l
                    );

                    c.setStatutCadeau(StatutCadeau.valueOf(rs.getString("STATUTCADEAU")));
                    cadeaux.add(c);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur loadCadeaux()", e);
        }

        l.setCadeaux(cadeaux);
    }
    
    public boolean addInvite(int listeId, int personneId) {

        final String SQL = """
            INSERT INTO listecadeau_invites (listeId, personneId)
            VALUES (?, ?)
        """;

        try (PreparedStatement st = connect.prepareStatement(SQL)) {
            st.setInt(1, listeId);
            st.setInt(2, personneId);
            
            return st.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur ListeCadeauDAO.insertInvite()", e);
        }
    }

    public boolean removeInvite(int listeId, int personneId) {

        final String SQL = """
            DELETE FROM listecadeau_invites
            WHERE listeId = ? AND personneId = ?
        """;

        try (PreparedStatement st = connect.prepareStatement(SQL)) {
            st.setInt(1, listeId);
            st.setInt(2, personneId);
            
            return st.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur ListeCadeauDAO.removeInvite()", e);
        }
    }

}
