package be.couderiannello.dao;

import java.util.List;

import be.couderiannello.models.Notification;

public class NotificationDAO extends RestDAO<Notification> {

    @Override
    public int create(Notification obj) {
        // TODO: appeler POST 
        return 0;
    }

    @Override
    public boolean delete(Notification obj) {
        // TODO: appeler DELETE 
        return false;
    }

    @Override
    public boolean update(Notification obj) {
        // TODO: appeler PUT 
        return false;
    }

    @Override
    public Notification find(int id) {
        // Par défaut - charger la personne
        return find(id, true);
    }

    @Override
    public List<Notification> findAll() {
        // Par défaut - charger la personne
        return findAll(true);
    }

    public Notification find(int id, boolean loadPersonne) {
        // TODO: GET
        return null;
    }

    public List<Notification> findAll(boolean loadPersonne) {
        // TODO: GET
        return null;
    }
}
