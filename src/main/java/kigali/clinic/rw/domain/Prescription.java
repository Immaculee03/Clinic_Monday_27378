package kigali.clinic.rw.domain;

import java.time.LocalDate;
import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity 
@Table (name="prescription")
public class Prescription {

    @Id 
    @GeneratedValue (strategy = GenerationType.UUID)
    private UUID id;

    @Column (name="medicine")
    private String medicineName;

    @Column (name="quantity")
    private String quantity;

    @Column (name="Duration_in_days")
    private int durationDays;

    @Column (name="issued_on")
    private LocalDate issuedOn;

    @ManyToOne 
    @JoinColumn (name="appointment_id")
    private Appointment appointment;

    public UUID getId() {
        return id;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public String getDosage() {
        return quantity;
    }

    public void setDosage(String dosage) {
        this.quantity = dosage;
    }

    public int getDurationDays() {
        return durationDays;
    }

    public void setDurationDays(int durationDays) {
        this.durationDays = durationDays;
    }

    public LocalDate getIssuedDate() {
        return issuedOn;
    }

    public void setIssuedDate(LocalDate issuedDate) {
        this.issuedOn = issuedDate;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

}
