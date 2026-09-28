 package com.keystone.keystone.service;

import com.keystone.keystone.model.Technician;
import com.keystone.keystone.repository.TechnicianRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TechnicianService {


private final TechnicianRepository technicianRepository;

public TechnicianService(
        TechnicianRepository technicianRepository) {

    this.technicianRepository = technicianRepository;
}

// Create technician
public Technician createTechnician(Technician technician) {
    return technicianRepository.save(technician);
}

// Get all technicians
public List<Technician> getAllTechnicians() {
    return technicianRepository.findAll();
}

// Get technician by ID
public Optional<Technician> getTechnicianById(Long id) {
    return technicianRepository.findById(id);
}

// Update technician
public Technician updateTechnician(
        Long id,
        Technician updatedTechnician) {

    return technicianRepository.findById(id)
            .map(technician -> {

                technician.setName(
                        updatedTechnician.getName()
                );

                technician.setSkill(
                        updatedTechnician.getSkill()
                );

                technician.setPhone(
                        updatedTechnician.getPhone()
                );

                technician.setStatus(
                        updatedTechnician.getStatus()
                );

                return technicianRepository.save(technician);
            })
            .orElse(null);
}

// Delete technician
public boolean deleteTechnician(Long id) {

    if (!technicianRepository.existsById(id)) {
        return false;
    }

    technicianRepository.deleteById(id);
    return true;
}

// Update technician status
public Technician updateStatus(
        Long id,
        String status) {

    return technicianRepository.findById(id)
            .map(technician -> {

                technician.setStatus(status);

                return technicianRepository.save(technician);
            })
            .orElse(null);
}


}
