package kigali.clinic.rw.service;

import java.sql.Date;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;
import kigali.clinic.rw.domain.Appointment;
import kigali.clinic.rw.domain.AppointmentStatus;
import kigali.clinic.rw.domain.Office;
import kigali.clinic.rw.domain.Specialization;
import kigali.clinic.rw.repository.AppointmentRepository;
import kigali.clinic.rw.repository.SpecializationRepository;
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

    @Autowired 
    private SpecializationRepository specRepo;

    @Autowired 
    private AppointmentRepository apptRepo;

    // A1
    @Transactional
    public String addSpecialization(UUID doctorId, UUID specializationId){

        Optional<Doctor> doctor = docRepo.findById(doctorId);
        if(doctor.isEmpty()){
            return "The doctor with that id does not exist";
        }

        Optional<Specialization> spec = specRepo.findById(specializationId);
        if(spec.isEmpty()){
            return "That specialization does not exist";
        }

        if(doctor.get().getSpecializations().contains(spec.get())){
            return "The doctor already has that specialization";
        }

        doctor.get().getSpecializations().add(spec.get());
        docRepo.save(doctor.get());
        return "Specialization added to the doctor";
    }

    // A2
    @Transactional
    public String removeSpecialization(UUID doctorId, UUID specializationId){

        Optional<Doctor> doctor = docRepo.findById(doctorId);
        if(doctor.isEmpty()){
            return "The doctor with that id does not exist";
        }

        Optional<Specialization> spec = specRepo.findById(specializationId);
        if(spec.isEmpty()){
            return "That specialization does not exist";
        }

        if(!doctor.get().getSpecializations().contains(spec.get())){
            return "The doctor does not have that specialization";
        }

        doctor.get().getSpecializations().remove(spec.get());
        docRepo.save(doctor.get());
        return "Specialization removed from the doctor";
    }

    // A4
    public String changeOffice(UUID doctorId, int officeNumber){

        Optional<Doctor> doctor = docRepo.findById(doctorId);
        if(doctor.isEmpty()){
            return "The doctor with that id does not exist";
        }

        Optional<Office> office = offRepo.findByOfficeNumber(officeNumber);
        if(office.isEmpty()){
            return "The office with that office number does not exist";
        }

        if(docRepo.existsByOfficeOfficeNumberAndIdNot(officeNumber, doctorId)){
            return "That office is taken by other doctor";
        }

        doctor.get().setOffice(office.get());
        docRepo.save(doctor.get());
        return "Office changed successfully";
    }

    // D1: empty Optional means the doctor does not exist
    public Optional<List<Appointment>> getAgenda(UUID doctorId, Date date){

        if(!docRepo.existsById(doctorId)){
            return Optional.empty();
        }
        return Optional.of(apptRepo.findByDoctorIdAndAppointmentDateAndStatusNot(doctorId, date, AppointmentStatus.CANCELLED));
    }

    // D3
    public List<Doctor> searchDoctors(String name){
        return docRepo.searchByName(name);
    }

    // Bonus
    public List<Object[]> getWorkload(Date start, Date end){
        return docRepo.findWorkload(AppointmentStatus.COMPLETED, start, end);
    }
}
