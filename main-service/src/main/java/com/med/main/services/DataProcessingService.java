package com.med.main.services;

import com.med.main.models.Patient;
import com.med.main.repo.AppointmentRepository;
import com.med.main.repo.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.Serializable;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataProcessingService {

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;

    // DTO для результата (должен быть Serializable для Redis)
    public record PatientSummary(String fullName, int totalAppointments, String riskLevel) implements Serializable {}

    @Cacheable(value = "patient_summaries", key = "#patientId")
    public PatientSummary processPatientStatistics(Long patientId) {
        log.info("--- НАЧАЛО ТЯЖЕЛОЙ ОБРАБОТКИ ДЛЯ ПАЦИЕНТА ID: {} ---", patientId);

        // Имитация долгой работы
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        int appointmentCount = appointmentRepository.findByPatientId(patientId).size();
        String riskLevel = (appointmentCount > 5) ? "HIGH" : "LOW";

        log.info("--- ОБРАБОТКА ЗАВЕРШЕНА ---");

        return new PatientSummary(
                patient.getFirstName() + " " + patient.getLastName(),
                appointmentCount,
                riskLevel
        );
    }
}