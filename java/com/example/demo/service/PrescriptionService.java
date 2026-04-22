package com.example.demo.service;

import com.example.demo.model.Prescription;
import com.example.demo.model.PrescriptionDetail;
import com.example.demo.model.Patient;
import com.example.demo.dao.PrescriptionDAO;
import com.example.demo.dao.PrescriptionDetailDAO;
import com.example.demo.dao.PatientDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PrescriptionService {

    @Autowired
    private PrescriptionDAO prescriptionDAO;

    @Autowired
    private PrescriptionDetailDAO prescriptionDetailDAO;

    @Autowired
    private PatientDAO patientDAO;

    public List<Prescription> getAllPrescriptions() {
        return prescriptionDAO.findAll();
    }

    public Prescription getPrescriptionById(Long id) {
        return prescriptionDAO.findById(id);
    }

    public List<Prescription> getPrescriptionsByPatientCode(String patientCode) {
        return prescriptionDAO.findByPatientCode(patientCode);
    }

    public Prescription savePrescription(Prescription prescription) {
        // Validate patient exists
        if (prescription.getPatient() == null || prescription.getPatient().getId() == null) {
            throw new IllegalArgumentException("Patient is required");
        }

        Patient patient = patientDAO.findById(prescription.getPatient().getId());
        if (patient == null) {
            throw new IllegalArgumentException("Patient not found");
        }

        // Calculate total amount from prescription details
        if (prescription.getPrescriptionDetails() != null) {
            double totalAmount = 0.0;
            for (PrescriptionDetail detail : prescription.getPrescriptionDetails()) {
                // Validate quantity is not negative
                if (detail.getQuantity() != null && detail.getQuantity() < 0) {
                    throw new IllegalArgumentException("Medicine quantity cannot be negative");
                }
                
                // Calculate total price for this detail
                if (detail.getUnitPrice() != null && detail.getQuantity() != null) {
                    detail.setTotalPrice(detail.getUnitPrice() * detail.getQuantity());
                    totalAmount += detail.getTotalPrice();
                }
                
                // Set prescription reference
                detail.setPrescription(prescription);
            }
            prescription.setTotalAmount(totalAmount);
        }

        return prescriptionDAO.save(prescription);
    }

    public void updatePrescription(Prescription prescription) {
        prescriptionDAO.update(prescription);
    }

    public void deletePrescription(Long id) {
        // First delete all prescription details
        prescriptionDetailDAO.deleteByPrescriptionId(id);
        // Then delete the prescription
        prescriptionDAO.delete(id);
    }

    public List<PrescriptionDetail> getPrescriptionDetails(Long prescriptionId) {
        return prescriptionDetailDAO.findByPrescriptionId(prescriptionId);
    }

    public PrescriptionDetail addPrescriptionDetail(PrescriptionDetail detail) {
        // Validate quantity is not negative
        if (detail.getQuantity() != null && detail.getQuantity() < 0) {
            throw new IllegalArgumentException("Medicine quantity cannot be negative");
        }

        // Calculate total price
        if (detail.getUnitPrice() != null && detail.getQuantity() != null) {
            detail.setTotalPrice(detail.getUnitPrice() * detail.getQuantity());
        }

        return prescriptionDetailDAO.save(detail);
    }
}
