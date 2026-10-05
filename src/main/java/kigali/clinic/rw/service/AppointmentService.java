package kigali.clinic.rw.service;

import kigali.clinic.rw.domain.AppointmentStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import java.sql.Date;
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
        existing.setStatus(appointment.getStatus());
        existing.setPatient(appointment.getPatient());
        existing.setDoctor(appointment.getDoctor());
        apptRepo.save(existing);
        return "Appointment is updated successfully";
    }

    public String deleteAppointment(UUID id){

        if(!apptRepo.existsById(id)){
            return "Appointment not found";
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

}
