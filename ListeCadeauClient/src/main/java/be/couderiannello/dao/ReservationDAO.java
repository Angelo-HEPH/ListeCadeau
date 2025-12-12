package be.couderiannello.dao;

import java.util.List;
import be.couderiannello.models.Reservation;

public class ReservationDAO extends RestDAO<Reservation> {

    @Override
    public int create(Reservation obj) {
        // TODO: POST /reservation
        return 0;
    }

    @Override
    public boolean delete(Reservation obj) {
        // TODO: DELETE /reservation/{id}
        return false;
    }

    @Override
    public boolean update(Reservation obj) {
        // TODO: PUT /reservation/{id}
        return false;
    }

    @Override
    public Reservation find(int id) {
        // Par défaut : charger tout
        return find(id, true, true);
    }

    @Override
    public List<Reservation> findAll() {
        // Par défaut : charger tout
        return findAll(true, true);
    }

    // ============================
    // AVEC PARAMÈTRES DE CHARGEMENT
    // ============================

    public Reservation find(int id, boolean loadCadeau, boolean loadPersonnes) {
        // TODO: GET /reservation/{id}?loadCadeau=x&loadPersonnes=y
        return null;
    }

    public List<Reservation> findAll(boolean loadCadeau, boolean loadPersonnes) {
        // TODO: GET /reservation?loadCadeau=x&loadPersonnes=y
        return null;
    }
}
