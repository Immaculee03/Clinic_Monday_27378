package kigali.clinic.rw.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import kigali.clinic.rw.domain.Doctor;
import kigali.clinic.rw.repository.DoctorRepository;
import kigali.clinic.rw.repository.OfficeRepository;

@Service 
public class DoctorService {

    @Autowired 
    private DoctorRepository docRepo;

    @Autowired 
    private OfficeRepository offRepo;

    // checks the office sent with the doctor (if any); returns an error message or null when fine
    private String checkOffice(Doctor doctor, UUID doctorId){

        if(doctor.getOffice() == null){
            return null;
        }

        UUID officeId = doctor.getOffice().getId();
        if(officeId == null || !offRepo.existsById(officeId)){
            return "Office not found";
        }

        Optional<Doctor> owner = docRepo.findByOfficeId(officeId);
        if(owner.isPresent() && !owner.get().getId().equals(doctorId)){
            return "Office is already assigned to another doctor";
        }
        return null;
    }

    public String saveDoctor(Doctor doctor){

        String problem = checkOffice(doctor, null);
        if(problem != null){
            return problem;
        }

        docRepo.save(doctor);
        return "Doctor is saved successfully";
    }

    public List<Doctor> getAllDoctors(){
        return docRepo.findAll();
    }

    public Doctor getDoctorById(UUID id){
        return docRepo.findById(id).orElse(null);
    }

    public String updateDoctor(UUID id, Doctor doctor){

        Optional<Doctor> found = docRepo.findById(id);
        if(found.isEmpty()){
            return "Doctor not found";
        }

        String problem = checkOffice(doctor, id);
        if(problem != null){
            return problem;
        }

        Doctor existing = found.get();
        existing.setFirstName(doctor.getFirstName());
        existing.setLastName(doctor.getLastName());
        existing.setDateOfBirth(doctor.getDateOfBirth());
        existing.setOffice(doctor.getOffice());
        docRepo.save(existing);
        return "Doctor is updated successfully";
    }

    public String deleteDoctor(UUID id){

        if(!docRepo.existsById(id)){
            return "Doctor not found";
        }

        try{
            docRepo.deleteById(id);
        }catch(DataIntegrityViolationException e){
            return "Doctor cannot be deleted because it is in use";
        }
        return "Doctor is deleted successfully";
    }


    // B1
    public List<Doctor> getDoctorsBySpecialization(String name){
        return docRepo.findBySpecializationName(name);
    }

    // B2
    public List<Doctor> getDoctorsWithoutOffice(){
        return docRepo.findDoctorsWithoutOffice();
    }

}
