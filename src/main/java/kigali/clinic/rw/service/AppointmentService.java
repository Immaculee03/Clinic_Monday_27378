package kigali.clinic.rw.service;

import kigali.clinic.rw.domain.AppointmentStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import java.sql.Date;
import java.time.LocalDate;
import kigali.clinic.rw.repository.PrescriptionRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import kigali.clinic.rw.domain.Appointment;
import kigali.clinic.rw.repository.AppointmentRepository;
import kigali.clinic.rw.repository.DoctorRepository;
import kigali.clinic.rw.repository.PatientRepository;

@Service 
public class AppointmentService {

    @Autowired 
    private AppointmentRepository apptRepo;

    @Autowired 
    private PatientRepository patRepo;

    @Autowired 
    private DoctorRepository docRepo;

    // an appointment always needs a real patient and a real doctor; returns an
    // error message when either is missing or doesn't exist, null when fine
    private String checkPatientAndDoctor(Appointment appointment){

        if(appointment.getPatient() == null || appointment.getPatient().getId() == null){
            return "Patient is required";
        }
        if(!patRepo.existsById(appointment.getPatient().getId())){
            return "Patient not found";
        }

        if(appointment.getDoctor() == null || appointment.getDoctor().getId() == null){
            return "Doctor is required";
        }
        if(!docRepo.existsById(appointment.getDoctor().getId())){
            return "Doctor not found";
        }

        return null;
    }

    public String saveAppointment(Appointment appointment){

        String problem = checkPatientAndDoctor(appointment);
        if(problem != null){
            return problem;
        }

        // B1: no booking in the past, and a new appointment always starts as SCHEDULED
        if(appointment.getAppointmentDate() == null){
            return "Appointment date is required";
        }
        if(appointment.getAppointmentDate().toLocalDate().isBefore(LocalDate.now())){
            return "Appointment date cannot be in the past";
        }
        appointment.setStatus(AppointmentStatus.SCHEDULED);

        // A4: a doctor cannot have two appointments on the same date unless the other one is CANCELLED
        if(appointment.getAppointmentDate() != null &&
           apptRepo.existsByDoctorIdAndAppointmentDateAndStatusNot(
                appointment.getDoctor().getId(), appointment.getAppointmentDate(), AppointmentStatus.CANCELLED)){
            return "Doctor is already booked on that date";
        }

        apptRepo.save(appointment);
        return "Appointment is saved successfully";
    }

    public List<Appointment> getAllAppointments(){
        return apptRepo.findAll();
    }

    public Appointment getAppointmentById(UUID id){
        return apptRepo.findById(id).orElse(null);
    }

    public String updateAppointment(UUID id, Appointment appointment){

        Optional<Appointment> found = apptRepo.findById(id);
        if(found.isEmpty()){
            return "Appointment not found";
        }

        String problem = checkPatientAndDoctor(appointment);
        if(problem != null){
            return problem;
        }

        Appointment existing = found.get();
        existing.setAppointmentDate(appointment.getAppointmentDate());
        existing.setReason(appointment.getReason());
        existing.setPatient(appointment.getPatient());
        existing.setDoctor(appointment.getDoctor());
        apptRepo.save(existing);
        return "Appointment is updated successfully";
    }

    public String deleteAppointment(UUID id){

        if(!apptRepo.existsById(id)){
            return "Appointment not found";
        }

        // C4: an appointment that has prescriptions cannot be deleted
        if(presRepo.existsByAppointmentId(id)){
            return "The appointment has prescriptions and cannot be deleted";
        }

        apptRepo.deleteById(id);
        return "Appointment is deleted successfully";
    }


    // A2
    public List<Appointment> getAppointmentsByStatus(AppointmentStatus status){
        return apptRepo.findByStatusOrderByAppointmentDateAsc(status);
    }

    // A3
    public List<Appointment> getAppointmentsBetween(Date start, Date end){
        return apptRepo.findByAppointmentDateBetweenOrderByAppointmentDateAsc(start, end);
    }

    // C1
    public List<Object[]> getStatsByStatus(){
        return apptRepo.countAppointmentsPerStatus();
    }

    // C4
    public String cancelDay(UUID doctorId, Date date){

        if(!docRepo.existsById(doctorId)){
            return "Doctor not found";
        }

        int count = apptRepo.cancelDayOfDoctor(doctorId, date, AppointmentStatus.CANCELLED, AppointmentStatus.COMPLETED);
        return count + " appointments cancelled";
    }

    // Bonus: page by page
    public Page<Appointment> getAppointmentsPage(Pageable pageable){
        return apptRepo.findAll(pageable);
    }

    // Bonus: clean old cancellations
    public String deleteCancelledBefore(Date date){

        int count = apptRepo.deleteByStatusBefore(AppointmentStatus.CANCELLED, date);
        return count + " appointments deleted";
    }

    @Autowired 
    private PrescriptionRepository presRepo;

    // B2: status can only move along the allowed arrows
    public String changeStatus(UUID id, AppointmentStatus newStatus){

        Optional<Appointment> found = apptRepo.findById(id);
        if(found.isEmpty()){
            return "The appointment with that id does not exist";
        }

        Appointment appointment = found.get();
        AppointmentStatus current = appointment.getStatus();

        boolean allowed = false;
        if(current == AppointmentStatus.SCHEDULED && (newStatus == AppointmentStatus.CONFIRMED || newStatus == AppointmentStatus.CANCELLED)){
            allowed = true;
        }
        if(current == AppointmentStatus.CONFIRMED && (newStatus == AppointmentStatus.COMPLETED || newStatus == AppointmentStatus.CANCELLED)){
            allowed = true;
        }

        if(!allowed){
            return "Cannot change status from " + current + " to " + newStatus;
        }

        appointment.setStatus(newStatus);
        apptRepo.save(appointment);
        return "Appointment status updated successfully";
    }

    // B3
    public String rescheduleAppointment(UUID id, Date newDate){

        Optional<Appointment> found = apptRepo.findById(id);
        if(found.isEmpty()){
            return "The appointment with that id does not exist";
        }

        Appointment appointment = found.get();

        if(appointment.getStatus() == AppointmentStatus.COMPLETED){
            return "A COMPLETED appointment cannot be rescheduled";
        }
        if(appointment.getStatus() == AppointmentStatus.CANCELLED){
            return "A CANCELLED appointment cannot be rescheduled";
        }

        if(newDate.toLocalDate().isBefore(LocalDate.now())){
            return "Appointment date cannot be in the past";
        }

        if(apptRepo.existsByDoctorIdAndAppointmentDateAndStatusNot(appointment.getDoctor().getId(), newDate, AppointmentStatus.CANCELLED)){
            return "Doctor is already booked on that date";
        }

        appointment.setAppointmentDate(newDate);
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        apptRepo.save(appointment);
        return "Appointment rescheduled successfully";
    }
}
