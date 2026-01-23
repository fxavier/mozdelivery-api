package com.xavier.mozdeliveryapi.order.application.usecase;

import java.util.List;
import com.xavier.mozdeliveryapi.order.domain.valueobject.Prescription;
import com.xavier.mozdeliveryapi.order.domain.valueobject.ValidationResult;

/**
 * Service for validating medical prescriptions.
 */
public interface PrescriptionValidationService {
    
    /**
     * Validate a prescription against regulatory requirements.
     */
    ValidationResult validatePrescription(Prescription prescription);
    
    /**
     * Validate multiple prescriptions.
     */
    ValidationResult validatePrescriptions(List<Prescription> prescriptions);
    
    /**
     * Check if a prescription is valid for a specific medication.
     */
    boolean isPrescriptionValidForMedication(Prescription prescription, String medicationName);
    
    /**
     * Verify doctor license validity.
     */
    boolean isDoctorLicenseValid(String doctorLicense);
    
    /**
     * Check if prescription quantity is within allowed limits.
     */
    boolean isQuantityWithinLimits(Prescription prescription);
    
    /**
     * Validate prescription against patient information.
     */
    ValidationResult validatePrescriptionForPatient(Prescription prescription, String patientName, String patientId);
}