package kigali.clinic.rw.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import kigali.clinic.rw.domain.Doctor;
import kigali.clinic.rw.domain.Specialization;
import kigali.clinic.rw.repository.DoctorRepository;
import kigali.clinic.rw.repository.SpecializationRepository;

@Service 
public class SpecializationService {

    @Autowired 
    private SpecializationRepository specRepo;

    @Autowired 
    private DoctorRepository docRepo;

    // makes sure every doctor sent with the specialization already exists,
    // and swaps them for the matching managed entity; returns null when fine
    private List<Doctor> resolveDoctors(List<Doctor> doctors){

        List<Doctor> resolved = new ArrayList<>();

        if(doctors == null){
            return resolved;
        }

        for(Doctor doctor : doctors){

            if(doctor.getId() == null){
                return null;
            }

            Optional<Doctor> found = docRepo.findById(doctor.getId());
            if(found.isEmpty()){
                return null;
            }
            resolved.add(found.get());
        }
        return resolved;
    }

    public String saveSpecialization(Specialization specialization){

        List<Doctor> resolved = resolveDoctors(specialization.getDoctors());
        if(resolved == null){
            return "Doctor not found";
        }

        specialization.setDoctors(resolved);
        specRepo.save(specialization);
        return "Specialization is saved successfully";
    }

    public List<Specialization> getAllSpecializations(){
        return specRepo.findAll();
    }

    public Specialization getSpecializationById(UUID id){
        return specRepo.findById(id).orElse(null);
    }

    public String updateSpecialization(UUID id, Specialization specialization){

        Optional<Specialization> found = specRepo.findById(id);
        if(found.isEmpty()){
            return "Specialization not found";
        }

        List<Doctor> resolved = resolveDoctors(specialization.getDoctors());
        if(resolved == null){
            return "Doctor not found";
        }

        Specialization existing = found.get();
        existing.setName(specialization.getName());
        existing.setDoctors(resolved);
        specRepo.save(existing);
        return "Specialization is updated successfully";
    }

    public String deleteSpecialization(UUID id){

        if(!specRepo.existsById(id)){
            return "Specialization not found";
        }

        try{
            specRepo.deleteById(id);
        }catch(DataIntegrityViolationException e){
            return "Specialization cannot be deleted because it is in use";
        }
        return "Specialization is deleted successfully";
    }


    // B3
    public List<Specialization> getUnusedSpecializations(){
        return specRepo.findUnusedSpecializations();
    }

}
