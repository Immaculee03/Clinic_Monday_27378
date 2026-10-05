package kigali.clinic.rw.controller;

import java.util.Optional;
import java.util.List;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import kigali.clinic.rw.domain.Patient;
import kigali.clinic.rw.service.PatientService;


@RestController 
@RequestMapping (value={"/api/patient", "/api/patients"})
public class PatientController {

    @Autowired 
    private PatientService patServe;


    @PostMapping(value = "/save")  
    public ResponseEntity<?> savePatient(@RequestBody Patient patient){

        String returnedMessage = patServe.savePatient(patient);

        if(returnedMessage.equals("Patient is saved successfully")){
            return new ResponseEntity<>(returnedMessage, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(returnedMessage, HttpStatus.CONFLICT);
        }
    }

    @GetMapping(value = "/all")
    public ResponseEntity<?> getAllPatients(){
        return new ResponseEntity<>(patServe.getAllPatients(), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getPatientById(@PathVariable UUID id){

        Patient patient = patServe.getPatientById(id);

        if(patient == null){
            return new ResponseEntity<>("Patient not found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(patient, HttpStatus.OK);
    }

    @PutMapping(value = "/update/{id}")
    public ResponseEntity<?> updatePatient(@PathVariable UUID id, @RequestBody Patient patient){

        String returnedMessage = patServe.updatePatient(id, patient);

        if(returnedMessage.equals("Patient is updated successfully")){
            return new ResponseEntity<>(returnedMessage, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(returnedMessage, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<?> deletePatient(@PathVariable UUID id){

        String returnedMessage = patServe.deletePatient(id);

        if(returnedMessage.equals("Patient is deleted successfully")){
            return new ResponseEntity<>(returnedMessage, HttpStatus.OK);
        }else if(returnedMessage.equals("Patient not found")){
            return new ResponseEntity<>(returnedMessage, HttpStatus.NOT_FOUND);
        }else{
            return new ResponseEntity<>(returnedMessage, HttpStatus.CONFLICT);
        }
    }


    // A1
    @GetMapping(value = "/by-last-name")
    public ResponseEntity<?> getPatientsByLastName(@RequestParam String lastName){
        return new ResponseEntity<>(patServe.getPatientsByLastName(lastName), HttpStatus.OK);
    }

    // B4
    @GetMapping(value = "/of-doctor/{doctorId}")
    public ResponseEntity<?> getPatientsOfDoctor(@PathVariable UUID doctorId){

        Optional<List<Patient>> patients = patServe.getPatientsOfDoctor(doctorId);

        if(patients.isEmpty()){
            return new ResponseEntity<>("The doctor with that id does not exist", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(patients.get(), HttpStatus.OK);
    }

    // C2
    @GetMapping(value = "/frequent")
    public ResponseEntity<?> getFrequentPatients(@RequestParam long min){
        return new ResponseEntity<>(patServe.getFrequentPatients(min), HttpStatus.OK);
    }

}
