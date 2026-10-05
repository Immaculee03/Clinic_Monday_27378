package kigali.clinic.rw.controller;

import kigali.clinic.rw.domain.AppointmentStatus;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import java.time.LocalDate;
import java.sql.Date;
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
import kigali.clinic.rw.domain.Appointment;
import kigali.clinic.rw.service.AppointmentService;


@RestController 
@RequestMapping (value={"/api/appointment"})
public class AppointmentController {

    @Autowired 
    private AppointmentService apptServe;


    @PostMapping(value = "/save")  
    public ResponseEntity<?> saveAppointment(@RequestBody Appointment appointment){

        String returnedMessage = apptServe.saveAppointment(appointment);

        if(returnedMessage.equals("Appointment is saved successfully")){
            return new ResponseEntity<>(returnedMessage, HttpStatus.OK);
        }else if(returnedMessage.equals("Patient not found") || returnedMessage.equals("Doctor not found")){
            return new ResponseEntity<>(returnedMessage, HttpStatus.NOT_FOUND);
        }else if(returnedMessage.equals("Doctor is already booked on that date")){
            return new ResponseEntity<>(returnedMessage, HttpStatus.CONFLICT);
        }else{
            return new ResponseEntity<>(returnedMessage, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/all")
    public ResponseEntity<?> getAllAppointments(){
        return new ResponseEntity<>(apptServe.getAllAppointments(), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getAppointmentById(@PathVariable UUID id){

        Appointment appointment = apptServe.getAppointmentById(id);

        if(appointment == null){
            return new ResponseEntity<>("Appointment not found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(appointment, HttpStatus.OK);
    }

    @PutMapping(value = "/update/{id}")
    public ResponseEntity<?> updateAppointment(@PathVariable UUID id, @RequestBody Appointment appointment){

        String returnedMessage = apptServe.updateAppointment(id, appointment);

        if(returnedMessage.equals("Appointment is updated successfully")){
            return new ResponseEntity<>(returnedMessage, HttpStatus.OK);
        }else if(returnedMessage.equals("Appointment not found") || returnedMessage.equals("Patient not found") || returnedMessage.equals("Doctor not found")){
            return new ResponseEntity<>(returnedMessage, HttpStatus.NOT_FOUND);
        }else{
            return new ResponseEntity<>(returnedMessage, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<?> deleteAppointment(@PathVariable UUID id){

        String returnedMessage = apptServe.deleteAppointment(id);

        if(returnedMessage.equals("Appointment is deleted successfully")){
            return new ResponseEntity<>(returnedMessage, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(returnedMessage, HttpStatus.NOT_FOUND);
        }
    }


    // A2
    @GetMapping(value = "/by-status")
    public ResponseEntity<?> getAppointmentsByStatus(@RequestParam AppointmentStatus status){
        return new ResponseEntity<>(apptServe.getAppointmentsByStatus(status), HttpStatus.OK);
    }

    // A3
    @GetMapping(value = "/between")
    public ResponseEntity<?> getAppointmentsBetween(@RequestParam String start, @RequestParam String end){

        Date startDate = Date.valueOf(LocalDate.parse(start));
        Date endDate = Date.valueOf(LocalDate.parse(end));
        return new ResponseEntity<>(apptServe.getAppointmentsBetween(startDate, endDate), HttpStatus.OK);
    }

    // C1
    @GetMapping(value = "/stats/by-status")
    public ResponseEntity<?> getStatsByStatus(){
        return new ResponseEntity<>(apptServe.getStatsByStatus(), HttpStatus.OK);
    }

    // C4
    @PatchMapping(value = "/cancel-day")
    public ResponseEntity<?> cancelDay(@RequestParam UUID doctorId, @RequestParam String date){

        String returnedMessage = apptServe.cancelDay(doctorId, Date.valueOf(LocalDate.parse(date)));

        if(returnedMessage.equals("Doctor not found")){
            return new ResponseEntity<>(returnedMessage, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(returnedMessage, HttpStatus.OK);
    }

    // Bonus: page=0&size=5&sort=appointmentDate,desc
    @GetMapping(value = "/page")
    public ResponseEntity<?> getAppointmentsPage(@RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "5") int size,
                                                 @RequestParam(defaultValue = "appointmentDate,asc") String sort){

        String[] parts = sort.split(",");
        Sort.Direction direction = parts.length > 1 ? Sort.Direction.fromString(parts[1].trim()) : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, parts[0].trim()));

        Page<Appointment> result = apptServe.getAppointmentsPage(pageable);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // Bonus
    @DeleteMapping(value = "/cancelled-before")
    public ResponseEntity<?> deleteCancelledBefore(@RequestParam String date){

        String returnedMessage = apptServe.deleteCancelledBefore(Date.valueOf(LocalDate.parse(date)));
        return new ResponseEntity<>(returnedMessage, HttpStatus.OK);
    }

}
