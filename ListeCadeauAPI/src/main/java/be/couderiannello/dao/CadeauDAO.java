package be.couderiannello.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import be.couderiannello.enumeration.StatutPriorite;
import be.couderiannello.models.*;

public class CadeauDAO extends JdbcDAO<Cadeau> {

    public CadeauDAO(Connection conn) {
        super(conn);
    }

    @Override
    public int create(Cadeau c) {

        final String SQL_INSERT = """
            INSERT INTO Cadeau
            (Id, Nom, Description, Prix, Photo, LienSite, Priorite, ListeId)
            VALUES (SEQ_CADEAU.NEXTVAL, ?, ?, ?, ?, ?, ?, ?)
        """;

        final String SQL_GET_ID = "SELECT SEQ_CADEAU.CURRVAL FROM dual";

        int generatedId = -1;

        try (PreparedStatement st = connect.prepareStatement(SQL_INSERT)) {

            st.setString(1, c.getName());
            st.setString(2, c.getDescription());
            st.setDouble(3, c.getPrice());
            st.setString(4, c.getPhoto());
            st.setString(5, c.getLinkSite());
            st.setString(6, c.getPriorite().name());
            st.setInt(7, c.getListeCadeau().getId());

            st.executeUpdate();

            try (PreparedStatement stId = connect.prepareStatement(SQL_GET_ID);
                 ResultSet rs = stId.executeQuery()) {

                if (rs.next()) {
                    generatedId = rs.getInt(1);
                    c.setId(generatedId);
                } else {
                    throw new RuntimeException("Impossible de récupérer l'ID généré pour Cadeau.");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur CadeauDAO.create()", e);
        }

        return generatedId;
    }


    @Override
    public boolean delete(Cadeau c) {

        final String SQL_DEL_RES = "DELETE FROM Reservation WHERE CadeauId=?";
        final String SQL_DELETE   = "DELETE FROM Cadeau WHERE Id=?";

        try (PreparedStatement st = connect.prepareStatement(SQL_DEL_RES)) {
            st.setInt(1, c.getId());
            st.executeUpdate();
            
        } catch (SQLException e) {
            throw new RuntimeException("Erreur CadeauDAO.delete() (réservations)", e);
        }

        try (PreparedStatement st = connect.prepareStatement(SQL_DELETE)) {
            st.setInt(1, c.getId());
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur CadeauDAO.delete()", e);
        }
    }

    @Override
    public boolean update(Cadeau c) {

        final String SQL_UPDATE = """
            UPDATE Cadeau
            SET Nom=?, Description=?, Prix=?, Photo=?, LienSite=?, Priorite=?
            WHERE Id=?
        """;

        try (PreparedStatement st = connect.prepareStatement(SQL_UPDATE)) {

            st.setString(1, c.getName());
            st.setString(2, c.getDescription());
            st.setDouble(3, c.getPrice());
            st.setString(4, c.getPhoto());
            st.setString(5, c.getLinkSite());
            st.setString(6, c.getPriorite().name());
            st.setInt(7, c.getId());

            return st.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur CadeauDAO.update()", e);
        }
    }

    @Override
    public Cadeau find(int id) {
        return find(id, false, false);
    }
    
    public Cadeau find(int id, boolean loadListeCadeau, boolean loadReservations) {

        final String SQL = "SELECT * FROM Cadeau WHERE Id=?";

        try (PreparedStatement st = connect.prepareStatement(SQL)) {

            st.setInt(1, id);

            try (ResultSet rs = st.executeQuery()) {
                if (!rs.next()) {
                	return null;
                }

                int listeId = rs.getInt("ListeId");
                ListeCadeau lc = new ListeCadeau();
                lc.setId(listeId);
                
                Cadeau c = new Cadeau(
                    rs.getInt("Id"),
                    rs.getString("Nom"),
                    rs.getString("Description"),
                    rs.getDouble("Prix"),
                    rs.getString("Photo"),
                    rs.getString("LienSite"),
                    StatutPriorite.valueOf(rs.getString("Priorite")),
                    lc
                );

                if (loadListeCadeau) {
                    loadListeCadeau(c);
                }
                
                if (loadReservations) {
                    loadReservations(c);
                }

                return c;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur CadeauDAO.find()", e);
        }
    }

    @Override
    public List<Cadeau> findAll() {
        return findAll(true, true);
    }
    
    public List<Cadeau> findAll(boolean loadListeCadeau, boolean loadReservations) {

        final String SQL = "SELECT * FROM Cadeau ORDER BY Nom";

        List<Cadeau> list = new ArrayList<>();

        try (PreparedStatement st = connect.prepareStatement(SQL);
             ResultSet rs = st.executeQuery()) {

            while (rs.next()) {

                int listeId = rs.getInt("ListeId");
                ListeCadeau lc = new ListeCadeau();
                lc.setId(listeId);
                
                Cadeau c = new Cadeau(
                    rs.getInt("Id"),
                    rs.getString("Nom"),
                    rs.getString("Description"),
                    rs.getDouble("Prix"),
                    rs.getString("Photo"),
                    rs.getString("LienSite"),
                    StatutPriorite.valueOf(rs.getString("Priorite")),
                    lc
                );

                if (loadListeCadeau) {
                    loadListeCadeau(c);
                }
                
                if (loadReservations) {
                    loadReservations(c);
                }

                list.add(c);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur CadeauDAO.findAll()", e);
        }

        return list;
    }

    private void loadListeCadeau(Cadeau c) {

        final String SQL = """
            SELECT *
            FROM ListeCadeau
            WHERE Id = (SELECT ListeId FROM Cadeau WHERE Id=?)
        """;

        try (PreparedStatement st = connect.prepareStatement(SQL)) {
            st.setInt(1, c.getListeCadeau().getId());

            try (ResultSet rs = st.executeQuery()) {

                if (rs.next()) {

                    ListeCadeau l = new ListeCadeau(); 

                    l.setId(rs.getInt("Id"));
                    l.setTitle(rs.getString("Titre"));
                    l.setEvenement(rs.getString("Evenement"));

                    l.setCreationDate(rs.getDate("DateCreation").toLocalDate());
                    l.setExpirationDate(rs.getDate("DateExpiration").toLocalDate());

                    l.setStatut(rs.getInt("Statut") == 1);

                    String share = rs.getString("ShareLink");
                    if (share != null && !share.isBlank()) {
                        l.setShareLink(share);

                    }
                    else {
                        l.setShareLink("https://default"); 
                    }
                   
                    c.setListeCadeau(l);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur loadListeCadeau()", e);
        }
    }

    private void loadReservations(Cadeau c) {

        final String SQL = "SELECT * FROM Reservation WHERE CadeauId=?";

        List<Reservation> list = new ArrayList<>();

        try (PreparedStatement st = connect.prepareStatement(SQL)) {

            st.setInt(1, c.getId());

            try (ResultSet rs = st.executeQuery()) {

                while (rs.next()) {

                    Reservation r = new Reservation(); 

                    r.setId(rs.getInt("Id"));
                    r.setAmount(rs.getDouble("Montant"));
                    r.setDateReservation(rs.getDate("DateReservation").toLocalDate());
                    r.setCadeau(c);
                    list.add(r);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur loadReservations()", e);
        }

        c.setReservations(list);
    }
}
