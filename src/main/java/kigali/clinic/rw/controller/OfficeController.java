package kigali.clinic.rw.controller;

import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import kigali.clinic.rw.domain.Office;
import kigali.clinic.rw.service.OfficeService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController 
@RequestMapping (value={"/api/office"})
public class OfficeController {

    @Autowired 
    private OfficeService offServe;
    

    @PostMapping(value = "/save")  
    public ResponseEntity<?> saveOffice(@RequestBody Office office){
        
       String returnedMessage =  offServe.saveOffice(office);

       if(returnedMessage.equals("Office is saved successfully")){
        return new ResponseEntity<>(returnedMessage,HttpStatus.OK);
       }else{
        return new ResponseEntity<>(returnedMessage, HttpStatus.CONFLICT);
       }

    }

    @GetMapping(value = "/all")
    public ResponseEntity<?> getAllOffices(){
        return new ResponseEntity<>(offServe.getAllOffices(), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getOfficeById(@PathVariable UUID id){

        Office office = offServe.getOfficeById(id);

        if(office == null){
            return new ResponseEntity<>("Office not found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(office, HttpStatus.OK);
    }

    @PutMapping(value = "/update/{id}")
    public ResponseEntity<?> updateOffice(@PathVariable UUID id, @RequestBody Office office){

        String returnedMessage = offServe.updateOffice(id, office);

        if(returnedMessage.equals("Office is updated successfully")){
            return new ResponseEntity<>(returnedMessage, HttpStatus.OK);
        }else if(returnedMessage.equals("Office not found")){
            return new ResponseEntity<>(returnedMessage, HttpStatus.NOT_FOUND);
        }else{
            return new ResponseEntity<>(returnedMessage, HttpStatus.CONFLICT);
        }
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<?> deleteOffice(@PathVariable UUID id){

        String returnedMessage = offServe.deleteOffice(id);

        if(returnedMessage.equals("Office is deleted successfully")){
            return new ResponseEntity<>(returnedMessage, HttpStatus.OK);
        }else if(returnedMessage.equals("Office not found")){
            return new ResponseEntity<>(returnedMessage, HttpStatus.NOT_FOUND);
        }else{
            return new ResponseEntity<>(returnedMessage, HttpStatus.CONFLICT);
        }
    }


    // C3
    @GetMapping(value = "/busiest")
    public ResponseEntity<?> getBusiestOffice(){

        Optional<Object[]> busiest = offServe.getBusiestOffice();

        if(busiest.isEmpty()){
            return new ResponseEntity<>("No appointments yet", HttpStatus.OK);
        }
        return new ResponseEntity<>(busiest.get(), HttpStatus.OK);
    }

}
