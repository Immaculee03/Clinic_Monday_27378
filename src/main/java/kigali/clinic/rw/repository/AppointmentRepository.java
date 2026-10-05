package kigali.clinic.rw.repository;

import kigali.clinic.rw.domain.AppointmentStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import java.util.List;
import java.sql.Date;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import kigali.clinic.rw.domain.Appointment;

@Repository 
public interface AppointmentRepository extends JpaRepository<Appointment,UUID> {


    // A2 (DERIVED): by status, earliest date first
    List<Appointment> findByStatusOrderByAppointmentDateAsc(AppointmentStatus status);

    // A3 (DERIVED, Between is inclusive on both ends)
    List<Appointment> findByAppointmentDateBetweenOrderByAppointmentDateAsc(Date start, Date end);

    // A4 (DERIVED, existsBy + And + Not)
    boolean existsByDoctorIdAndAppointmentDateAndStatusNot(UUID doctorId, Date appointmentDate, AppointmentStatus status);

    // C1 (JPQL, GROUP BY / COUNT): one row per status that has appointments
    @Query("SELECT a.status, COUNT(a) FROM Appointment a GROUP BY a.status")
    List<Object[]> countAppointmentsPerStatus();

    // C4 (JPQL UPDATE): cancel everything for a doctor on a date, except COMPLETED ones
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE Appointment a SET a.status = :newStatus WHERE a.doctor.id = :doctorId AND a.appointmentDate = :date AND a.status <> :keepStatus")
    int cancelDayOfDoctor(@Param("doctorId") UUID doctorId, @Param("date") Date date,
                          @Param("newStatus") AppointmentStatus newStatus, @Param("keepStatus") AppointmentStatus keepStatus);

    // Bonus (JPQL DELETE): remove cancelled appointments dated before :date
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("DELETE FROM Appointment a WHERE a.status = :status AND a.appointmentDate < :date")
    int deleteByStatusBefore(@Param("status") AppointmentStatus status, @Param("date") Date date);

}
