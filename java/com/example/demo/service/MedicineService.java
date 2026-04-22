package com.example.demo.service;

import com.example.demo.model.Medicine;
import com.example.demo.dao.MedicineDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MedicineService {

    @Autowired
    private MedicineDAO medicineDAO;

    public List<Medicine> getAllMedicines() {
        return medicineDAO.findAll();
    }

    public Medicine getMedicineById(Long id) {
        return medicineDAO.findById(id);
    }

    public Medicine getMedicineByName(String name) {
        return medicineDAO.findByName(name);
    }

    public Medicine saveMedicine(Medicine medicine) {
        return medicineDAO.save(medicine);
    }

    public void updateMedicine(Medicine medicine) {
        medicineDAO.update(medicine);
    }

    public void deleteMedicine(Long id) {
        medicineDAO.delete(id);
    }
}
