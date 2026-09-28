 package com.keystone.keystone.model;

import jakarta.persistence.*;

@Entity
@Table(name = "technicians")
public class Technician {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String skill;

    private String status;

    private String phone;

    public Technician() {
    }

    public Technician(String name, String skill, String status, String phone) {
        this.name = name;
        this.skill = skill;
        this.status = status;
        this.phone = phone;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @PrePersist
    public void onCreate() {
        if (status == null) {
            status = "AVAILABLE";
        }
    }
}