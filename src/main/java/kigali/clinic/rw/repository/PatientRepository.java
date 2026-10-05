package kigali.clinic.rw.repository;

import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import kigali.clinic.rw.domain.Patient;

@Repository 
public interface PatientRepository extends JpaRepository<Patient,UUID> {


    // A1 (DERIVED): last name ignoring case, first name A to Z
    List<Patient> findByLastNameIgnoreCaseOrderByFirstNameAsc(String lastName);

    // B4 (JPQL, DISTINCT + path navigation): each patient of a doctor listed once
    @Query("SELECT DISTINCT a.patient FROM Appointment a WHERE a.doctor.id = :doctorId")
    List<Patient> findPatientsOfDoctor(@Param("doctorId") UUID doctorId);

    // C2 (JPQL, GROUP BY / HAVING / ORDER BY COUNT): patients with at least :min appointments
    @Query("SELECT p FROM Appointment a JOIN a.patient p GROUP BY p.id HAVING COUNT(a) >= :min ORDER BY COUNT(a) DESC")
    List<Patient> findFrequentPatients(@Param("min") long min);

}
