package com.example.demo.controller;

import com.example.demo.model.Prescription;
import com.example.demo.model.PrescriptionDetail;
import com.example.demo.model.Patient;
import com.example.demo.model.Medicine;
import com.example.demo.service.PrescriptionService;
import com.example.demo.service.PatientService;
import com.example.demo.service.MedicineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/prescriptions")
public class PrescriptionController {

    @Autowired
    private PrescriptionService prescriptionService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private MedicineService medicineService;

    @GetMapping
    public String listPrescriptions(Model model) {
        List<Prescription> prescriptions = prescriptionService.getAllPrescriptions();
        model.addAttribute("prescriptions", prescriptions);
        return "prescriptions/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        Prescription prescription = new Prescription();
        prescription.setPrescriptionDate(LocalDate.now());
        model.addAttribute("prescription", prescription);
        model.addAttribute("patients", patientService.getAllPatients());
        model.addAttribute("medicines", medicineService.getAllMedicines());
        return "prescriptions/form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Prescription prescription = prescriptionService.getPrescriptionById(id);
        if (prescription == null) {
            return "redirect:/prescriptions";
        }
        model.addAttribute("prescription", prescription);
        model.addAttribute("patients", patientService.getAllPatients());
        model.addAttribute("medicines", medicineService.getAllMedicines());
        return "prescriptions/form";
    }

    @GetMapping("/view/{id}")
    public String viewPrescription(@PathVariable Long id, Model model) {
        Prescription prescription = prescriptionService.getPrescriptionById(id);
        if (prescription == null) {
            return "redirect:/prescriptions";
        }
        
        List<PrescriptionDetail> details = prescriptionService.getPrescriptionDetails(id);
        prescription.setPrescriptionDetails(details);
        
        model.addAttribute("prescription", prescription);
        return "prescriptions/view";
    }

    @GetMapping("/search")
    public String searchPrescriptions(@RequestParam String patientCode, Model model) {
        List<Prescription> prescriptions = prescriptionService.getPrescriptionsByPatientCode(patientCode);
        model.addAttribute("prescriptions", prescriptions);
        model.addAttribute("searchTerm", patientCode);
        return "prescriptions/list";
    }

    @PostMapping("/save")
    public String savePrescription(@Valid @ModelAttribute Prescription prescription, 
                                 BindingResult result, 
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("patients", patientService.getAllPatients());
            model.addAttribute("medicines", medicineService.getAllMedicines());
            return "prescriptions/form";
        }

        try {
            prescriptionService.savePrescription(prescription);
            redirectAttributes.addFlashAttribute("success", "Prescription saved successfully!");
            return "redirect:/prescriptions";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("patients", patientService.getAllPatients());
            model.addAttribute("medicines", medicineService.getAllMedicines());
            return "prescriptions/form";
        }
    }

    @GetMapping("/delete/{id}")
    public String deletePrescription(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            prescriptionService.deletePrescription(id);
            redirectAttributes.addFlashAttribute("success", "Prescription deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting prescription: " + e.getMessage());
        }
        return "redirect:/prescriptions";
    }

    @PostMapping("/add-detail")
    public String addPrescriptionDetail(@RequestParam Long prescriptionId,
                                       @RequestParam Long medicineId,
                                       @RequestParam Integer quantity,
                                       @RequestParam String dosage,
                                       @RequestParam String instruction,
                                       @RequestParam Double unitPrice,
                                       RedirectAttributes redirectAttributes) {
        try {
            PrescriptionDetail detail = new PrescriptionDetail();
            detail.setPrescription(prescriptionService.getPrescriptionById(prescriptionId));
            detail.setMedicine(medicineService.getMedicineById(medicineId));
            detail.setQuantity(quantity);
            detail.setDosage(dosage);
            detail.setInstruction(instruction);
            detail.setUnitPrice(unitPrice);
            
            prescriptionService.addPrescriptionDetail(detail);
            redirectAttributes.addFlashAttribute("success", "Medicine added to prescription successfully!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/prescriptions/view/" + prescriptionId;
    }
}
