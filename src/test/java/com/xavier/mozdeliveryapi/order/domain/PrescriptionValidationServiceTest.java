package com.xavier.mozdeliveryapi.order.domain;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PrescriptionValidationServiceTest {
    
    private PrescriptionValidationService prescriptionService;
    
    @BeforeEach
    void setUp() {
        prescriptionService = new PrescriptionValidationServiceImpl();
    }
    
    @Test
    void shouldValidateValidPrescription() {
        // Given
        Prescription prescription = createValidPrescription();
        
        // When
        ValidationResult result = prescriptionService.validatePrescription(prescription);
        
        // Then
        assertThat(result.isValid()).isTrue();
    }
    
    @Test
    void shouldRejectExpiredPrescription() {
        // Given
        Prescription expiredPrescription = new Prescription(
            "RX-123",
            "Dr. Smith",
            "DR123456",
            "John Doe",
            "ID123",
            LocalDate.now().minusMonths(2),
            LocalDate.now().minusDays(1), // Expired yesterday
            "Medication",
            "10mg",
            30,
            "Take once daily"
        );
        
        // When
        ValidationResult result = prescriptionService.validatePrescription(expiredPrescription);
        
        // Then
        assertThat(result.isValid()).isFalse();
        assertThat(result.errors()).hasSize(1);
        assertThat(result.errors().get(0).errorCode()).isEqualTo("PRESCRIPTION_EXPIRED");
    }
    
    @Test
    void shouldRejectInvalidDoctorLicense() {
        // Given
        Prescription invalidLicensePrescription = new Prescription(
            "RX-123",
            "Dr. Smith",
            "INVALID", // Invalid license
            "John Doe",
            "ID123",
            LocalDate.now().minusDays(1),
            LocalDate.now().plusDays(30),
            "Medication",
            "10mg",
            30,
            "Take once daily"
        );
        
        // When
        ValidationResult result = prescriptionService.validatePrescription(invalidLicensePrescription);
        
        // Then
        assertThat(result.isValid()).isFalse();
        assertThat(result.errors()).hasSize(1);
        assertThat(result.errors().get(0).errorCode()).isEqualTo("INVALID_DOCTOR_LICENSE");
    }
    
    @Test
    void shouldRejectExcessiveQuantity() {
        // Given
        Prescription excessiveQuantityPrescription = new Prescription(
            "RX-123",
            "Dr. Smith",
            "DR123456",
            "John Doe",
            "ID123",
            LocalDate.now().minusDays(1),
            LocalDate.now().plusDays(30),
            "Medication",
            "10mg",
            200, // Excessive quantity
            "Take once daily"
        );
        
        // When
        ValidationResult result = prescriptionService.validatePrescription(excessiveQuantityPrescription);
        
        // Then
        assertThat(result.isValid()).isFalse();
        assertThat(result.errors()).hasSize(1);
        assertThat(result.errors().get(0).errorCode()).isEqualTo("QUANTITY_EXCEEDS_LIMIT");
    }
    
    @Test
    void shouldRejectOldPrescription() {
        // Given
        Prescription oldPrescription = new Prescription(
            "RX-123",
            "Dr. Smith",
            "DR123456",
            "John Doe",
            "ID123",
            LocalDate.now().minusMonths(8), // Too old
            LocalDate.now().plusDays(30),
            "Medication",
            "10mg",
            30,
            "Take once daily"
        );
        
        // When
        ValidationResult result = prescriptionService.validatePrescription(oldPrescription);
        
        // Then
        assertThat(result.isValid()).isFalse();
        assertThat(result.errors()).hasSize(1);
        assertThat(result.errors().get(0).errorCode()).isEqualTo("PRESCRIPTION_TOO_OLD");
    }
    
    @Test
    void shouldValidatePrescriptionForMedication() {
        // Given
        Prescription prescription = createValidPrescription();
        
        // When & Then
        assertThat(prescriptionService.isPrescriptionValidForMedication(prescription, "Medication")).isTrue();
        assertThat(prescriptionService.isPrescriptionValidForMedication(prescription, "Other Medicine")).isFalse();
    }
    
    @Test
    void shouldValidateDoctorLicense() {
        // When & Then
        assertThat(prescriptionService.isDoctorLicenseValid("DR123456")).isTrue();
        assertThat(prescriptionService.isDoctorLicenseValid("INVALID")).isFalse();
        assertThat(prescriptionService.isDoctorLicenseValid("DR12")).isFalse(); // Too short
    }
    
    @Test
    void shouldValidateQuantityLimits() {
        // Given
        Prescription validQuantity = createValidPrescription();
        Prescription excessiveQuantity = new Prescription(
            "RX-123", "Dr. Smith", "DR123456", "John Doe", "ID123",
            LocalDate.now().minusDays(1), LocalDate.now().plusDays(30),
            "Medication", "10mg", 200, "Take once daily"
        );
        
        // When & Then
        assertThat(prescriptionService.isQuantityWithinLimits(validQuantity)).isTrue();
        assertThat(prescriptionService.isQuantityWithinLimits(excessiveQuantity)).isFalse();
    }
    
    @Test
    void shouldValidatePrescriptionForPatient() {
        // Given
        Prescription prescription = createValidPrescription();
        
        // When
        ValidationResult validPatient = prescriptionService.validatePrescriptionForPatient(
            prescription, "John Doe", "ID123");
        ValidationResult invalidPatient = prescriptionService.validatePrescriptionForPatient(
            prescription, "Jane Doe", "ID456");
        
        // Then
        assertThat(validPatient.isValid()).isTrue();
        assertThat(invalidPatient.isValid()).isFalse();
        assertThat(invalidPatient.errors().get(0).errorCode()).isEqualTo("PRESCRIPTION_PATIENT_MISMATCH");
    }
    
    private Prescription createValidPrescription() {
        return new Prescription(
            "RX-123",
            "Dr. Smith",
            "DR123456",
            "John Doe",
            "ID123",
            LocalDate.now().minusDays(1),
            LocalDate.now().plusDays(30),
            "Medication",
            "10mg",
            30,
            "Take once daily"
        );
    }
}