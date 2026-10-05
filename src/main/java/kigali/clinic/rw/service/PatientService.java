package kigali.clinic.rw.service;

import kigali.clinic.rw.repository.DoctorRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import kigali.clinic.rw.domain.Patient;
import kigali.clinic.rw.repository.PatientRepository;

@Service 
public class PatientService {

    @Autowired 
    private PatientRepository patRepo;

    public String savePatient(Patient patient){

        patRepo.save(patient);
        return "Patient is saved successfully";
    }

    public List<Patient> getAllPatients(){
        return patRepo.findAll();
    }

    public Patient getPatientById(UUID id){
        return patRepo.findById(id).orElse(null);
    }

    public String updatePatient(UUID id, Patient patient){

        Optional<Patient> found = patRepo.findById(id);
        if(found.isEmpty()){
            return "Patient not found";
        }

        Patient existing = found.get();
        existing.setFirstName(patient.getFirstName());
        existing.setLastName(patient.getLastName());
        existing.setDateOfBirth(patient.getDateOfBirth());
        patRepo.save(existing);
        return "Patient is updated successfully";
    }

    public String deletePatient(UUID id){

        if(!patRepo.existsById(id)){
            return "Patient not found";
        }

        try{
            patRepo.deleteById(id);
        }catch(DataIntegrityViolationException e){
            return "Patient cannot be deleted because it is in use";
        }
        return "Patient is deleted successfully";
    }


    @Autowired 
    private DoctorRepository docRepo;

    // A1
    public List<Patient> getPatientsByLastName(String lastName){
        return patRepo.findByLastNameIgnoreCaseOrderByFirstNameAsc(lastName);
    }

    // B4: empty Optional means the doctor does not exist
    public Optional<List<Patient>> getPatientsOfDoctor(UUID doctorId){

        if(!docRepo.existsById(doctorId)){
            return Optional.empty();
        }
        return Optional.of(patRepo.findPatientsOfDoctor(doctorId));
    }

    // C2
    public List<Patient> getFrequentPatients(long min){
        return patRepo.findFrequentPatients(min);
    }

}
