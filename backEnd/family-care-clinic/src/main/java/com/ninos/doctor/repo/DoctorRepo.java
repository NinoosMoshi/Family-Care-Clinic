package com.ninos.doctor.repo;

import com.ninos.doctor.entity.Doctor;
import com.ninos.enums.Specialization;
import com.ninos.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DoctorRepo extends JpaRepository<Doctor, Long> {

    Optional<Doctor> findByUser(User user);

    List<Doctor> findBySpecialization(Specialization specialization);

}
