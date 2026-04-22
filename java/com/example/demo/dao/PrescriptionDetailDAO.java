package com.example.demo.dao;

import com.example.demo.model.PrescriptionDetail;
import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.List;

@Repository
public class PrescriptionDetailDAO {

    @Autowired
    private SessionFactory sessionFactory;

    private Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    public List<PrescriptionDetail> findByPrescriptionId(Long prescriptionId) {
        Session session = getCurrentSession();
        String hql = "FROM PrescriptionDetail pd WHERE pd.prescription.id = :prescriptionId";
        return session.createQuery(hql, PrescriptionDetail.class)
                .setParameter("prescriptionId", prescriptionId)
                .getResultList();
    }

    public PrescriptionDetail findById(Long id) {
        return getCurrentSession().get(PrescriptionDetail.class, id);
    }

    public void save(PrescriptionDetail prescriptionDetail) {
        getCurrentSession().saveOrUpdate(prescriptionDetail);
    }

    public void update(PrescriptionDetail prescriptionDetail) {
        getCurrentSession().update(prescriptionDetail);
    }

    public void delete(Long id) {
        PrescriptionDetail prescriptionDetail = findById(id);
        if (prescriptionDetail != null) {
            getCurrentSession().delete(prescriptionDetail);
        }
    }

    public void deleteByPrescriptionId(Long prescriptionId) {
        Session session = getCurrentSession();
        String hql = "DELETE FROM PrescriptionDetail pd WHERE pd.prescription.id = :prescriptionId";
        session.createQuery(hql)
                .setParameter("prescriptionId", prescriptionId)
                .executeUpdate();
    }
}
