package com.example.demo.service;

import com.example.demo.model.Patient;
import com.example.demo.dao.PatientDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PatientService {

    @Autowired
    private PatientDAO patientDAO;

    public List<Patient> getAllPatients() {
        return patientDAO.findAll();
    }

    public Patient getPatientById(Long id) {
        return patientDAO.findById(id);
    }

    public Patient getPatientByPatientCode(String patientCode) {
        return patientDAO.findByPatientCode(patientCode);
    }

    public Patient savePatient(Patient patient) {
        // Validate patient code is unique
        if (patient.getPatientCode() != null) {
            Patient existingPatient = patientDAO.findByPatientCode(patient.getPatientCode());
            if (existingPatient != null && !existingPatient.getId().equals(patient.getId())) {
                throw new IllegalArgumentException("Patient code already exists");
            }
        }
        return patientDAO.save(patient);
    }

    public void updatePatient(Patient patient) {
        patientDAO.update(patient);
    }

    public void deletePatient(Long id) {
        patientDAO.delete(id);
    }
}
