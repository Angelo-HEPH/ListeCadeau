package be.couderiannello.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import be.couderiannello.models.Notification;
import be.couderiannello.models.Personne;

public class NotificationDAO extends DAO<Notification> {

    public NotificationDAO(Connection conn) {
        super(conn);
    }

    @Override
    public int create(Notification n) {

        final String SQL_INSERT = """
            INSERT INTO Notification
            (Message, DateEnvoie, Lu, PersonneId)
            VALUES (?, ?, ?, ?)
        """;

        try (PreparedStatement st = connect.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

            st.setString(1, n.getMessage());
            st.setDate(2, Date.valueOf(n.getSendDate()));
            st.setInt(3, n.isRead() ? 1 : 0);
            st.setInt(4, n.getPersonne().getId());

            st.executeUpdate();

            try (ResultSet rs = st.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    n.setId(id);
                    return id;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur NotificationDAO.create()", e);
        }

        return -1;
    }

    @Override
    public boolean delete(Notification n) {
        final String SQL = "DELETE FROM Notification WHERE Id=?";
        
        try (PreparedStatement st = connect.prepareStatement(SQL)) {
            st.setInt(1, n.getId());
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur NotificationDAO.delete()", e);
        }
    }

    @Override
    public boolean update(Notification n) {

        final String SQL_UPDATE = """
            UPDATE Notification
            SET Message=?, DateEnvoie=?, Lu=?, PersonneId=?
            WHERE Id=?
        """;

        try (PreparedStatement st = connect.prepareStatement(SQL_UPDATE)) {

            st.setString(1, n.getMessage());
            st.setDate(2, Date.valueOf(n.getSendDate()));
            st.setInt(3, n.isRead() ? 1 : 0);
            st.setInt(4, n.getPersonne().getId());
            st.setInt(5, n.getId());

            return st.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur NotificationDAO.update()", e);
        }
    }

    @Override
    public Notification find(int id) {

        final String SQL = "SELECT * FROM Notification WHERE Id=?";

        try (PreparedStatement st = connect.prepareStatement(SQL)) {

            st.setInt(1, id);

            try (ResultSet rs = st.executeQuery()) {

                if (!rs.next()) return null;

                Notification n = new Notification(); 

                n.setId(rs.getInt("Id"));
                n.setMessage(rs.getString("Message"));
                n.setSendDate(rs.getDate("DateEnvoie").toLocalDate());
                n.setRead(rs.getInt("Lu") == 1);

                Personne p = loadPersonne(rs.getInt("PersonneId"));
                n.setPersonne(p);

                return n;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur NotificationDAO.find()", e);
        }
    }

    @Override
    public List<Notification> findAll() {

        final String SQL = "SELECT * FROM Notification ORDER BY DateEnvoie DESC";

        List<Notification> list = new ArrayList<>();

        try (PreparedStatement st = connect.prepareStatement(SQL);
             ResultSet rs = st.executeQuery()) {

            while (rs.next()) {

                Notification n = new Notification();

                n.setId(rs.getInt("Id"));
                n.setMessage(rs.getString("Message"));
                n.setSendDate(rs.getDate("DateEnvoie").toLocalDate());
                n.setRead(rs.getInt("Lu") == 1);

                Personne p = loadPersonne(rs.getInt("PersonneId"));
                n.setPersonne(p);

                list.add(n);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur NotificationDAO.findAll()", e);
        }

        return list;
    }

    private Personne loadPersonne(int idPersonne) {

        final String SQL = "SELECT * FROM Personne WHERE Id=?";

        try (PreparedStatement st = connect.prepareStatement(SQL)) {

            st.setInt(1, idPersonne);

            try (ResultSet rs = st.executeQuery()) {
                if (!rs.next()) 
                	return null;

                return new Personne( rs.getInt("Id"), 
                		rs.getString("Nom"), 
                		rs.getString("Prenom"), 
                		rs.getInt("Age"), 
                		rs.getString("Rue"),
                		rs.getString("Ville"), 
                		rs.getString("Numero"), 
                        rs.getInt("CodePostal"),
                		rs.getString("Email"), 
                		rs.getString("MotDePasse") );
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur loadPersonne()", e);
        }
    }
}
