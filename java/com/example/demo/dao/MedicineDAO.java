package com.example.demo.dao;

import com.example.demo.model.Medicine;
import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.List;

@Repository
public class MedicineDAO {

    @Autowired
    private SessionFactory sessionFactory;

    private Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    public List<Medicine> findAll() {
        Session session = getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Medicine> cq = cb.createQuery(Medicine.class);
        Root<Medicine> root = cq.from(Medicine.class);
        cq.select(root);
        return session.createQuery(cq).getResultList();
    }

    public Medicine findById(Long id) {
        return getCurrentSession().get(Medicine.class, id);
    }

    public Medicine findByName(String name) {
        Session session = getCurrentSession();
        String hql = "FROM Medicine m WHERE m.name = :name";
        try {
            return session.createQuery(hql, Medicine.class)
                    .setParameter("name", name)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public void save(Medicine medicine) {
        getCurrentSession().saveOrUpdate(medicine);
    }

    public void update(Medicine medicine) {
        getCurrentSession().update(medicine);
    }

    public void delete(Long id) {
        Medicine medicine = findById(id);
        if (medicine != null) {
            getCurrentSession().delete(medicine);
        }
    }
}
