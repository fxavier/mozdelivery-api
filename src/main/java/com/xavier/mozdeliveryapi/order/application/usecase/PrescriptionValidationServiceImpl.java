package com.xavier.mozdeliveryapi.order.application.usecase;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import com.xavier.mozdeliveryapi.order.domain.valueobject.Prescription;
import com.xavier.mozdeliveryapi.order.domain.valueobject.ValidationResult;

/**
 * Implementation of PrescriptionValidationService.
 */
@Service
public class PrescriptionValidationServiceImpl implements PrescriptionValidationService {
    
    @Override
    public ValidationResult validatePrescription(Prescription prescription) {
        Objects.requireNonNull(prescription, "Prescription cannot be null");
        
        List<ValidationResult.ValidationError> errors = new ArrayList<>();
        
        // Check if prescription is currently valid
        if (!prescription.isCurrentlyValid()) {
            errors.add(new ValidationResult.ValidationError(
                "PRESCRIPTION_EXPIRED",
                "Prescription has expired on " + prescription.expiryDate()
            ));
        }
        
        // Check doctor license
        if (!isDoctorLicenseValid(prescription.doctorLicense())) {
            errors.add(new ValidationResult.ValidationError(
                "INVALID_DOCTOR_LICENSE",
                "Doctor license " + prescription.doctorLicense() + " is not valid"
            ));
        }
        
        // Check quantity limits
        if (!isQuantityWithinLimits(prescription)) {
            errors.add(new ValidationResult.ValidationError(
                "QUANTITY_EXCEEDS_LIMIT",
                "Prescribed quantity exceeds allowed limits"
            ));
        }
        
        // Check if prescription is too old
        if (prescription.issuedDate().isBefore(LocalDate.now().minusMonths(6))) {
            errors.add(new ValidationResult.ValidationError(
                "PRESCRIPTION_TOO_OLD",
                "Prescription is older than 6 months"
            ));
        }
        
        return errors.isEmpty() ? ValidationResult.valid() : ValidationResult.invalid(errors);
    }
    
    @Override
    public ValidationResult validatePrescriptions(List<Prescription> prescriptions) {
        Objects.requireNonNull(prescriptions, "Prescriptions cannot be null");
        
        ValidationResult result = ValidationResult.valid();
        
        for (Prescription prescription : prescriptions) {
            ValidationResult prescriptionResult = validatePrescription(prescription);
            result = result.combine(prescriptionResult);
        }
        
        return result;
    }
    
    @Override
    public boolean isPrescriptionValidForMedication(Prescription prescription, String medicationName) {
        Objects.requireNonNull(prescription, "Prescription cannot be null");
        Objects.requireNonNull(medicationName, "Medication name cannot be null");
        
        return prescription.medicationName().equalsIgnoreCase(medicationName.trim()) &&
               prescription.isCurrentlyValid();
    }
    
    @Override
    public boolean isDoctorLicenseValid(String doctorLicense) {
        Objects.requireNonNull(doctorLicense, "Doctor license cannot be null");
        
        // For demonstration, consider licenses starting with "DR" as valid
        // In real implementation, this would check against a medical board database
        return doctorLicense.trim().toUpperCase().startsWith("DR") && 
               doctorLicense.trim().length() >= 6;
    }
    
    @Override
    public boolean isQuantityWithinLimits(Prescription prescription) {
        Objects.requireNonNull(prescription, "Prescription cannot be null");
        
        // For demonstration, limit quantities to reasonable amounts
        // In real implementation, this would check against medication-specific limits
        return prescription.quantity() > 0 && prescription.quantity() <= 100;
    }
    
    @Override
    public ValidationResult validatePrescriptionForPatient(Prescription prescription, String patientName, String patientId) {
        Objects.requireNonNull(prescription, "Prescription cannot be null");
        Objects.requireNonNull(patientName, "Patient name cannot be null");
        Objects.requireNonNull(patientId, "Patient ID cannot be null");
        
        if (!prescription.isForPatient(patientName, patientId)) {
            return ValidationResult.invalid(
                "PRESCRIPTION_PATIENT_MISMATCH",
                "Prescription is not for the specified patient"
            );
        }
        
        return ValidationResult.valid();
    }
}