package com.med.main.controllers;

import com.med.main.models.Patient;
import com.med.main.repo.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientRepository patientRepository;

    @GetMapping
    public List<Patient> list() {
        return patientRepository.findAll();
    }

    @PostMapping
    public Patient create(@RequestBody Patient p) {
        return patientRepository.save(p);
    }

    @GetMapping("/{id}")
    public Patient getOne(@PathVariable Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
    }
}