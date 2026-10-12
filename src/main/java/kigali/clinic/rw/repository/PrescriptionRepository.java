package kigali.clinic.rw.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import kigali.clinic.rw.domain.Prescription;

@Repository 
public interface PrescriptionRepository extends JpaRepository<Prescription,UUID> {

    // C2 (DERIVED): prescriptions of one appointment
    List<Prescription> findByAppointmentId(UUID appointmentId);

    // C3 (JPQL, path navigation p.appointment.patient): history of a patient, newest first
    @Query("SELECT p FROM Prescription p WHERE p.appointment.patient.ssNumber = :ss ORDER BY p.issuedDate DESC")
    List<Prescription> findHistoryOfPatient(@Param("ss") String ssNumber);

    // C4 (DERIVED, existsBy): does this appointment have prescriptions?
    boolean existsByAppointmentId(UUID appointmentId);

    // Bonus (JPQL, GROUP BY + COUNT + ORDER BY): most prescribed medicines
    @Query("SELECT p.medicineName, COUNT(p) FROM Prescription p GROUP BY p.medicineName ORDER BY COUNT(p) DESC")
    List<Object[]> findTopMedicines();

}
