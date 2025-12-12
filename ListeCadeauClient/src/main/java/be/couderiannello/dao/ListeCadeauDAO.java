package be.couderiannello.dao;

import java.util.List;

import be.couderiannello.models.ListeCadeau;

public class ListeCadeauDAO extends RestDAO<ListeCadeau> {

    @Override
    public int create(ListeCadeau obj) {
        // TODO: appel POST 
        return 0;
    }

    @Override
    public boolean delete(ListeCadeau obj) {
        // TODO: appel DELETE
        return false;
    }

    @Override
    public boolean update(ListeCadeau obj) {
        // TODO: appel PUT 
        return false;
    }

    @Override
    public ListeCadeau find(int id) {
        // Par défaut - charger toutes les relations
        return find(id, true, true, true);
    }

    @Override
    public List<ListeCadeau> findAll() {
        // Par défaut - charger toutes les relations
        return findAll(true, true, true);
    }


    public ListeCadeau find(int id, boolean loadCreator, boolean loadInvites, boolean loadCadeaux) {
        // TODO: appel GET
        return null;
    }

    public List<ListeCadeau> findAll(boolean loadCreator, boolean loadInvites, boolean loadCadeaux) {
        // TODO: appel GET
        return null;
    }
}
