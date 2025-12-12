package com.med.main.controllers;

import com.med.main.models.Patient;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/patients")
public class PatientController {
    @GetMapping
    public List<Patient> list() { return Collections.emptyList(); }
    @PostMapping
    public Patient create(@RequestBody Patient p) { return p; }
}
