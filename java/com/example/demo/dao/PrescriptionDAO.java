package com.example.demo.dao;

import com.example.demo.model.Prescription;
import com.example.demo.model.Patient;
import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Predicate;
import java.util.List;

@Repository
public class PrescriptionDAO {

    @Autowired
    private SessionFactory sessionFactory;

    private Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    public List<Prescription> findAll() {
        Session session = getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Prescription> cq = cb.createQuery(Prescription.class);
        Root<Prescription> root = cq.from(Prescription.class);
        cq.select(root);
        return session.createQuery(cq).getResultList();
    }

    public Prescription findById(Long id) {
        return getCurrentSession().get(Prescription.class, id);
    }

    public List<Prescription> findByPatientCode(String patientCode) {
        Session session = getCurrentSession();
        String hql = "FROM Prescription p WHERE p.patient.patientCode = :patientCode ORDER BY p.prescriptionDate DESC";
        return session.createQuery(hql, Prescription.class)
                .setParameter("patientCode", patientCode)
                .getResultList();
    }

    public List<Prescription> findByPatientId(Long patientId) {
        Session session = getCurrentSession();
        String hql = "FROM Prescription p WHERE p.patient.id = :patientId ORDER BY p.prescriptionDate DESC";
        return session.createQuery(hql, Prescription.class)
                .setParameter("patientId", patientId)
                .getResultList();
    }

    public void save(Prescription prescription) {
        getCurrentSession().saveOrUpdate(prescription);
    }

    public void update(Prescription prescription) {
        getCurrentSession().update(prescription);
    }

    public void delete(Long id) {
        Prescription prescription = findById(id);
        if (prescription != null) {
            getCurrentSession().delete(prescription);
        }
    }
}
