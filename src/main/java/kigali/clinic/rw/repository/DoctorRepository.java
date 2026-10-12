package kigali.clinic.rw.repository;

import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import java.sql.Date;
import java.util.List;
import kigali.clinic.rw.domain.AppointmentStatus;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import kigali.clinic.rw.domain.Doctor;

@Repository 
public interface DoctorRepository extends JpaRepository<Doctor,UUID> {

    Optional<Doctor> findByOfficeId(UUID officeId);


    // A4 (DERIVED, existsBy): is this office number used by a doctor other than :id?
    boolean existsByOfficeOfficeNumberAndIdNot(int officeNumber, UUID id);

    // D3 (JPQL, LIKE + LOWER + OR)
    @Query("SELECT d FROM Doctor d WHERE LOWER(d.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR LOWER(d.lastName) LIKE LOWER(CONCAT('%', :name, '%')) ORDER BY d.lastName")
    List<Doctor> searchByName(@Param("name") String name);

    // Bonus (JPQL, JOIN + BETWEEN + GROUP BY): COMPLETED appointments per doctor
    @Query("SELECT d.firstName, d.lastName, COUNT(a) FROM Appointment a JOIN a.doctor d WHERE a.status = :status AND a.appointmentDate BETWEEN :start AND :end GROUP BY d.id, d.firstName, d.lastName ORDER BY COUNT(a) DESC")
    List<Object[]> findWorkload(@Param("status") AppointmentStatus status, @Param("start") Date start, @Param("end") Date end);

    // B1 (JPQL, JOIN on collection + LOWER)
    @Query("SELECT d FROM Doctor d JOIN d.specializations s WHERE LOWER(s.name) = LOWER(:name)")
    List<Doctor> findBySpecializationName(@Param("name") String name);

    // B2 (JPQL, IS NULL)
    @Query("SELECT d FROM Doctor d WHERE d.office IS NULL ORDER BY d.lastName")
    List<Doctor> findDoctorsWithoutOffice();

}
