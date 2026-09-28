 package com.keystone.keystone.repository;

import com.keystone.keystone.model.Technician;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TechnicianRepository extends JpaRepository<Technician, Long> {
}