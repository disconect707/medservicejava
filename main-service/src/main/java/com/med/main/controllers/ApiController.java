package com.med.main.controllers;

import com.med.main.repo.AppointmentRepository;
import com.med.main.models.*;
import com.med.main.repo.*;
import com.med.main.services.MinioStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiController {
    private final DoctorRepository doctorRepo;
    private final PatientRepository patientRepo;
    private final AppointmentRepository apptRepo;
    private final MedicalRecordRepository medRepo;
    private final MinioStorageService storageService;

    // Вспомогательный метод для получения ID из заголовков (установленных Gateway)
    private Long getCurrentUserId(String uidHeader) {
        if(uidHeader == null) throw new RuntimeException("Unauthorized");
        return Long.parseLong(uidHeader);
    }

    @GetMapping("/doctors")
    public List<Doctor> getDoctors() {
        return doctorRepo.findAll();
    }

    @GetMapping("/patients")
    public List<Patient> getPatients() {
        return patientRepo.findAll();
    }

    @PostMapping("/appointments")
    public Appointment createAppointment(@RequestBody Appointment appt, @RequestHeader(value="X-User-Id", required=false) String uid) {
        // Логика валидации
        return apptRepo.save(appt);
    }

    @GetMapping("/appointments")
    public List<Appointment> getAppointments() {
        return apptRepo.findAll();
    }

    @PostMapping(value = "/medical_records", consumes = {"multipart/form-data"})
    public MedicalRecord createMedicalRecord(
            @RequestParam("patient_id") Long patientId,
            @RequestParam("doctor_id") Long doctorId,
            @RequestParam("notes") String notes,
            @RequestParam("visit_date") String dateStr,
            @RequestParam(value = "files", required = false) List<MultipartFile> files
    ) {
        MedicalRecord mr = new MedicalRecord();
        mr.setPatientId(patientId);
        mr.setDoctorId(doctorId);
        mr.setNotes(notes);
        mr.setVisitDate(LocalDate.parse(dateStr));

        if (files != null && !files.isEmpty()) {
            List<String> urls = new ArrayList<>();
            for (MultipartFile f : files) {
                urls.add(storageService.uploadFile(f));
            }
            mr.setAttachments(urls);
        }
        return medRepo.save(mr);
    }
}