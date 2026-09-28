package com.keystone.keystone.service;

import com.keystone.keystone.model.Technician;
import com.keystone.keystone.model.WorkOrder;
import com.keystone.keystone.repository.TechnicianRepository;
import com.keystone.keystone.repository.WorkOrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WorkOrderService {

    private final WorkOrderRepository workOrderRepository;
    private final TechnicianRepository technicianRepository;

    public WorkOrderService(
            WorkOrderRepository workOrderRepository,
            TechnicianRepository technicianRepository) {

        this.workOrderRepository = workOrderRepository;
        this.technicianRepository = technicianRepository;
    }

    // Create a work order
    public WorkOrder createWorkOrder(WorkOrder workOrder) {

        if (workOrder.getStatus() == null ||
                workOrder.getStatus().isBlank()) {
            workOrder.setStatus("NEW");
        }

        if (workOrder.getPriority() == null ||
                workOrder.getPriority().isBlank()) {
            workOrder.setPriority("MEDIUM");
        }

        return workOrderRepository.save(workOrder);
    }

    // Get all work orders
    public List<WorkOrder> getAllWorkOrders() {
        return workOrderRepository.findAll();
    }

    // Get work order by ID
    public Optional<WorkOrder> getWorkOrderById(Long id) {
        return workOrderRepository.findById(id);
    }

    // Update complete work order
    public WorkOrder updateWorkOrder(
            Long id,
            WorkOrder updatedWorkOrder) {

        return workOrderRepository.findById(id)
                .map(workOrder -> {

                    workOrder.setTitle(
                            updatedWorkOrder.getTitle()
                    );

                    workOrder.setDescription(
                            updatedWorkOrder.getDescription()
                    );

                    workOrder.setPriority(
                            updatedWorkOrder.getPriority()
                    );

                    workOrder.setStatus(
                            updatedWorkOrder.getStatus()
                    );

                    workOrder.setLocation(
                            updatedWorkOrder.getLocation()
                    );

                    return workOrderRepository.save(workOrder);
                })
                .orElse(null);
    }

    // Delete work order
    public boolean deleteWorkOrder(Long id) {

        if (!workOrderRepository.existsById(id)) {
            return false;
        }

        workOrderRepository.deleteById(id);
        return true;
    }

    // Update work order status
    public WorkOrder updateStatus(
            Long id,
            String status) {

        return workOrderRepository.findById(id)
                .map(workOrder -> {

                    workOrder.setStatus(status);

                    /*
                     * When a work order is closed,
                     * its assigned technician becomes available.
                     */
                    if ("CLOSED".equalsIgnoreCase(status)
                            && workOrder.getTechnician() != null) {

                        Technician technician =
                                workOrder.getTechnician();

                        technician.setStatus("AVAILABLE");

                        technicianRepository.save(technician);
                    }

                    return workOrderRepository.save(workOrder);
                })
                .orElse(null);
    }

    // Assign technician to work order
    // Previous technician is released if necessary.
    public WorkOrder assignTechnician(
            Long workOrderId,
            Long technicianId) {

        Optional<WorkOrder> workOrderOptional =
                workOrderRepository.findById(workOrderId);

        if (workOrderOptional.isEmpty()) {
            return null;
        }

        Optional<Technician> technicianOptional =
                technicianRepository.findById(technicianId);

        if (technicianOptional.isEmpty()) {
            return null;
        }

        WorkOrder workOrder = workOrderOptional.get();
        Technician newTechnician = technicianOptional.get();

        /*
         * If another technician was already assigned,
         * release that technician first.
         */
        Technician previousTechnician =
                workOrder.getTechnician();

        if (previousTechnician != null
                && !previousTechnician.getId()
                        .equals(newTechnician.getId())) {

            previousTechnician.setStatus("AVAILABLE");

            technicianRepository.save(
                    previousTechnician
            );
        }

        /*
         * Assign the new technician.
         */
        workOrder.setTechnician(newTechnician);

        /*
         * A technician assigned to an active work order
         * becomes BUSY.
         */
        newTechnician.setStatus("BUSY");

        technicianRepository.save(newTechnician);

        /*
         * If the work order is still NEW,
         * move it automatically to IN_PROGRESS.
         */
        if ("NEW".equalsIgnoreCase(
                workOrder.getStatus())) {

            workOrder.setStatus("IN_PROGRESS");
        }

        return workOrderRepository.save(workOrder);
    }

    // Get work orders assigned to a technician
    public List<WorkOrder> getWorkOrdersByTechnician(
            Long technicianId) {

        return workOrderRepository.findByTechnicianId(
                technicianId
        );
    }
    public List<WorkOrder> getWorkOrdersByCustomer(
        Long customerId) {

    return workOrderRepository.findByCustomerId(
            customerId
    );
}
}
