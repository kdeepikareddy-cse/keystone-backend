 package com.keystone.keystone.controller;

import com.keystone.keystone.model.Technician;
import com.keystone.keystone.service.TechnicianService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/technicians")
public class TechnicianController {


private final TechnicianService technicianService;

public TechnicianController(TechnicianService technicianService) {
    this.technicianService = technicianService;
}

// Create technician
@PostMapping
public ResponseEntity<Technician> createTechnician(
        @RequestBody Technician technician) {

    return ResponseEntity.ok(
            technicianService.createTechnician(technician)
    );
}

// Get all technicians
@GetMapping
public ResponseEntity<List<Technician>> getAllTechnicians() {

    return ResponseEntity.ok(
            technicianService.getAllTechnicians()
    );
}

// Get technician by ID
@GetMapping("/{id}")
public ResponseEntity<Technician> getTechnicianById(
        @PathVariable Long id) {

    return technicianService.getTechnicianById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
}

// Update technician
@PutMapping("/{id}")
public ResponseEntity<Technician> updateTechnician(
        @PathVariable Long id,
        @RequestBody Technician technician) {

    Technician updated =
            technicianService.updateTechnician(id, technician);

    if (updated == null) {
        return ResponseEntity.notFound().build();
    }

    return ResponseEntity.ok(updated);
}

// Delete technician
@DeleteMapping("/{id}")
public ResponseEntity<String> deleteTechnician(
        @PathVariable Long id) {

    if (!technicianService.deleteTechnician(id)) {
        return ResponseEntity.notFound().build();
    }

    return ResponseEntity.ok(
            "Technician deleted successfully"
    );
}

// Update technician status
@PutMapping("/{id}/status")
public ResponseEntity<Technician> updateStatus(
        @PathVariable Long id,
        @RequestParam String status) {

    Technician updated =
            technicianService.updateStatus(id, status);

    if (updated == null) {
        return ResponseEntity.notFound().build();
    }

    return ResponseEntity.ok(updated);
}


}
