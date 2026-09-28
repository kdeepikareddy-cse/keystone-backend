 package com.keystone.keystone.repository;

import com.keystone.keystone.model.WorkOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long> {

    List<WorkOrder> findByTechnicianId(Long technicianId);

    List<WorkOrder> findByCustomerId(Long customerId);
}