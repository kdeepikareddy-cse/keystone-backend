package com.keystone.keystone.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import com.keystone.keystone.model.User;

@Entity
@Table(name = "work_orders")
public class WorkOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    private String priority;

    private String status;

    private String location;

    private LocalDateTime createdAt;

    // SLA due date/time
    private LocalDateTime slaDueAt;

    // Technician assigned to this work order
    @ManyToOne
    @JoinColumn(name = "technician_id")
    private Technician technician;
@ManyToOne
@JoinColumn(name = "customer_id")
private User customer;

    public WorkOrder() {
    }

    public WorkOrder(
            String title,
            String description,
            String priority,
            String status,
            String location,
            LocalDateTime createdAt) {

        this.title = title;
        this.description = description;
        this.priority = priority;
        this.status = status;
        this.location = location;
        this.createdAt = createdAt;
    }

    @PrePersist
    public void onCreate() {

        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }

        if (status == null || status.isBlank()) {
            status = "NEW";
        }

        /*
         * Set SLA automatically according to priority.
         *
         * HIGH   = 4 hours
         * MEDIUM = 8 hours
         * LOW    = 24 hours
         */
        if (slaDueAt == null) {

            if ("HIGH".equalsIgnoreCase(priority)) {

                slaDueAt = createdAt.plusHours(4);

            } else if ("LOW".equalsIgnoreCase(priority)) {

                slaDueAt = createdAt.plusHours(24);

            } else {

                slaDueAt = createdAt.plusHours(8);
            }
        }
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getSlaDueAt() {
        return slaDueAt;
    }

    public void setSlaDueAt(LocalDateTime slaDueAt) {
        this.slaDueAt = slaDueAt;
    }

    public Technician getTechnician() {
        return technician;
    }

    public void setTechnician(Technician technician) {
        this.technician = technician;
    }
public User getCustomer() {
    return customer;
}

public void setCustomer(User customer) {
    this.customer = customer;
}
}