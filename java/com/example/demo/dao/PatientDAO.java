package com.example.demo.dao;

import com.example.demo.model.Patient;
import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.List;

@Repository
public class PatientDAO {

    @Autowired
    private SessionFactory sessionFactory;

    private Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    public List<Patient> findAll() {
        Session session = getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Patient> cq = cb.createQuery(Patient.class);
        Root<Patient> root = cq.from(Patient.class);
        cq.select(root);
        return session.createQuery(cq).getResultList();
    }

    public Patient findById(Long id) {
        return getCurrentSession().get(Patient.class, id);
    }

    public Patient findByPatientCode(String patientCode) {
        Session session = getCurrentSession();
        String hql = "FROM Patient p WHERE p.patientCode = :patientCode";
        try {
            return session.createQuery(hql, Patient.class)
                    .setParameter("patientCode", patientCode)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public void save(Patient patient) {
        getCurrentSession().saveOrUpdate(patient);
    }

    public void update(Patient patient) {
        getCurrentSession().update(patient);
    }

    public void delete(Long id) {
        Patient patient = findById(id);
        if (patient != null) {
            getCurrentSession().delete(patient);
        }
    }
}
