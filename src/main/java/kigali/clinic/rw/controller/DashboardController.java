package kigali.clinic.rw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import kigali.clinic.rw.service.DashboardService;

@RestController 
@RequestMapping (value="/api/dashboard")
public class DashboardController {

    @Autowired 
    private DashboardService dashServe;

    // D4
    @GetMapping(value = "/summary")
    public ResponseEntity<?> getSummary(){
        return new ResponseEntity<>(dashServe.getSummary(), HttpStatus.OK);
    }

}
