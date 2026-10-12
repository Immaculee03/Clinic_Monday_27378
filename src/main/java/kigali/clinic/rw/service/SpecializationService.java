package kigali.clinic.rw.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import kigali.clinic.rw.domain.Specialization;
import kigali.clinic.rw.repository.SpecializationRepository;

@Service 
public class SpecializationService {

    @Autowired 
    private SpecializationRepository specRepo;

    public String saveSpecialization(Specialization specialization){

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

        Specialization existing = found.get();
        existing.setName(specialization.getName());
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
