package com.example.demo.controller;

import com.example.demo.model.Patient;
import com.example.demo.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/patients")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @GetMapping
    public String listPatients(Model model) {
        model.addAttribute("patients", patientService.getAllPatients());
        return "patients/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("patient", new Patient());
        return "patients/form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Patient patient = patientService.getPatientById(id);
        if (patient == null) {
            return "redirect:/patients";
        }
        model.addAttribute("patient", patient);
        return "patients/form";
    }

    @PostMapping("/save")
    public String savePatient(@Valid @ModelAttribute Patient patient, 
                            BindingResult result, 
                            Model model,
                            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "patients/form";
        }

        try {
            patientService.savePatient(patient);
            redirectAttributes.addFlashAttribute("success", "Patient saved successfully!");
            return "redirect:/patients";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "patients/form";
        }
    }

    @GetMapping("/delete/{id}")
    public String deletePatient(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            patientService.deletePatient(id);
            redirectAttributes.addFlashAttribute("success", "Patient deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting patient: " + e.getMessage());
        }
        return "redirect:/patients";
    }
}
