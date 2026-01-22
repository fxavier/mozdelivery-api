package com.xavier.mozdeliveryapi.order.domain;

import java.time.LocalDate;
import java.util.Objects;

import com.xavier.mozdeliveryapi.shared.domain.ValueObject;

/**
 * Value object representing a medical prescription.
 */
public record Prescription(
    String prescriptionId,
    String doctorName,
    String doctorLicense,
    String patientName,
    String patientId,
    LocalDate issuedDate,
    LocalDate expiryDate,
    String medicationName,
    String dosage,
    int quantity,
    String instructions
) implements ValueObject {
    
    public Prescription {
        Objects.requireNonNull(prescriptionId, "Prescription ID cannot be null");
        Objects.requireNonNull(doctorName, "Doctor name cannot be null");
        Objects.requireNonNull(doctorLicense, "Doctor license cannot be null");
        Objects.requireNonNull(patientName, "Patient name cannot be null");
        Objects.requireNonNull(patientId, "Patient ID cannot be null");
        Objects.requireNonNull(issuedDate, "Issued date cannot be null");
        Objects.requireNonNull(expiryDate, "Expiry date cannot be null");
        Objects.requireNonNull(medicationName, "Medication name cannot be null");
        Objects.requireNonNull(dosage, "Dosage cannot be null");
        Objects.requireNonNull(instructions, "Instructions cannot be null");
        
        if (prescriptionId.trim().isEmpty()) {
            throw new IllegalArgumentException("Prescription ID cannot be empty");
        }
        
        if (doctorName.trim().isEmpty()) {
            throw new IllegalArgumentException("Doctor name cannot be empty");
        }
        
        if (doctorLicense.trim().isEmpty()) {
            throw new IllegalArgumentException("Doctor license cannot be empty");
        }
        
        if (patientName.trim().isEmpty()) {
            throw new IllegalArgumentException("Patient name cannot be empty");
        }
        
        if (patientId.trim().isEmpty()) {
            throw new IllegalArgumentException("Patient ID cannot be empty");
        }
        
        if (medicationName.trim().isEmpty()) {
            throw new IllegalArgumentException("Medication name cannot be empty");
        }
        
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        
        if (issuedDate.isAfter(expiryDate)) {
            throw new IllegalArgumentException("Issued date cannot be after expiry date");
        }
    }
    
    /**
     * Check if the prescription is valid for the given date.
     */
    public boolean isValidOn(LocalDate date) {
        return !date.isBefore(issuedDate) && !date.isAfter(expiryDate);
    }
    
    /**
     * Check if the prescription is currently valid.
     */
    public boolean isCurrentlyValid() {
        return isValidOn(LocalDate.now());
    }
    
    /**
     * Check if the prescription matches the given patient.
     */
    public boolean isForPatient(String patientName, String patientId) {
        return this.patientName.equalsIgnoreCase(patientName) && 
               this.patientId.equals(patientId);
    }
}