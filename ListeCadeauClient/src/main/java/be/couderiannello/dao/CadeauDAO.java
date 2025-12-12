package be.couderiannello.dao;

import java.util.List;

import be.couderiannello.models.Cadeau;

public class CadeauDAO extends RestDAO<Cadeau>{

	@Override
	public int create(Cadeau obj) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean delete(Cadeau obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update(Cadeau obj) {
		// TODO Auto-generated method stub
		return false;
	}

    @Override
    public Cadeau find(int id) {
        // Par défaut - tout charger
        return find(id, true, true);
    }

    @Override
    public List<Cadeau> findAll() {
        // Par défaut - tout charger
        return findAll(true, true);
    }

    public Cadeau find(int id, boolean loadListeCadeau, boolean loadReservations) {
        // TODO: appel GET
        return null;
    }

    public List<Cadeau> findAll(boolean loadListeCadeau, boolean loadReservations) {
        // TODO: appel GET
        return null;
    }
}
