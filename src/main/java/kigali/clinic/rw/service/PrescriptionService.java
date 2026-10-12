package kigali.clinic.rw.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import kigali.clinic.rw.domain.Appointment;
import kigali.clinic.rw.domain.AppointmentStatus;
import kigali.clinic.rw.domain.Prescription;
import kigali.clinic.rw.repository.AppointmentRepository;
import kigali.clinic.rw.repository.PrescriptionRepository;

@Service 
public class PrescriptionService {

    @Autowired 
    private PrescriptionRepository presRepo;

    @Autowired 
    private AppointmentRepository apptRepo;

    // C1
    public String savePrescription(UUID appointmentId, Prescription prescription){

        Optional<Appointment> appointment = apptRepo.findById(appointmentId);
        if(appointment.isEmpty()){
            return "The appointment with that id does not exist";
        }

        if(appointment.get().getStatus() != AppointmentStatus.COMPLETED){
            return "Prescriptions are only for COMPLETED appointments";
        }

        prescription.setIssuedDate(LocalDate.now());
        prescription.setAppointment(appointment.get());
        presRepo.save(prescription);
        return "Prescription saved successfully";
    }

    // C2: empty Optional means the appointment does not exist
    public Optional<List<Prescription>> getPrescriptionsOfAppointment(UUID appointmentId){

        if(!apptRepo.existsById(appointmentId)){
            return Optional.empty();
        }
        return Optional.of(presRepo.findByAppointmentId(appointmentId));
    }

    // C3
    public List<Prescription> getHistoryOfPatient(String ssNumber){
        return presRepo.findHistoryOfPatient(ssNumber);
    }

    // C4: only medicineName, dosage and durationDays can change
    public String updatePrescription(UUID id, Prescription prescription){

        Optional<Prescription> found = presRepo.findById(id);
        if(found.isEmpty()){
            return "The prescription with that id does not exist";
        }

        Prescription existing = found.get();
        existing.setMedicineName(prescription.getMedicineName());
        existing.setDosage(prescription.getDosage());
        existing.setDurationDays(prescription.getDurationDays());
        presRepo.save(existing);
        return "Prescription updated successfully";
    }

    // C4
    public String deletePrescription(UUID id){

        if(!presRepo.existsById(id)){
            return "The prescription with that id does not exist";
        }

        presRepo.deleteById(id);
        return "Prescription deleted successfully";
    }

    // Bonus
    public List<Object[]> getTopMedicines(){
        return presRepo.findTopMedicines();
    }

}
