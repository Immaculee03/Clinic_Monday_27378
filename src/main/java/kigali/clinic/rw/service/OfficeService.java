package kigali.clinic.rw.service;

import org.springframework.data.domain.PageRequest;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import kigali.clinic.rw.domain.Office;
import kigali.clinic.rw.repository.OfficeRepository;

@Service 
public class OfficeService {

    @Autowired 
    private OfficeRepository offRepo;

    public String saveOffice(Office office){

        if(offRepo.findByOfficeNumber(office.getOfficeNumber()).isPresent()){
            return "Office number already exists";
        }

        offRepo.save(office);
        return "Office is saved successfully";
    }

    public List<Office> getAllOffices(){
        return offRepo.findAll();
    }

    public Office getOfficeById(UUID id){
        return offRepo.findById(id).orElse(null);
    }

    public String updateOffice(UUID id, Office office){

        Optional<Office> found = offRepo.findById(id);
        if(found.isEmpty()){
            return "Office not found";
        }

        Optional<Office> sameNumber = offRepo.findByOfficeNumber(office.getOfficeNumber());
        if(sameNumber.isPresent() && !sameNumber.get().getId().equals(id)){
            return "Office number already exists";
        }

        Office existing = found.get();
        existing.setName(office.getName());
        existing.setOfficeNumber(office.getOfficeNumber());
        offRepo.save(existing);
        return "Office is updated successfully";
    }

    public String deleteOffice(UUID id){

        if(!offRepo.existsById(id)){
            return "Office not found";
        }

        try{
            offRepo.deleteById(id);
        }catch(DataIntegrityViolationException e){
            return "Office cannot be deleted because it is in use";
        }
        return "Office is deleted successfully";
    }


    // C3: empty Optional means there are no appointments yet
    public Optional<Object[]> getBusiestOffice(){

        List<Object[]> rows = offRepo.findBusiestOffice(PageRequest.of(0, 1));
        if(rows.isEmpty()){
            return Optional.empty();
        }
        return Optional.of(rows.get(0));
    }

}
