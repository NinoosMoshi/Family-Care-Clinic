package com.ninos.patient.repo;

import com.ninos.patient.entity.Patient;
import com.ninos.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface PatientRepo extends JpaRepository<Patient, Long> {

    Optional<Patient> findByUser(User user);

}
