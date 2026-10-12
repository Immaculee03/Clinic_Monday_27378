package kigali.clinic.rw.controller;

import org.springframework.web.bind.annotation.RequestParam;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.web.bind.annotation.PatchMapping;
import kigali.clinic.rw.domain.Appointment;
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
import kigali.clinic.rw.domain.Doctor;
import kigali.clinic.rw.service.DoctorService;


@RestController 
@RequestMapping (value={"/api/doctor", "/api/doctors"})
public class DoctorController {

    @Autowired 
    private DoctorService docServe;


    @PostMapping(value = "/save")  
    public ResponseEntity<?> saveDoctor(@RequestBody Doctor doctor){

        String returnedMessage = docServe.saveDoctor(doctor);

        if(returnedMessage.equals("Doctor is saved successfully")){
            return new ResponseEntity<>(returnedMessage, HttpStatus.OK);
        }else if(returnedMessage.equals("Office not found")){
            return new ResponseEntity<>(returnedMessage, HttpStatus.NOT_FOUND);
        }else{
            return new ResponseEntity<>(returnedMessage, HttpStatus.CONFLICT);
        }
    }

    @GetMapping(value = "/all")
    public ResponseEntity<?> getAllDoctors(){
        return new ResponseEntity<>(docServe.getAllDoctors(), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getDoctorById(@PathVariable UUID id){

        Doctor doctor = docServe.getDoctorById(id);

        if(doctor == null){
            return new ResponseEntity<>("Doctor not found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(doctor, HttpStatus.OK);
    }

    @PutMapping(value = "/update/{id}")
    public ResponseEntity<?> updateDoctor(@PathVariable UUID id, @RequestBody Doctor doctor){

        String returnedMessage = docServe.updateDoctor(id, doctor);

        if(returnedMessage.equals("Doctor is updated successfully")){
            return new ResponseEntity<>(returnedMessage, HttpStatus.OK);
        }else if(returnedMessage.equals("Doctor not found") || returnedMessage.equals("Office not found")){
            return new ResponseEntity<>(returnedMessage, HttpStatus.NOT_FOUND);
        }else{
            return new ResponseEntity<>(returnedMessage, HttpStatus.CONFLICT);
        }
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<?> deleteDoctor(@PathVariable UUID id){

        String returnedMessage = docServe.deleteDoctor(id);

        if(returnedMessage.equals("Doctor is deleted successfully")){
            return new ResponseEntity<>(returnedMessage, HttpStatus.OK);
        }else if(returnedMessage.equals("Doctor not found")){
            return new ResponseEntity<>(returnedMessage, HttpStatus.NOT_FOUND);
        }else{
            return new ResponseEntity<>(returnedMessage, HttpStatus.CONFLICT);
        }
    }


    // B1
    @GetMapping(value = "/by-specialization")
    public ResponseEntity<?> getDoctorsBySpecialization(@RequestParam String name){
        return new ResponseEntity<>(docServe.getDoctorsBySpecialization(name), HttpStatus.OK);
    }

    // B2
    @GetMapping(value = "/without-office")
    public ResponseEntity<?> getDoctorsWithoutOffice(){
        return new ResponseEntity<>(docServe.getDoctorsWithoutOffice(), HttpStatus.OK);
    }

    // A1
    @PutMapping(value = "/{doctorId}/specializations/{specializationId}")
    public ResponseEntity<?> addSpecialization(@PathVariable UUID doctorId, @PathVariable UUID specializationId){

        String returnedMessage = docServe.addSpecialization(doctorId, specializationId);

        if(returnedMessage.equals("Specialization added to the doctor")){
            return new ResponseEntity<>(returnedMessage, HttpStatus.OK);
        }else if(returnedMessage.equals("The doctor with that id does not exist") || returnedMessage.equals("That specialization does not exist")){
            return new ResponseEntity<>(returnedMessage, HttpStatus.NOT_FOUND);
        }else{
            return new ResponseEntity<>(returnedMessage, HttpStatus.CONFLICT);
        }
    }

    // A2
    @DeleteMapping(value = "/{doctorId}/specializations/{specializationId}")
    public ResponseEntity<?> removeSpecialization(@PathVariable UUID doctorId, @PathVariable UUID specializationId){

        String returnedMessage = docServe.removeSpecialization(doctorId, specializationId);

        if(returnedMessage.equals("Specialization removed from the doctor")){
            return new ResponseEntity<>(returnedMessage, HttpStatus.OK);
        }else if(returnedMessage.equals("The doctor with that id does not exist") || returnedMessage.equals("That specialization does not exist")){
            return new ResponseEntity<>(returnedMessage, HttpStatus.NOT_FOUND);
        }else{
            return new ResponseEntity<>(returnedMessage, HttpStatus.CONFLICT);
        }
    }

    // A4
    @PatchMapping(value = "/{doctorId}/office")
    public ResponseEntity<?> changeOffice(@PathVariable UUID doctorId, @RequestParam int officeNumber){

        String returnedMessage = docServe.changeOffice(doctorId, officeNumber);

        if(returnedMessage.equals("Office changed successfully")){
            return new ResponseEntity<>(returnedMessage, HttpStatus.OK);
        }else if(returnedMessage.equals("That office is taken by other doctor")){
            return new ResponseEntity<>(returnedMessage, HttpStatus.CONFLICT);
        }else{
            return new ResponseEntity<>(returnedMessage, HttpStatus.NOT_FOUND);
        }
    }

    // D1
    @GetMapping(value = "/{doctorId}/agenda")
    public ResponseEntity<?> getAgenda(@PathVariable UUID doctorId, @RequestParam String date){

        Optional<List<Appointment>> agenda = docServe.getAgenda(doctorId, Date.valueOf(LocalDate.parse(date)));

        if(agenda.isEmpty()){
            return new ResponseEntity<>("The doctor with that id does not exist", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(agenda.get(), HttpStatus.OK);
    }

    // D3
    @GetMapping(value = "/search")
    public ResponseEntity<?> searchDoctors(@RequestParam String name){
        return new ResponseEntity<>(docServe.searchDoctors(name), HttpStatus.OK);
    }

    // Bonus
    @GetMapping(value = "/workload")
    public ResponseEntity<?> getWorkload(@RequestParam String start, @RequestParam String end){

        Date startDate = Date.valueOf(LocalDate.parse(start));
        Date endDate = Date.valueOf(LocalDate.parse(end));
        return new ResponseEntity<>(docServe.getWorkload(startDate, endDate), HttpStatus.OK);
    }
}
