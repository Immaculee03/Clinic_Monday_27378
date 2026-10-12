package kigali.clinic.rw.service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import kigali.clinic.rw.domain.AppointmentStatus;
import kigali.clinic.rw.repository.AppointmentRepository;
import kigali.clinic.rw.repository.DoctorRepository;
import kigali.clinic.rw.repository.OfficeRepository;
import kigali.clinic.rw.repository.PatientRepository;
import kigali.clinic.rw.repository.PrescriptionRepository;

@Service 
public class DashboardService {

    @Autowired 
    private PatientRepository patRepo;

    @Autowired 
    private DoctorRepository docRepo;

    @Autowired 
    private OfficeRepository offRepo;

    @Autowired 
    private AppointmentRepository apptRepo;

    @Autowired 
    private PrescriptionRepository presRepo;

    // D4: LinkedHashMap keeps the keys in the order we put them
    public Map<String, Long> getSummary(){

        Map<String, Long> summary = new LinkedHashMap<>();

        Date today = Date.valueOf(LocalDate.now());

        summary.put("patients", patRepo.count());
        summary.put("doctors", docRepo.count());
        summary.put("freeOffices", (long) offRepo.findFreeOffices().size());
        summary.put("appointmentsToday", apptRepo.countByAppointmentDate(today));
        summary.put("scheduled", apptRepo.countByStatus(AppointmentStatus.SCHEDULED));
        summary.put("prescriptions", presRepo.count());

        return summary;
    }

}
