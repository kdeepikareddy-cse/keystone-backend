
package com.keystone.keystone.controller;

import com.keystone.keystone.model.WorkOrder;
import com.keystone.keystone.service.WorkOrderService;
import org.springframework.http.ResponseEntity;
import com.keystone.keystone.model.User;
import com.keystone.keystone.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/work-orders")
public class WorkOrderController {

    private final WorkOrderService workOrderService;
private final UserRepository userRepository;

    public WorkOrderController(
        WorkOrderService workOrderService,
        UserRepository userRepository) {

    this.workOrderService = workOrderService;
    this.userRepository = userRepository;
}

    // Create a work order
    @PostMapping
public ResponseEntity<WorkOrder> createWorkOrder(
        @RequestBody WorkOrder workOrder,
        Authentication authentication) {

    User customer = userRepository
            .findByEmail(authentication.getName())
            .orElse(null);

    if (customer != null &&
            "CUSTOMER".equalsIgnoreCase(customer.getRole())) {

        workOrder.setCustomer(customer);
    }

    return ResponseEntity.ok(
            workOrderService.createWorkOrder(workOrder)
    );
}

    // Get all work orders
   @GetMapping
public ResponseEntity<List<WorkOrder>> getAllWorkOrders(
        Authentication authentication) {

    User user = userRepository
            .findByEmail(authentication.getName())
            .orElse(null);

    if (user == null) {
        return ResponseEntity.status(401).build();
    }

    // CUSTOMER → only their own work orders
    if ("CUSTOMER".equalsIgnoreCase(user.getRole())) {

        return ResponseEntity.ok(
                workOrderService.getWorkOrdersByCustomer(
                        user.getId()
                )
        );
    }

    // ADMIN → all work orders
    if ("ADMIN".equalsIgnoreCase(user.getRole())) {

        return ResponseEntity.ok(
                workOrderService.getAllWorkOrders()
        );
    }

    // TECHNICIAN → only assigned work orders
    if ("TECHNICIAN".equalsIgnoreCase(user.getRole())) {

        // Technician accounts are represented separately
        // in the Technician table, so the existing
        // technician filtering in App.tsx remains in place.
        return ResponseEntity.ok(
                workOrderService.getAllWorkOrders()
        );
    }

    return ResponseEntity.status(403).build();
}

    // Get work order by ID
   @PutMapping("/{id}")
public ResponseEntity<WorkOrder> updateWorkOrder(
        @PathVariable Long id,
        @RequestBody WorkOrder workOrder,
        Authentication authentication) {

    User user = userRepository
            .findByEmail(authentication.getName())
            .orElse(null);

    if (user == null) {
        return ResponseEntity.status(401).build();
    }

    if (!"ADMIN".equalsIgnoreCase(user.getRole())) {
        return ResponseEntity.status(403).build();
    }

    WorkOrder updated =
            workOrderService.updateWorkOrder(id, workOrder);

    if (updated == null) {
        return ResponseEntity.notFound().build();
    }

    return ResponseEntity.ok(updated);
}

    // Delete work order
    @DeleteMapping("/{id}")
public ResponseEntity<String> deleteWorkOrder(
        @PathVariable Long id,
        Authentication authentication) {

    User user = userRepository
            .findByEmail(authentication.getName())
            .orElse(null);

    if (user == null) {
        return ResponseEntity.status(401).build();
    }

    if (!"ADMIN".equalsIgnoreCase(user.getRole())) {
        return ResponseEntity.status(403).build();
    }

    if (!workOrderService.deleteWorkOrder(id)) {
        return ResponseEntity.notFound().build();
    }

    return ResponseEntity.ok("Work order deleted successfully");
}

    // Update only the status
    // Frontend sends: { "status": "IN_PROGRESS" }
    @PutMapping("/{id}/status")
public ResponseEntity<WorkOrder> updateStatus(
        @PathVariable Long id,
        @RequestBody StatusRequest request,
        Authentication authentication) {

    User user = userRepository
            .findByEmail(authentication.getName())
            .orElse(null);

    if (user == null) {
        return ResponseEntity.status(401).build();
    }

    String role = user.getRole();

    if (!"ADMIN".equalsIgnoreCase(role)
            && !"TECHNICIAN".equalsIgnoreCase(role)) {
        return ResponseEntity.status(403).build();
    }

    WorkOrder updated =
            workOrderService.updateStatus(
                    id,
                    request.status
            );

    if (updated == null) {
        return ResponseEntity.notFound().build();
    }

    return ResponseEntity.ok(updated);
}

    // Assign technician to work order
   @PutMapping("/{workOrderId}/assign/{technicianId}")
public ResponseEntity<WorkOrder> assignTechnician(
        @PathVariable Long workOrderId,
        @PathVariable Long technicianId,
        Authentication authentication) {

    User user = userRepository
            .findByEmail(authentication.getName())
            .orElse(null);

    if (user == null) {
        return ResponseEntity.status(401).build();
    }

    if (!"ADMIN".equalsIgnoreCase(user.getRole())) {
        return ResponseEntity.status(403).build();
    }

    WorkOrder updated =
            workOrderService.assignTechnician(
                    workOrderId,
                    technicianId
            );

    if (updated == null) {
        return ResponseEntity.notFound().build();
    }

    return ResponseEntity.ok(updated);
}

    // Get work orders assigned to a technician
    @GetMapping("/technician/{technicianId}")
public ResponseEntity<List<WorkOrder>> getTechnicianWorkOrders(
        @PathVariable Long technicianId,
        Authentication authentication) {

    User user = userRepository
            .findByEmail(authentication.getName())
            .orElse(null);

    if (user == null) {
        return ResponseEntity.status(401).build();
    }

    String role = user.getRole();

    if (!"ADMIN".equalsIgnoreCase(role)
            && !"TECHNICIAN".equalsIgnoreCase(role)) {
        return ResponseEntity.status(403).build();
    }

    return ResponseEntity.ok(
            workOrderService.getWorkOrdersByTechnician(
                    technicianId
            )
    );
}

    // Request body for status update
    public static class StatusRequest {

        public String status;

        public StatusRequest() {
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}


