package be.couderiannello.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import be.couderiannello.enumeration.StatutPriorite;
import be.couderiannello.models.*;

public class ReservationDAO extends JdbcDAO<Reservation> {

    public ReservationDAO(Connection conn) {
        super(conn);
    }

    @Override
    public int create(Reservation r) {

        final String SQL_INSERT = """
            INSERT INTO Reservation (Id, Montant, DateReservation, CadeauId)
            VALUES (SEQ_RESERVATION.NEXTVAL, ?, ?, ?)
        """;

        final String SQL_GET_ID = "SELECT SEQ_RESERVATION.CURRVAL FROM dual";

        final String SQL_LINK = """
            INSERT INTO Personne_Reservation (PersonneId, ReservationId)
            VALUES (?, ?)
        """;

        int id = -1;

        try (PreparedStatement st = connect.prepareStatement(SQL_INSERT)) {

            st.setDouble(1, r.getAmount());
            st.setDate(2, Date.valueOf(r.getDateReservation()));
            st.setInt(3, r.getCadeau().getId());
            st.executeUpdate();

            try (PreparedStatement stId = connect.prepareStatement(SQL_GET_ID);
                 ResultSet rs = stId.executeQuery()) {

                if (rs.next()) {
                    id = rs.getInt(1);
                    r.setId(id);
                } else {
                    r.setId(-1);
                    id = -1;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur ReservationDAO.create()", e);
        }

        try (PreparedStatement st = connect.prepareStatement(SQL_LINK)) {
            for (Personne p : r.getPersonnes()) {
                st.setInt(1, p.getId());
                st.setInt(2, id);
                st.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur ReservationDAO.create()", e);
        }

        return id;
    }


    @Override
    public boolean delete(Reservation r) {

        final String SQL_DELETE_LINKS = "DELETE FROM Personne_Reservation WHERE ReservationId=?";
        final String SQL_DELETE       = "DELETE FROM Reservation WHERE Id=?";

        try (PreparedStatement st = connect.prepareStatement(SQL_DELETE_LINKS)) {
            st.setInt(1, r.getId());
            st.executeUpdate();
            
        } catch (SQLException e) {
            throw new RuntimeException("Erreur ReservationDAO.delete() (liens)", e);
        }

        try (PreparedStatement st = connect.prepareStatement(SQL_DELETE)) {
            st.setInt(1, r.getId());
            return st.executeUpdate() > 0;
            
        } catch (SQLException e) {
            throw new RuntimeException("Erreur ReservationDAO.delete()", e);
        }
    }

    @Override
    public boolean update(Reservation r) {

        final String SQL_UPDATE = """
            UPDATE Reservation 
            SET Montant=?, DateReservation=?, CadeauId=?
            WHERE Id=?
        """;

        final String SQL_DELETE_LINKS = "DELETE FROM Personne_Reservation WHERE ReservationId=?";
        final String SQL_INSERT_LINK  = """
            INSERT INTO Personne_Reservation (PersonneId, ReservationId)
            VALUES (?, ?)
        """;

        try (PreparedStatement st = connect.prepareStatement(SQL_UPDATE)) {
            st.setDouble(1, r.getAmount());
            st.setDate(2, Date.valueOf(r.getDateReservation()));
            st.setInt(3, r.getCadeau().getId());
            st.setInt(4, r.getId());
            st.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur ReservationDAO.update()", e);
        }

        try (PreparedStatement st = connect.prepareStatement(SQL_DELETE_LINKS)) {
            st.setInt(1, r.getId());
            st.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur ReservationDAO.update()", e);
        }

        try (PreparedStatement st = connect.prepareStatement(SQL_INSERT_LINK)) {
            for (Personne p : r.getPersonnes()) {
                st.setInt(1, p.getId());
                st.setInt(2, r.getId());
                st.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur ReservationDAO.update()", e);
        }

        return true;
    }

    @Override
    public Reservation find(int id) {
        return find(id, true, true);
    }

    public Reservation find(int id, boolean loadCadeau, boolean loadPersonnes) {

        final String SQL = "SELECT * FROM Reservation WHERE Id=?";

        try (PreparedStatement st = connect.prepareStatement(SQL)) {

            st.setInt(1, id);

            try (ResultSet rs = st.executeQuery()) {

                if (!rs.next())
                    return null;

                Reservation r = new Reservation(); 

                r.setId(rs.getInt("Id"));
                r.setAmount(rs.getDouble("Montant"));
                r.setDateReservation(rs.getDate("DateReservation").toLocalDate());

                if (loadCadeau) {
                    r.setCadeau(loadCadeau(rs.getInt("CadeauId")));
                }

                if (loadPersonnes) {
                    r.setPersonnes(loadPersonnes(id));
                }

                return r;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur ReservationDAO.find()", e);
        }
    }

    @Override
    public List<Reservation> findAll() {
        return findAll(true, true);
    }

    public List<Reservation> findAll(boolean loadCadeau, boolean loadPersonnes) {

        final String SQL = "SELECT * FROM Reservation ORDER BY Id DESC";

        List<Reservation> list = new ArrayList<>();

        try (PreparedStatement st = connect.prepareStatement(SQL);
             ResultSet rs = st.executeQuery()) {

            while (rs.next()) {

                Reservation r = new Reservation(); 

                r.setId(rs.getInt("Id"));
                r.setAmount(rs.getDouble("Montant"));
                r.setDateReservation(rs.getDate("DateReservation").toLocalDate());

                if (loadCadeau) {
                    r.setCadeau(loadCadeau(rs.getInt("CadeauId")));
                }

                if (loadPersonnes) {
                    r.setPersonnes(loadPersonnes(r.getId()));
                }
                
                list.add(r);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur ReservationDAO.findAll()", e);
        }

        return list;
    }

    private Cadeau loadCadeau(int idCadeau) {

        final String SQL = "SELECT * FROM Cadeau WHERE Id=?";

        try (PreparedStatement st = connect.prepareStatement(SQL)) {

            st.setInt(1, idCadeau);

            try (ResultSet rs = st.executeQuery()) {

                if (!rs.next()) return null;

                Cadeau c = new Cadeau(); 

                c.setId(rs.getInt("Id"));
                c.setName(rs.getString("Nom"));
                c.setDescription(rs.getString("Description"));
                c.setPrice(rs.getDouble("Prix"));
                c.setPhoto(rs.getString("Photo"));
                c.setLinkSite(rs.getString("LienSite"));
                c.setPriorite(StatutPriorite.valueOf(rs.getString("Priorite")));

                return c;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur loadCadeau()", e);
        }
    }

    private List<Personne> loadPersonnes(int resId) {

        final String SQL = """
            SELECT p.* 
            FROM Personne p
            JOIN Personne_Reservation pr ON pr.PersonneId = p.Id
            WHERE pr.ReservationId = ?
        """;

        List<Personne> list = new ArrayList<>();

        try (PreparedStatement st = connect.prepareStatement(SQL)) {

            st.setInt(1, resId);

            try (ResultSet rs = st.executeQuery()) {

                while (rs.next()) {

                    Personne p = new Personne(); 

                    p.setId(rs.getInt("Id"));
                    p.setName(rs.getString("Nom"));
                    p.setFirstName(rs.getString("Prenom"));
                    p.setAge(rs.getInt("Age"));
                    p.setStreet(rs.getString("Rue"));
                    p.setCity(rs.getString("Ville"));
                    p.setStreetNumber(rs.getString("Numero"));
                    p.setPostalCode(rs.getInt("CodePostal"));
                    p.setEmail(rs.getString("Email"));
                    p.setPassword(rs.getString("MotDePasse"));

                    list.add(p);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur loadPersonnes()", e);
        }

        return list;
    }
}
